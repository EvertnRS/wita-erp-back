package org.wita.erp.services.transaction.order;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.transaction.dtos.OrderDTO;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.order.OrderItem;
import org.wita.erp.domain.entities.transaction.order.dtos.*;
import org.wita.erp.domain.entities.transaction.order.mappers.OrderMapper;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.payment.customer.CustomerPaymentTypeRepository;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.domain.repositories.transaction.order.OrderRepository;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.order.OrderException;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.payment.customer.observers.CustomerPaymentTypeSoftDeleteObserver;
import org.wita.erp.services.stock.observers.StockCompensationOrderObserver;
import org.wita.erp.services.transaction.observers.TransactionSoftDeleteObserver;
import org.wita.erp.services.transaction.order.observers.*;
import org.wita.erp.services.user.observers.UserSoftDeleteObserver;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final CustomerPaymentTypeRepository customerPaymentTypeRepository;
    private final ProductRepository productRepository;
    private final MovementReasonRepository movementReasonRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<OrderDTO>> getAllOrders(Pageable pageable, String searchTerm) {
        Page<Order> orderPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            orderPage = orderRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        return ResponseEntity.ok(orderPage.map(orderMapper::toDTO));
    }

    @Transactional
    public ResponseEntity<OrderDTO> save(CreateOrderRequestDTO data) {
        if (orderRepository.findByTransactionCode(data.transactionCode()) != null) {
            throw new OrderException("Transaction code already exists", HttpStatus.BAD_REQUEST);
        }

        User seller = userRepository.findById(data.seller())
                .orElseThrow(() -> new UserException("Seller not registered", HttpStatus.NOT_FOUND));

        CustomerPaymentType customerPaymentType = customerPaymentTypeRepository.findById(data.customerPaymentType())
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        if((!customerPaymentType.getAllowsInstallments() || customerPaymentType.getIsImmediate()) && data.installments() != null){
            throw new OrderException("This payment type does not allow installments", HttpStatus.BAD_REQUEST);
        }

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        long uniqueIds = data.products().stream().map(ProductOrderRequestDTO::productId).distinct().count();
        if (uniqueIds != data.products().size()) {
            throw new OrderException("Duplicate products found. Combine quantities.", HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setDescription(data.description());
        order.setDiscount(data.discount());
        order.setSeller(seller);
        order.setTransactionCode(data.transactionCode());
        order.setCustomerPaymentType(customerPaymentType);
        order.setInstallments(data.installments());

        for (ProductOrderRequestDTO itemData : data.products()) {
            Product product = productRepository.findById(itemData.productId())
                    .orElseThrow(() -> new ProductException("Product " + itemData.productId() + " not found", HttpStatus.NOT_FOUND));

            OrderItem orderItem = createOrderItem(product, itemData.quantity());
            BigDecimal itemDiscount = product.calculateItemDiscount(orderItem.getUnitPrice(), itemData.quantity());
            orderItem.calculateTotal(itemDiscount);
            order.addItem(orderItem);
        }

        order.applyOrderDiscount();

        orderRepository.save(order);

        publisher.publishEvent(
                new CreateOrderObserver(order.getId(), movementReason.getId())
        );

        if (data.installments() != null) {
            publisher.publishEvent(
                    new CreateReceivableOrderObserver(order.getId())
            );
        }

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    public ResponseEntity<OrderDTO> update(UUID id, UpdateOrderRequestDTO data) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        if (data.seller() != null) {
            order.setSeller(userRepository.findById(data.seller())
                    .orElseThrow(() -> new UserException("Seller not registered", HttpStatus.NOT_FOUND)));
        }

        if (data.customerPaymentType() != null) {
            CustomerPaymentType customerPaymentType = customerPaymentTypeRepository.findById(data.customerPaymentType())
                    .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

            if((!customerPaymentType.getAllowsInstallments() || customerPaymentType.getIsImmediate()) && order.getInstallments() != null){
                throw new OrderException("This payment type does not allow installments", HttpStatus.BAD_REQUEST);

            }

            order.setCustomerPaymentType(customerPaymentType);
        }

        if (data.transactionCode() != null && !data.transactionCode().equals(order.getTransactionCode())) {
            if (orderRepository.findByTransactionCode(data.transactionCode()) != null) {
                throw new OrderException("Transaction code already exists", HttpStatus.BAD_REQUEST);
            }
            order.setTransactionCode(data.transactionCode());
        }

        if (data.discount() != null) {
            order.setDiscount(data.discount());
            order.applyOrderDiscount();
        }

        orderMapper.updateOrderFromDTO(data, order);

        if(order.getInstallments() != null){
            publisher.publishEvent(new UpdateReceivableOrderObserver(order.getId()));
        }

        orderRepository.save(order);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @Transactional
    public ResponseEntity<OrderDTO> addProductInOrder(UUID orderId, ProductInOrderDTO data) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(data.product().productId())
                .orElseThrow(() -> new ProductException("Product " + data.product().productId() + " not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        OrderItem currentItem;

        Optional<OrderItem> existingItemOpt = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(data.product().productId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            currentItem = existingItemOpt.get();

            int updatedQuantity = currentItem.getQuantity() + data.product().quantity();
            currentItem.setQuantity(updatedQuantity);
        } else {
            currentItem = createOrderItem(product, data.product().quantity());
            order.addItem(currentItem);
        }

        BigDecimal itemDiscount = product.calculateItemDiscount(currentItem.getUnitPrice(), currentItem.getQuantity());
        currentItem.calculateTotal(itemDiscount);

        order.applyOrderDiscount();

        publisher.publishEvent(new AddProductInOrderObserver(order.getId(), movementReason.getId(), data.product()));

        if(order.getInstallments() != null){
            publisher.publishEvent(new UpdateReceivableOrderObserver(order.getId()));
        }

        orderRepository.save(order);

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @Transactional
    public ResponseEntity<OrderDTO> removeProductInOrder(UUID orderId, ProductInOrderDTO data) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(data.product().productId())
                .orElseThrow(() -> new ProductException("Product " + data.product().productId() + " not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        OrderItem currentItem = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(data.product().productId()))
                .findFirst()
                .orElseThrow(() -> new OrderException("Product not found in this order", HttpStatus.NOT_FOUND));

        if (currentItem.getQuantity() < data.product().quantity()) {
            throw new OrderException("Quantity requested for removal exceeds the available quantity.", HttpStatus.BAD_REQUEST);
        }

        else if (currentItem.getQuantity().equals(data.product().quantity())) {
            order.getItems().remove(currentItem);
        } else {
            int newQuantity = currentItem.getQuantity() - data.product().quantity();
            currentItem.setQuantity(newQuantity);

            BigDecimal itemDiscount = product.calculateItemDiscount(currentItem.getUnitPrice(), currentItem.getQuantity());
            currentItem.calculateTotal(itemDiscount);
        }

        BigDecimal itemDiscount = product.calculateItemDiscount(currentItem.getUnitPrice(), currentItem.getQuantity());
        currentItem.calculateTotal(itemDiscount);

        order.applyOrderDiscount();

        publisher.publishEvent(new RemoveProductInOrderObserver(order.getId(), movementReason.getId(), data.product()));

        if(order.getInstallments() != null){
            publisher.publishEvent(new UpdateReceivableOrderObserver(order.getId()));
        }

        orderRepository.save(order);

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    public ResponseEntity<OrderDTO> delete(UUID id, DeleteOrderRequestDTO data) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));
        order.setActive(false);
        orderRepository.save(order);

        this.auditOrderSoftDelete(id, data.reason());
        this.orderCascadeDelete(id);

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    private OrderItem createOrderItem(Product product, int quantity) {
        if ((product.getQuantityInStock() - quantity) <= 0){
            throw new ProductException("Not enough stock for product: " + product.getName(), HttpStatus.BAD_REQUEST);
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(product.getPrice());
        return orderItem;
    }

    @EventListener
    @Async
    public void onStockCompensationOrder(StockCompensationOrderObserver event) {
        orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        this.delete(event.order(), new DeleteOrderRequestDTO("Stock compensation"));
    }

    @EventListener
    @Async
    public void onReceivableCompensationOrder(ReceivableCompensationObserver event) {
        orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        this.delete(event.order(), new DeleteOrderRequestDTO("Receivable compensation"));
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onTransactionSoftDelete(TransactionSoftDeleteObserver event) {
        if(orderRepository.findById(event.transaction()).isPresent()){
            this.delete(event.transaction(), new DeleteOrderRequestDTO(event.reason()));
        }
    }

    @EventListener
    public void onUserSoftDelete(UserSoftDeleteObserver event) {
        List<UUID> orderIds = orderRepository.cascadeDeleteFromUser(event.user());
        if(!orderIds.isEmpty()){
            for (UUID orderId : orderIds) {
                this.auditOrderSoftDelete(orderId, "Cascade delete from user " + event.user());
                this.orderCascadeDelete(orderId);
            }
        }
    }

    @EventListener
    public void onCustomerPaymentTypeSoftDelete(CustomerPaymentTypeSoftDeleteObserver event) {
        List<UUID> orderIds = orderRepository.cascadeDeleteFromCustomerPaymentType(event.customerPaymentType());
        if(!orderIds.isEmpty()){
            for (UUID orderId : orderIds) {
                this.auditOrderSoftDelete(orderId, "Cascade delete from customer payment type " + event.customerPaymentType());
                this.orderCascadeDelete(orderId);
            }
        }
    }

    @Async
    public void auditOrderSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.TRANSACTION.getEntityType(), reason));
    }

    @Async
    public void orderCascadeDelete(UUID id){
        publisher.publishEvent(new OrderSoftDeleteObserver(id));
    }

}

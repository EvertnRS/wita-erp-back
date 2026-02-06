package org.wita.erp.services.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.order.OrderItem;
import org.wita.erp.domain.entities.order.dtos.*;
import org.wita.erp.domain.entities.order.mappers.OrderMapper;
import org.wita.erp.domain.entities.payment.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.order.Order;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.customer.CustomerRepository;
import org.wita.erp.domain.repositories.order.OrderRepository;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.domain.repositories.user.UserRepository;
import org.wita.erp.infra.exceptions.customer.CustomerException;
import org.wita.erp.infra.exceptions.order.OrderException;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.infra.exceptions.product.ProductException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.infra.exceptions.user.UserException;
import org.wita.erp.services.stock.StockCompensationOrderObserver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final ProductRepository productRepository;
    private final MovementReasonRepository movementReasonRepository;
    private final ApplicationEventPublisher publisher;

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

        Customer customer = customerRepository.findById(data.customer())
                .orElseThrow(() -> new CustomerException("Customer not registered", HttpStatus.NOT_FOUND));

        User seller = userRepository.findById(data.seller())
                .orElseThrow(() -> new UserException("Seller not registered", HttpStatus.NOT_FOUND));

        PaymentType paymentType = paymentTypeRepository.findById(data.paymentType())
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));
        if (!(paymentType instanceof CustomerPaymentType)) {
            throw new OrderException("Invalid Payment Type for purchase", HttpStatus.BAD_REQUEST);
        }

        long uniqueIds = data.products().stream().map(ProductOrderRequestDTO::productId).distinct().count();
        if (uniqueIds != data.products().size()) {
            throw new OrderException("Duplicate products found. Combine quantities.", HttpStatus.BAD_REQUEST);
        }

        Order order = new Order();
        order.setDescription(data.description());
        order.setDiscount(data.discount());
        order.setCustomer(customer);
        order.setSeller(seller);
        order.setTransactionCode(data.transactionCode());
        order.setPaymentType(paymentType);


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
                new CreateOrderObserver(order.getId(), data.movementReason())
        );

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @Transactional
    public ResponseEntity<OrderDTO> update(UUID id, UpdateOrderRequestDTO data) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        if (data.customer() != null) {
            order.setCustomer(customerRepository.findById(data.customer())
                    .orElseThrow(() -> new CustomerException("Customer not registered", HttpStatus.NOT_FOUND)));
        }

        if (data.seller() != null) {
            order.setSeller(userRepository.findById(data.seller())
                    .orElseThrow(() -> new UserException("Seller not registered", HttpStatus.NOT_FOUND)));
        }

        if (data.paymentType() != null) {
            PaymentType paymentType = paymentTypeRepository.findById(data.paymentType())
                    .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));
            if (!(paymentType instanceof CustomerPaymentType)) {
                throw new OrderException("Invalid Payment Type for purchase", HttpStatus.BAD_REQUEST);
            }
            order.setPaymentType(paymentType);
        }

        if (data.transactionCode() != null && !data.transactionCode().equals(order.getTransactionCode())) {
            if (orderRepository.findByTransactionCode(data.transactionCode()) != null) {
                throw new OrderException("Transaction code already exists", HttpStatus.BAD_REQUEST);
            }
            order.setTransactionCode(data.transactionCode());
        }

        if (data.description() != null && !data.description().equals(order.getDescription())) {
            order.setDescription(data.description());
        }

        if (data.products() != null) {
            if (data.movementReason() == null) {
                throw new OrderException("Movement Reason is required when updating products", HttpStatus.BAD_REQUEST);
            }

            List<OrderItemChange> stockChanges = new ArrayList<>();


            for (ProductOrderRequestDTO itemData : data.products()) {
                OrderItem item = order.getItems().stream()
                        .filter(i -> i.getProduct().getId().equals(itemData.productId()))
                        .findFirst()
                        .orElseThrow(() -> new OrderException("Product not found in Order", HttpStatus.NOT_FOUND));

                int difference;

                difference = item.getQuantity() - itemData.quantity();

                if (difference != 0) {
                    stockChanges.add(new OrderItemChange(itemData.productId(), difference));
                }

                item.setQuantity(itemData.quantity());
                item.setTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(itemData.quantity())));
            }

            if (!stockChanges.isEmpty()) {
                publisher.publishEvent(new UpdateOrderObserver(order.getId(), data.movementReason(), stockChanges));
            }
        }

        BigDecimal discount = data.discount() != null ? data.discount() : (order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO);
        applyDiscountAndCalculateTotal(order, discount);

        orderRepository.save(order);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @Transactional
    public ResponseEntity<OrderDTO> addProductInOrder(UUID orderId, AddProductInOrderDTO data) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        Product product = productRepository.findById(data.product().productId())
                .orElseThrow(() -> new ProductException("Product " + data.product() + " not found", HttpStatus.NOT_FOUND));

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement Reason not found", HttpStatus.NOT_FOUND));

        Optional<OrderItem> existingItemOpt = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(data.product().productId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            OrderItem existingItem = existingItemOpt.get();

            int updatedQuantity = existingItem.getQuantity() + data.product().quantity();
            existingItem.setQuantity(updatedQuantity);

            BigDecimal itemDiscount = existingItem.getProduct()
                    .calculateItemDiscount(existingItem.getUnitPrice(), updatedQuantity);
            existingItem.calculateTotal(itemDiscount);

        } else {
            OrderItem newItem = createOrderItem(product, data.product().quantity());
            order.addItem(newItem);
        }

        applyDiscountAndCalculateTotal(order, order.getDiscount());

        List<OrderItemChange> stockChanges = new ArrayList<>();
        stockChanges.add(new OrderItemChange(product.getId(), -data.product().quantity()));

        publisher.publishEvent(new UpdateOrderObserver(order.getId(), movementReason.getId(), stockChanges));

        orderRepository.save(order);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    public ResponseEntity<OrderDTO> delete(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));
        order.setActive(false);
        orderRepository.save(order);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    private OrderItem createOrderItem(Product product, int quantity) {
        if ((product.getQuantityInStock() - quantity) <= product.getMinQuantity()) {
            throw new ProductException("Not enough stock for product: " + product.getName(), HttpStatus.BAD_REQUEST);
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(product.getPrice());
        orderItem.setTotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return orderItem;
    }

    private void applyDiscountAndCalculateTotal(Order order, BigDecimal discount) {
        BigDecimal subTotal = order.getItems().stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal safeDiscount = discount != null ? discount : BigDecimal.ZERO;

        if (safeDiscount.compareTo(subTotal) > 0) {
            throw new OrderException("Discount amount greater than order total", HttpStatus.BAD_REQUEST);
        }

        order.setDiscount(safeDiscount);
        order.setValue(subTotal.subtract(safeDiscount));
    }

    @EventListener
    @Async
    public void onStockCompensationOrder(StockCompensationOrderObserver event) {
        orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        this.delete(event.order());
    }
}

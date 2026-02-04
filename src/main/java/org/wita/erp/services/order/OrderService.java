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
import org.wita.erp.domain.entities.order.dtos.CreateOrderRequestDTO;
import org.wita.erp.domain.entities.order.dtos.OrderDTO;
import org.wita.erp.domain.entities.order.dtos.ProductOrderRequestDTO;
import org.wita.erp.domain.entities.order.dtos.UpdateOrderRequestDTO;
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
import org.wita.erp.services.stock.StockCompensationObserver;

import java.math.BigDecimal;
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
        Page<Order> purchasePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            purchasePage = orderRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            purchasePage = orderRepository.findAll(pageable);
        }

        return ResponseEntity.ok(purchasePage.map(orderMapper::toDTO));
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

        MovementReason movementReason = movementReasonRepository.findById(data.movementReason())
                .orElseThrow(() -> new MovementReasonException("Movement reason not found", HttpStatus.NOT_FOUND));

        long uniqueIds = data.products().stream().map(ProductOrderRequestDTO::product).distinct().count();
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
            Product product = productRepository.findById(itemData.product())
                    .orElseThrow(() -> new ProductException("Product " + itemData.product() + " not found", HttpStatus.NOT_FOUND));

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

        /*if (data.products() != null) {
            MovementReason reason = findSaleReason();
            UUID sellerId = data.seller() != null ? data.seller() : order.getSeller().getId();

            for (ProductOrderRequestDTO itemData : data.products()) {
                updateOrderItem(order, itemData, reason, sellerId);
            }
        }*/

        BigDecimal discount = data.discount() != null ? data.discount() : (order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO);
        applyDiscountAndCalculateTotal(order, discount);

        orderRepository.save(order);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @Transactional
    public ResponseEntity<OrderDTO> addProductInOrder(UUID orderId, ProductOrderRequestDTO data) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));
        Product product = productRepository.findById(data.product())
                .orElseThrow(() -> new ProductException("Product " + data.product() + " not found", HttpStatus.NOT_FOUND));

        OrderItem orderItem = createOrderItem(product, data.quantity());
        order.addItem(orderItem);

        applyDiscountAndCalculateTotal(order, order.getDiscount());

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
        if ((product.getQuantityInStock() - quantity) <= product.getMinQuantity()){
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
    public void onStockCompensation(StockCompensationObserver event) {
        orderRepository.findById(event.order())
                .orElseThrow(() -> new OrderException("Order not found", HttpStatus.NOT_FOUND));

        this.delete(event.order());
    }

}

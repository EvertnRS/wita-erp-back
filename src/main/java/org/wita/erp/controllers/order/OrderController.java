package org.wita.erp.controllers.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.order.Order;
import org.wita.erp.domain.entities.order.dtos.*;
import org.wita.erp.services.order.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_READ')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return orderService.getAllOrders(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ORDER_CREATE')")
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody CreateOrderRequestDTO data) {
        return orderService.save(data);
    }

    @PostMapping("/add-item/{orderId}")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
    public ResponseEntity<OrderDTO> addProductInOrder(@PathVariable UUID orderId, @RequestBody @Valid AddProductInOrderDTO data) {
        return orderService.addProductInOrder(orderId, data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_UPDATE')")
    public ResponseEntity<OrderDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateOrderRequestDTO data) {
        return orderService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_DELETE')")
    public ResponseEntity<OrderDTO> delete(@PathVariable UUID id) {
        return orderService.delete(id);
    }
}

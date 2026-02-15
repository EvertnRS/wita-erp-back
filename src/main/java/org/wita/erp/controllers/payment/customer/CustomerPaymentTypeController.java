package org.wita.erp.controllers.payment.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.customer.dto.CreateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.DeleteCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.UpdateCustomerPaymentTypeRequestDTO;
import org.wita.erp.services.payment.customer.CustomerPaymentTypeService;

import java.util.UUID;

@RestController
@RequestMapping("/customer/payment")
@RequiredArgsConstructor
public class CustomerPaymentTypeController {
    private final CustomerPaymentTypeService customerPaymentService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_READ')")
    public ResponseEntity<Page<CustomerPaymentType>> getAllCustomerPaymentTypes(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return customerPaymentService.getAllCustomerPaymentTypes(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_CREATE')")
    public ResponseEntity<PaymentType> create(@Valid @RequestBody CreateCustomerPaymentTypeRequestDTO data) {
        return customerPaymentService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_UPDATE')")
    public ResponseEntity<PaymentType> update(@PathVariable UUID id, @RequestBody @Valid UpdateCustomerPaymentTypeRequestDTO data) {
        return customerPaymentService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_DELETE')")
    public ResponseEntity<PaymentType> delete(@PathVariable UUID id, @RequestBody @Valid DeleteCustomerPaymentTypeRequestDTO data) {
        return customerPaymentService.delete(id, data);
    }
}

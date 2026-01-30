package org.wita.erp.controllers.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.payment.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.CreateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.services.payment.CustomerPaymentTypeService;
import org.wita.erp.services.payment.PaymentTypeService;

import java.util.UUID;

@RestController
@RequestMapping("/customer/payment")
@RequiredArgsConstructor
public class CustomerPaymentTypeController {
    private final CustomerPaymentTypeService customerPaymentService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_READ')")
    public ResponseEntity<Page<PaymentType>> getAllCustomerPaymentTypes(@PageableDefault(size = 10, sort = "paymentMethod") Pageable pageable, @RequestParam(required = false) String searchTerm) {
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
    public ResponseEntity<PaymentType> delete(@PathVariable UUID id) {
        return customerPaymentService.delete(id);
    }
}

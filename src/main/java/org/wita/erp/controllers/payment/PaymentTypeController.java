package org.wita.erp.controllers.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.services.payment.PaymentTypeService;

import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentTypeController {
    private final PaymentTypeService paymentService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public ResponseEntity<Page<PaymentType>> getAllPaymentTypes(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return paymentService.getAllPaymentTypes(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    public ResponseEntity<PaymentType> create(@Valid @RequestBody CreatePaymentTypeRequestDTO data) {
        return paymentService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_UPDATE')")
    public ResponseEntity<PaymentType> update(@PathVariable UUID id, @RequestBody @Valid UpdatePaymentTypeRequestDTO data) {
        return paymentService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_DELETE')")
    public ResponseEntity<PaymentType> delete(@PathVariable UUID id) {
        return paymentService.delete(id);
    }
}

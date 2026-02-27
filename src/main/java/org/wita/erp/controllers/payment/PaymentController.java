package org.wita.erp.controllers.payment;

import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.payment.docs.PaymentDocs;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentCheckoutDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentDTO;
import org.wita.erp.services.payment.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController implements PaymentDocs {
    private final PaymentService paymentService;

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('PAYMENT_READ')")
    public ResponseEntity<Page<PaymentDTO>> getAllPayments(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return paymentService.getAllPayments(pageable, searchTerm);
    }

    @PostMapping("/create/{receivableId}")
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    public ResponseEntity<PaymentDTO> create(@PathVariable UUID receivableId, @Valid @RequestBody CreatePaymentRequestDTO data) {
        return paymentService.save(receivableId, data);
    }

    @PostMapping("/checkout/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_UPDATE')")
    public ResponseEntity<PaymentCheckoutDTO> checkout(@PathVariable UUID id) throws StripeException {
        return paymentService.checkout(id);
    }

}

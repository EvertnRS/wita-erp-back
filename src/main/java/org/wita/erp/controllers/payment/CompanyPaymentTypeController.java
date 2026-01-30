package org.wita.erp.controllers.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.payment.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.CreateCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdateCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.services.payment.CompanyPaymentTypeService;
import org.wita.erp.services.payment.PaymentTypeService;

import java.util.UUID;

@RestController
@RequestMapping("/company/payment")
@RequiredArgsConstructor
public class CompanyPaymentTypeController {
    private final CompanyPaymentTypeService companyPaymentService;

    @GetMapping
    @PreAuthorize("hasAuthority('COMPANY_PAYMENT_READ')")
    public ResponseEntity<Page<PaymentType>> getAllCompanyPaymentTypes(@PageableDefault(size = 10, sort = "paymentMethod") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return companyPaymentService.getAllCompanyPaymentTypes(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('COMPANY_PAYMENT_CREATE')")
    public ResponseEntity<PaymentType> create(@Valid @RequestBody CreateCompanyPaymentTypeRequestDTO data) {
        return companyPaymentService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_PAYMENT_UPDATE')")
    public ResponseEntity<PaymentType> update(@PathVariable UUID id, @RequestBody @Valid UpdateCompanyPaymentTypeRequestDTO data) {
        return companyPaymentService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_PAYMENT_DELETE')")
    public ResponseEntity<PaymentType> delete(@PathVariable UUID id) {
        return companyPaymentService.delete(id);
    }
}

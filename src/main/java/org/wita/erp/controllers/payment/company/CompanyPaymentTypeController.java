package org.wita.erp.controllers.payment.company;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.payment.company.docs.CompanyPaymentTypeDocs;
import org.wita.erp.domain.entities.payment.company.dtos.CompanyPaymentTypeDTO;
import org.wita.erp.domain.entities.payment.company.dtos.CreateCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.dtos.DeleteCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.dtos.UpdateCompanyPaymentTypeRequestDTO;
import org.wita.erp.services.payment.company.CompanyPaymentTypeService;

import java.util.UUID;

@RestController
@RequestMapping("/company/payment")
@RequiredArgsConstructor
public class CompanyPaymentTypeController implements CompanyPaymentTypeDocs {
    private final CompanyPaymentTypeService companyPaymentService;

    @GetMapping
    @PreAuthorize("hasAuthority('COMPANY_PAYMENT_READ')")
    public ResponseEntity<Page<CompanyPaymentTypeDTO>> getAllCompanyPaymentTypes(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return companyPaymentService.getAllCompanyPaymentTypes(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('COMPANY_PAYMENT_CREATE')")
    public ResponseEntity<CompanyPaymentTypeDTO> create(@Valid @RequestBody CreateCompanyPaymentTypeRequestDTO data) {
        return companyPaymentService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_PAYMENT_UPDATE')")
    public ResponseEntity<CompanyPaymentTypeDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateCompanyPaymentTypeRequestDTO data) {
        return companyPaymentService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_PAYMENT_DELETE')")
    public ResponseEntity<CompanyPaymentTypeDTO> delete(@PathVariable UUID id, @RequestBody @Valid DeleteCompanyPaymentTypeRequestDTO data) {
        return companyPaymentService.delete(id, data);
    }
}

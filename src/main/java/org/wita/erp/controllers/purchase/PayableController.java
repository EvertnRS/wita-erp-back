package org.wita.erp.controllers.purchase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.purchase.Payable;
import org.wita.erp.domain.entities.purchase.Purchase;
import org.wita.erp.domain.entities.purchase.dtos.CreatePayableRequestDTO;
import org.wita.erp.domain.entities.purchase.dtos.CreatePurchaseRequestDTO;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePayableRequestDTO;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePurchaseRequestDTO;
import org.wita.erp.services.purchase.PayableService;
import org.wita.erp.services.purchase.PurchaseService;

import java.util.UUID;

@RestController
@RequestMapping("/payable")
@RequiredArgsConstructor
public class PayableController {
    private final PayableService payableService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYABLE_READ')")
    public ResponseEntity<Page<Payable>> getAllPayable(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return payableService.getAllPayable(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PAYABLE_CREATE')")
    public ResponseEntity<Payable> create(@Valid @RequestBody CreatePayableRequestDTO data) {
        return payableService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYABLE_UPDATE')")
    public ResponseEntity<Payable> update(@PathVariable UUID id, @RequestBody @Valid UpdatePayableRequestDTO data) {
        return payableService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYABLE_DELETE')")
    public ResponseEntity<Payable> delete(@PathVariable UUID id) {
        return payableService.delete(id);
    }
}

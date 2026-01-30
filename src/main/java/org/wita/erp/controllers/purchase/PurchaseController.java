package org.wita.erp.controllers.purchase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.purchase.Purchase;
import org.wita.erp.domain.entities.purchase.dtos.CreatePurchaseRequestDTO;
import org.wita.erp.domain.entities.purchase.dtos.UpdatePurchaseRequestDTO;
import org.wita.erp.services.purchase.PurchaseService;


import java.util.UUID;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_READ')")
    public ResponseEntity<Page<Purchase>> getAllPurchases(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return purchaseService.getAllPurchases(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PURCHASE_CREATE')")
    public ResponseEntity<Purchase> create(@Valid @RequestBody CreatePurchaseRequestDTO data) {
        return purchaseService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_UPDATE')")
    public ResponseEntity<Purchase> update(@PathVariable UUID id, @RequestBody @Valid UpdatePurchaseRequestDTO data) {
        return purchaseService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_DELETE')")
    public ResponseEntity<Purchase> delete(@PathVariable UUID id) {
        return purchaseService.delete(id);
    }
}

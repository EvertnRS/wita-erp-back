package org.wita.erp.controllers.transaction.purchase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.transaction.purchase.dtos.*;
import org.wita.erp.services.transaction.purchase.PurchaseService;

import java.util.UUID;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_READ')")
    public ResponseEntity<Page<PurchaseDTO>> getAllPurchases(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return purchaseService.getAllPurchases(pageable, searchTerm);
    }

    @PostMapping("/create/replacement")
    @PreAuthorize("hasAuthority('PURCHASE_CREATE')")
    public ResponseEntity<PurchaseDTO> createReplacementPurchase(@Valid @RequestBody CreateReplacementPurchaseRequestDTO data) {
        return purchaseService.save(data);
    }

    @PostMapping("/create/expense")
    @PreAuthorize("hasAuthority('PURCHASE_CREATE')")
    public ResponseEntity<PurchaseDTO> createExpensePurchase(@Valid @RequestBody CreateExpensePurchaseRequestDTO data) {
        return purchaseService.save(data);
    }

    /*@PostMapping("/add-item/{purchaseId}")
    @PreAuthorize("hasAuthority('PURCHASE_UPDATE')")
    public ResponseEntity<PurchaseDTO> addProductInPurchase(@PathVariable UUID purchaseId, @RequestBody @Valid ProductPurchaseRequestDTO data) {
        return purchaseService.addProductInPurchase(purchaseId, data);
    }*/

    @PutMapping("replacement/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_UPDATE')")
    public ResponseEntity<PurchaseDTO> updateReplacement(@PathVariable UUID id, @RequestBody @Valid UpdatePurchaseReplacementRequestDTO data) {
        return purchaseService.update(id, data);
    }

    @PutMapping("expense/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_UPDATE')")
    public ResponseEntity<PurchaseDTO> updateExpense(@PathVariable UUID id, @RequestBody @Valid UpdatePurchaseExpenseRequestDTO data) {
        return purchaseService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_DELETE')")
    public ResponseEntity<PurchaseDTO> delete(@PathVariable UUID id) {
        return purchaseService.delete(id);
    }
}

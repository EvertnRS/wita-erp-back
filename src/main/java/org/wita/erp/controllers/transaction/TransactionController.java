package org.wita.erp.controllers.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.transaction.docs.TransactionDocs;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.transaction.TransactionType;
import org.wita.erp.domain.entities.transaction.dtos.TransactionDTO;
import org.wita.erp.services.transaction.TransactionService;

import java.util.UUID;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController implements TransactionDocs {
    private final TransactionService transactionService;

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('ORDER_READ') and hasAuthority('PURCHASE_READ')")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) TransactionType transactionType) {
        return transactionService.getAllTransactions(pageable, transactionType);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDER_DELETE') and hasAuthority('PURCHASE_DELETE')")
    public ResponseEntity<Transaction> delete(@PathVariable UUID id) {
        return transactionService.delete(id);
    }
}

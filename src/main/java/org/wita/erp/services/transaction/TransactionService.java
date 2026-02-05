package org.wita.erp.services.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.transaction.TransactionType;
import org.wita.erp.domain.repositories.transaction.TransactionRepository;
import org.wita.erp.infra.exceptions.transaction.TransactionException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public ResponseEntity<Page<Transaction>> getAllTransactions(Pageable pageable, TransactionType transactionType) {
        Page<Transaction> TransactionPage;

        if (transactionType == TransactionType.PURCHASE) {
            TransactionPage = transactionRepository.findAllPurchase(pageable);
        }
        else if(transactionType == TransactionType.ORDER){
            TransactionPage = transactionRepository.findAllOrder(pageable);
        }
        else {
            TransactionPage = transactionRepository.findAll(pageable);
        }

        return ResponseEntity.ok(TransactionPage);
    }

    public ResponseEntity<Transaction> delete(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionException("Transaction not found", HttpStatus.NOT_FOUND));
        transaction.setActive(false);
        transactionRepository.save(transaction);
        return ResponseEntity.ok(transaction);
    }
}

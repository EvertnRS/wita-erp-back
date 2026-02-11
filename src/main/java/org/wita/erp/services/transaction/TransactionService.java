package org.wita.erp.services.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.transaction.Transaction;
import org.wita.erp.domain.entities.transaction.TransactionType;
import org.wita.erp.domain.entities.transaction.dtos.TransactionDTO;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.order.mappers.OrderMapper;
import org.wita.erp.domain.entities.transaction.purchase.Purchase;
import org.wita.erp.domain.entities.transaction.purchase.mappers.PurchaseMapper;
import org.wita.erp.domain.repositories.transaction.TransactionRepository;
import org.wita.erp.infra.exceptions.transaction.TransactionException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final OrderMapper orderMapper;
    private final PurchaseMapper purchaseMapper;

    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(Pageable pageable, TransactionType transactionType) {

        if (transactionType == TransactionType.PURCHASE) {
            Page<Purchase> TransactionPage = transactionRepository.findAllPurchase(pageable);
            return ResponseEntity.ok(TransactionPage.map(purchaseMapper::toDTO));
        }
        else if(transactionType == TransactionType.ORDER){
            Page<Order> TransactionPage = transactionRepository.findAllOrder(pageable);
            return ResponseEntity.ok(TransactionPage.map(orderMapper::toDTO));
        }
        else {
            Page<Transaction> transactionPage = transactionRepository.findAll(pageable);

            Page<TransactionDTO> dtoPage = transactionPage.map(transaction ->
                    switch (transaction) {
                        case Purchase purchase -> purchaseMapper.toDTO(purchase);
                        case Order order -> orderMapper.toDTO(order);
                        default ->
                                throw new IllegalStateException("Unexpected value: " + transaction);
                    }
            );

            return ResponseEntity.ok(dtoPage);
        }
    }

    public ResponseEntity<TransactionDTO> delete(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionException("Transaction not found", HttpStatus.NOT_FOUND));

        transaction.setActive(false);
        transactionRepository.save(transaction);

        TransactionDTO dto = switch (transaction) {
            case Purchase purchase -> purchaseMapper.toDTO(purchase);
            case Order order -> orderMapper.toDTO(order);
            default -> throw new IllegalStateException("Unexpected value: " + transaction);
        };

        return ResponseEntity.ok(dto);
    }

}

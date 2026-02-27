package org.wita.erp.domain.entities.transaction;

import lombok.Getter;

@Getter
public enum TransactionType {
    ORDER("Order"),
    PURCHASE("Purchase");
    private final String transactionType;

    TransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}


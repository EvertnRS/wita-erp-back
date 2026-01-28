package org.wita.erp.domain.entities.stock;

import lombok.Getter;

@Getter
public enum StockMovementType {
    IN("in"),
    OUT("out");

    private final String stockMovementType;

    StockMovementType(String stockMovementType) {
        this.stockMovementType = stockMovementType;
    }

}

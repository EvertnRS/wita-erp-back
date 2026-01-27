package org.wita.erp.domain.stock;

public enum StockMovementType {
    IN("in"),
    OUT("out");

    private String stockMovementType;

    StockMovementType(String stockMovementType) {
        this.stockMovementType = stockMovementType;
    }

    public String getStockMovementType() {
        return this.stockMovementType;
    }
}

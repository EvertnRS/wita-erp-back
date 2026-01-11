package org.wita.erp.domain.Stock;

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

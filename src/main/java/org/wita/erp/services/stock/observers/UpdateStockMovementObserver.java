package org.wita.erp.services.stock.observers;

import org.wita.erp.domain.entities.stock.StockMovementType;

import java.util.UUID;

public record UpdateStockMovementObserver(StockMovementType stockMovementType, UUID product, int newQuantity) {
}


package org.wita.erp.services.stock.observers;

import org.wita.erp.domain.entities.stock.StockMovementType;

import java.util.UUID;

public record StockMovementObserver(StockMovementType stockMovementType, UUID product, int quantity) {
}


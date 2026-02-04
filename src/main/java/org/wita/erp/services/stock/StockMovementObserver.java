package org.wita.erp.services.stock;

import lombok.Getter;
import lombok.Setter;
import org.wita.erp.domain.entities.stock.StockMovementType;

import java.util.UUID;

@Getter
@Setter
public record StockMovementObserver(StockMovementType stockMovementType, UUID product, int quantity) {
}


package org.wita.erp.domain.entities.stock.dtos;

import jakarta.validation.constraints.Positive;
import org.wita.erp.domain.entities.stock.StockMovementType;

import java.util.UUID;

public record UpdateStockRequestDTO(UUID product,
                                    StockMovementType stockMovementType,
                                    @Positive Integer quantity,
                                    UUID movementReason,
                                    UUID user) {
}

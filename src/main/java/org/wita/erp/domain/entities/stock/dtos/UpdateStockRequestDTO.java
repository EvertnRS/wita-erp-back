package org.wita.erp.domain.entities.stock.dtos;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.stock.StockMovementType;

import java.util.UUID;

public record UpdateStockRequestDTO(@NotNull UUID product,
                                    @NotNull StockMovementType stockMovementType,
                                    @NotNull Integer quantity,
                                    @NotNull UUID movementReason,
                                    @NotNull UUID user) {
}

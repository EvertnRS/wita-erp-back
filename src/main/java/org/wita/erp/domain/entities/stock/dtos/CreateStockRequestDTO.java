package org.wita.erp.domain.entities.stock.dtos;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.stock.StockMovementType;

import java.util.UUID;

public record CreateStockRequestDTO(@NotNull UUID product,
                                    @NotNull Integer quantity,
                                    @NotNull UUID movementReason,
                                    @NotNull UUID transaction,
                                    @NotNull StockMovementType movementType) {
}

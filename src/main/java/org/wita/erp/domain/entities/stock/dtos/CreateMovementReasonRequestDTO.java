package org.wita.erp.domain.entities.stock.dtos;

import jakarta.validation.constraints.NotBlank;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.StockMovementType;
import org.wita.erp.domain.entities.user.User;

public record CreateMovementReasonRequestDTO(@NotBlank String reason) {
}

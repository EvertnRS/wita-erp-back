package org.wita.erp.domain.entities.stock.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.StockMovementType;
import org.wita.erp.domain.entities.user.User;

public record CreateMovementReasonRequestDTO(
        @Schema(description = "Reason for the stock movement", example = "Stock Adjustment")
        @NotBlank String reason
) {
}

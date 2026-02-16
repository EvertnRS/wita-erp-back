package org.wita.erp.domain.entities.transaction.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.payment.company.dtos.CompanyPaymentTypeDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.PurchaseItemDTO;
import org.wita.erp.domain.entities.user.dtos.BuyerDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PurchaseDTO(
        @Schema(description = "Purchase's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Purchase's total value", example = "100.00")
        BigDecimal total,
        @Schema(description = "Puchase's transaction code", example = "TR1234567890")
        String transactionCode,
        BuyerDTO buyer,
        CompanyPaymentTypeDTO companyPaymentType,
        List<PurchaseItemDTO> items) implements TransactionDTO {
}

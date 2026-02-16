package org.wita.erp.domain.entities.transaction.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.wita.erp.domain.entities.payment.customer.dto.CustomerPaymentTypeDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.OrderItemDTO;
import org.wita.erp.domain.entities.user.dtos.SellerDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
        @Schema(description = "Order's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Order's total value", example = "100.00")
        BigDecimal total,
        @Schema(description = "Order's discount value", example = "10.00")
        BigDecimal discount,
        @Schema(description = "Order's transaction code", example = "TR1234567890")
        String transactionCode,

        SellerDTO seller,
        CustomerPaymentTypeDTO customerPaymentType,
        List<OrderItemDTO> items) implements TransactionDTO {
}

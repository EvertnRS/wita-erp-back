package org.wita.erp.domain.entities.purchase.dtos;

import org.wita.erp.domain.entities.payment.dtos.PaymentTypeDTO;
import org.wita.erp.domain.entities.user.dtos.BuyerDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PurchaseDTO(
        UUID id,
        BigDecimal total,
        String transactionCode,
        BuyerDTO seller,
        PaymentTypeDTO paymentType,
        List<PurchaseItemDTO> items) {
}

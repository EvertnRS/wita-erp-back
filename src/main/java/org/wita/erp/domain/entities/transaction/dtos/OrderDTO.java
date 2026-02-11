package org.wita.erp.domain.entities.transaction.dtos;

import org.wita.erp.domain.entities.payment.customer.dto.CustomerPaymentTypeDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.OrderItemDTO;
import org.wita.erp.domain.entities.user.dtos.SellerDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
        UUID id,
        BigDecimal total,
        BigDecimal discount,
        String transactionCode,
        SellerDTO seller,
        CustomerPaymentTypeDTO customerPaymentType,
        List<OrderItemDTO> items) implements TransactionDTO {
}

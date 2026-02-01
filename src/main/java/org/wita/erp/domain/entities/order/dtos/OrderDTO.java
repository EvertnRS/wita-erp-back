package org.wita.erp.domain.entities.order.dtos;

import org.wita.erp.domain.entities.customer.dtos.CustomerDTO;
import org.wita.erp.domain.entities.order.OrderItem;
import org.wita.erp.domain.entities.payment.dtos.PaymentTypeDTO;
import org.wita.erp.domain.entities.user.dtos.SellerDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
        UUID id,
        BigDecimal total,
        BigDecimal discount,
        String transactionCode,
        SellerDTO seller,
        CustomerDTO customer,
        PaymentTypeDTO paymentType,
        List<OrderItemDTO> items) {
}

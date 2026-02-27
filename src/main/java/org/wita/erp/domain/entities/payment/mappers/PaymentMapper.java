package org.wita.erp.domain.entities.payment.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wita.erp.domain.entities.payment.Payment;
import org.wita.erp.domain.entities.payment.dtos.PaymentDTO;
import org.wita.erp.domain.entities.product.mappers.CategoryMapper;
import org.wita.erp.domain.entities.supplier.mappers.SupplierMapper;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, SupplierMapper.class})
public interface PaymentMapper {
    @Mapping(source = "receivable.id", target = "receivableId")
    PaymentDTO toDTO(Payment payment);
}

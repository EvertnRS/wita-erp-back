package org.wita.erp.domain.entities.payment.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;

@Mapper(componentModel = "spring")
public interface PaymentTypeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePaymentTypeFromDTO(UpdatePaymentTypeRequestDTO dto, @MappingTarget PaymentType paymentType);
}

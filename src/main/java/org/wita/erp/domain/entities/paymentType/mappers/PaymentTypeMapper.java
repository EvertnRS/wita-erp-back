package org.wita.erp.domain.entities.paymentType.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.paymentType.PaymentType;
import org.wita.erp.domain.entities.paymentType.dtos.UpdatePaymentTypeRequestDTO;

@Mapper(componentModel = "spring")
public interface PaymentTypeMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePaymentTypeFromDTO(UpdatePaymentTypeRequestDTO dto, @MappingTarget PaymentType paymentType);
}

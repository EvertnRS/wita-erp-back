package org.wita.erp.domain.entities.payment.customer.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.customer.dto.UpdateCustomerPaymentTypeRequestDTO;

@Mapper(componentModel = "spring")
public interface CustomerPaymentTypeMapper {
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "isImmediate", ignore = true)
    @Mapping(target = "allowsInstallments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerPaymentTypeFromDTO(UpdateCustomerPaymentTypeRequestDTO dto, @MappingTarget CustomerPaymentType paymentType);

}

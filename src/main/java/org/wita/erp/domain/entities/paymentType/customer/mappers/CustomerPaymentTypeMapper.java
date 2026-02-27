package org.wita.erp.domain.entities.paymentType.customer.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.paymentType.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.paymentType.customer.dto.CustomerPaymentTypeDTO;
import org.wita.erp.domain.entities.paymentType.customer.dto.UpdateCustomerPaymentTypeRequestDTO;

@Mapper(componentModel = "spring")
public interface CustomerPaymentTypeMapper {
    @Mapping(target = "isImmediate", ignore = true)
    @Mapping(target = "allowsInstallments", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerPaymentTypeFromDTO(UpdateCustomerPaymentTypeRequestDTO dto, @MappingTarget CustomerPaymentType paymentType);

    CustomerPaymentTypeDTO toDTO(CustomerPaymentType customerPaymentType);
}

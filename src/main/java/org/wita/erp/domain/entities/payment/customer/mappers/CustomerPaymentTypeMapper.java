package org.wita.erp.domain.entities.payment.customer.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.customer.dto.UpdateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentTypeDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;

@Mapper(componentModel = "spring")
public interface CustomerPaymentTypeMapper {
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "isImmediate", ignore = true)
    @Mapping(target = "allowsInstallments", ignore = true)
    @Mapping(target = "maxInstallments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerPaymentTypeFromDTO(UpdateCustomerPaymentTypeRequestDTO dto, @MappingTarget CustomerPaymentType paymentType);

}

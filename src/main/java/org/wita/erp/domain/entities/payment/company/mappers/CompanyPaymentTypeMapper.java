package org.wita.erp.domain.entities.payment.company.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.company.dtos.UpdateCompanyPaymentTypeRequestDTO;

@Mapper(componentModel = "spring")
public interface CompanyPaymentTypeMapper {
    @Mapping(target = "isImmediate", ignore = true)
    @Mapping(target = "allowsInstallments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCompanyPaymentTypeFromDTO(UpdateCompanyPaymentTypeRequestDTO dto, @MappingTarget CompanyPaymentType paymentType);

}

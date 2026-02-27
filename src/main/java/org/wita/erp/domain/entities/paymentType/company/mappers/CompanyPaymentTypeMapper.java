package org.wita.erp.domain.entities.paymentType.company.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.paymentType.company.CompanyPaymentType;
import org.wita.erp.domain.entities.paymentType.company.dtos.CompanyPaymentTypeDTO;
import org.wita.erp.domain.entities.paymentType.company.dtos.UpdateCompanyPaymentTypeRequestDTO;

@Mapper(componentModel = "spring")
public interface CompanyPaymentTypeMapper {
    @Mapping(target = "isImmediate", ignore = true)
    @Mapping(target = "allowsInstallments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCompanyPaymentTypeFromDTO(UpdateCompanyPaymentTypeRequestDTO dto, @MappingTarget CompanyPaymentType paymentType);

    CompanyPaymentTypeDTO toDTO(CompanyPaymentType companyPaymentType);
}

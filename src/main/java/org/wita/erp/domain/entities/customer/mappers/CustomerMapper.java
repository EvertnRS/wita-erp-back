package org.wita.erp.domain.entities.customer.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.customer.dtos.UpdateCustomerRequestDTO;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromDTO(UpdateCustomerRequestDTO dto, @MappingTarget Customer customer);

}

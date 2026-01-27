package org.wita.erp.domain.Customer.Mapper;

import org.mapstruct.*;
import org.wita.erp.domain.Customer.Customer;
import org.wita.erp.domain.Customer.Dtos.UpdateCustomerRequestDTO;
import org.wita.erp.domain.User.Dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.User.Dtos.UserDTO;
import org.wita.erp.domain.User.User;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromDTO(UpdateCustomerRequestDTO dto, @MappingTarget Customer customer);

}

package org.wita.erp.domain.entities.user.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.user.dtos.SellerDTO;
import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;
import org.wita.erp.domain.entities.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDTO(UpdateUserRequestDTO dto, @MappingTarget User user);

    UserDTO toUserDTO(User user);

    SellerDTO toSellerDTO(User user);
}

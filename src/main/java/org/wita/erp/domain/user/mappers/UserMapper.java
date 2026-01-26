package org.wita.erp.domain.user.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.user.dtos.UserDTO;
import org.wita.erp.domain.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDTO(UpdateUserRequestDTO dto, @MappingTarget User user);

    UserDTO toUserDTO(User user);
}

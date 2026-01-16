package org.wita.erp.domain.User.Mappers;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import org.wita.erp.domain.User.Dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.User.Dtos.UserDTO;
import org.wita.erp.domain.User.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDTO(UpdateUserRequestDTO dto, @MappingTarget User user);

    UserDTO toUserDTO(User user);
}

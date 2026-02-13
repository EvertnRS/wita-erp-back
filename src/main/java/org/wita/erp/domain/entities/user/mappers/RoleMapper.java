package org.wita.erp.domain.entities.user.mappers;

import org.mapstruct.*;
import org.wita.erp.domain.entities.user.Permission;
import org.wita.erp.domain.entities.user.Role;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.entities.user.dtos.RoleDTO;
import org.wita.erp.domain.entities.user.dtos.SellerDTO;
import org.wita.erp.domain.entities.user.dtos.UpdateUserRequestDTO;
import org.wita.erp.domain.entities.user.dtos.UserDTO;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);

    default Set<String> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) {
            return Collections.emptySet();
        }

        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}


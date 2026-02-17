package org.wita.erp.domain.entities.user.mappers;

import org.mapstruct.Mapper;
import org.wita.erp.domain.entities.user.dtos.RoleDTO;
import org.wita.erp.domain.entities.user.role.Permission;
import org.wita.erp.domain.entities.user.role.Role;

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


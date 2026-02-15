package org.wita.erp.domain.entities.user.dtos;

import org.wita.erp.domain.entities.user.role.Role;

import java.util.UUID;

public record UserDTO(UUID id, String name, String email, Role role, Boolean active) {
}

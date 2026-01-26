package org.wita.erp.domain.user.dtos;

import org.wita.erp.domain.user.Role;

import java.util.UUID;

public record UserDTO(UUID id, String name, String email, Role role, Boolean active) {
}

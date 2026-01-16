package org.wita.erp.domain.User.Dtos;

import org.wita.erp.domain.User.Role;

import java.util.UUID;

public record UserDTO(UUID id, String name, String email, Role role, Boolean active) {
}

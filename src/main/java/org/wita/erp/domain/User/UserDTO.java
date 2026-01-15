package org.wita.erp.domain.User;

import java.util.UUID;

public record UserDTO(UUID id, String name, String email, Role role, Boolean active) {
}

package org.wita.erp.domain.User;

public record LoginResponseDTO(UserDTO user, String token) {
}

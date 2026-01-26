package org.wita.erp.domain.user.dtos;

public record LoginResponseDTO(UserDTO user, String token) {
}

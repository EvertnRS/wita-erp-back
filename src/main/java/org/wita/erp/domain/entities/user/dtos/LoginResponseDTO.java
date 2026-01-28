package org.wita.erp.domain.entities.user.dtos;

public record LoginResponseDTO(UserDTO user, String token) {
}

package org.wita.erp.domain.User.Dtos;

public record LoginResponseDTO(UserDTO user, String token) {
}

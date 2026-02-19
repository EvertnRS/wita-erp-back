package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDTO(
        UserDTO user,
        @Schema(description = "JWT token for authenticated access", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        @Schema(description = "Indicates if two-factor authentication is required", example = "false")
        boolean twoFactorRequired){}

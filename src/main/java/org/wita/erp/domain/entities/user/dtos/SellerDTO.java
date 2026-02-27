package org.wita.erp.domain.entities.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SellerDTO(
        @Schema(description = "Seller's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Seller's name", example = "John Doe")
        String name,
        @Schema(description = "Seller's email", example = "john@exmple.com")
        String email) {
}

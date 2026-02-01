package org.wita.erp.domain.entities.order.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductOrderRequestDTO(
        @NotNull UUID product,
        @NotNull Integer quantity
        ) {
}

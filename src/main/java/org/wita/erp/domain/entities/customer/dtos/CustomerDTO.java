package org.wita.erp.domain.entities.customer.dtos;

import java.util.UUID;

public record CustomerDTO(UUID id, String name, String email) {
}

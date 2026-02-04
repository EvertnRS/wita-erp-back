package org.wita.erp.services.order;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public record CreateOrderObserver(UUID order, UUID movementReason) {
}


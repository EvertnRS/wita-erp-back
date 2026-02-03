package org.wita.erp.services.order;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderObserver {
    private final UUID order;
    private final UUID movementReason;

    public CreateOrderObserver(UUID order, UUID movementReason) {
        this.order = order;
        this.movementReason = movementReason;
    }
}


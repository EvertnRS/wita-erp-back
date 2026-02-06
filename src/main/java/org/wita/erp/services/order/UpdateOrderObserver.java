package org.wita.erp.services.order;

import lombok.Getter;
import lombok.Setter;
import org.wita.erp.domain.entities.order.dtos.OrderItemChange;

import java.util.List;
import java.util.UUID;

/*@Getter
@Setter*/
public record UpdateOrderObserver(UUID order, UUID movementReason, List<OrderItemChange> changes) {
}


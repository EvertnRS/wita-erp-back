package org.wita.erp.services.purchase;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public record CreatePurchaseObserver(UUID purchase, UUID movementReason) {
}


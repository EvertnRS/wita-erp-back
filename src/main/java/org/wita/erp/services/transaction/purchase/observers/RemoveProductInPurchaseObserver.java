package org.wita.erp.services.transaction.purchase.observers;

import org.wita.erp.domain.entities.transaction.purchase.dtos.ProductPurchaseRequestDTO;

import java.util.UUID;

public record RemoveProductInPurchaseObserver(UUID purchase, UUID movementReason, ProductPurchaseRequestDTO stockDifference) {
}

package org.wita.erp.domain.entities.transaction.purchase.dtos;

public record UpdatePurchaseRequestDTO(
                                       String transactionCode,
                                       String description
                                        ) {
}

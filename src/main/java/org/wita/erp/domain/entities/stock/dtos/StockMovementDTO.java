package org.wita.erp.domain.entities.stock.dtos;

import org.wita.erp.domain.entities.stock.StockMovementType;
import org.wita.erp.domain.entities.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementDTO(UUID id, Product product, User user, MovementInfo movementInfo, String transactionCode, LocalDateTime createdAt) {
    public record Product(UUID id, String name, Integer quantity, Integer quantityInStock) {}
    public record User(UUID id, String name){}
    public record MovementInfo(StockMovementType movementType, String movementReason){}
}

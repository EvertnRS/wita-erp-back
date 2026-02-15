package org.wita.erp.domain.entities.audit;

import lombok.Getter;

@Getter
public enum EntityType {
    USER("users"),
    ROLE("role"),
    CATEGORY("category"),
    PRODUCT("product"),
    CUSTOMER("customer"),
    SUPPLIER("supplier"),
    PAYMENT_TYPE("payment_type"),
    TRANSACTION("transaction"),
    RECEIVABLE("receivable"),
    PAYABLE("payable"),
    MOVEMENT_REASON("movement_reason"),
    STOCK_MOVEMENT("stock_movement");
    private final String entityType;

    EntityType(String entityType) {
        this.entityType = entityType;
    }

}



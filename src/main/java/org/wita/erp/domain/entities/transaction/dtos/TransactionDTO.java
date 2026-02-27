package org.wita.erp.domain.entities.transaction.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PurchaseDTO.class, name = "PURCHASE"),
        @JsonSubTypes.Type(value = OrderDTO.class, name = "ORDER")
})
public sealed interface TransactionDTO
        permits PurchaseDTO, OrderDTO {
}
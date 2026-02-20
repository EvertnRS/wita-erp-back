package org.wita.erp.domain.entities.transaction.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PayableDTO.class, name = "PAYABLE"),
        @JsonSubTypes.Type(value = ReceivableDTO.class, name = "RECEIVABLE")
})
public sealed interface AccountsDTO
        permits PayableDTO, ReceivableDTO {
}
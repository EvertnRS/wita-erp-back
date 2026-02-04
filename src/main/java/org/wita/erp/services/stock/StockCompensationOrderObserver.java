package org.wita.erp.services.stock;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public record StockCompensationOrderObserver(UUID order) {
}


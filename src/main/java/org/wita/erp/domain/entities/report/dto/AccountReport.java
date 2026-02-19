package org.wita.erp.domain.entities.report.dto;

import org.wita.erp.domain.entities.transaction.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountReport(UUID id,
                            BigDecimal value,
                            LocalDate dueDate,
                            PaymentStatus status,
                            String type) {

}
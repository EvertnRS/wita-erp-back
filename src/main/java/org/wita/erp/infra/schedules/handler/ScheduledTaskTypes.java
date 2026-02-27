package org.wita.erp.infra.schedules.handler;

import lombok.Getter;


@Getter
public enum ScheduledTaskTypes {
    RECEIVABLE_OVERDUE("RECEIVABLE_OVERDUE"),
    RECEIVABLE_DUE_SOON("RECEIVABLE_DUE_SOON"),
    PAYABLE_OVERDUE("PAYABLE_OVERDUE"),
    PAYABLE_DUE_SOON("PAYABLE_DUE_SOON"),
    PRODUCT_REPLENISHMENT("PRODUCT_REPLENISHMENT");
    private final String scheduledTaskTypes;

    ScheduledTaskTypes(String scheduledTaskTypes) {
        this.scheduledTaskTypes = scheduledTaskTypes;
    }
}


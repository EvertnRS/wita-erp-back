package org.wita.erp.infra.schedules.handler;

import jakarta.mail.MessagingException;

public interface ScheduledTaskHandler {

    ScheduledTaskTypes getType();
    void handle(String referenceId) throws MessagingException;
}

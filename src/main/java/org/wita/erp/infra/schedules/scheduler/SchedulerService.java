package org.wita.erp.infra.schedules.scheduler;

import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;

import java.time.LocalDateTime;

public interface SchedulerService {

    void schedule(
            ScheduledTaskTypes type,
            String referenceId,
            LocalDateTime executeAt
    );
}


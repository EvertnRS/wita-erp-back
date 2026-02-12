package org.wita.erp.infra.schedules.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class RedisDelayScheduler implements SchedulerService {
    private final StringRedisTemplate redisTemplate;
    private static final String KEY = "scheduled_tasks";

    @Override
    public void schedule(ScheduledTaskTypes type,
                         String referenceId,
                         LocalDateTime executeAt) {

        long score = executeAt
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();

        String value = type.name() + ":" + referenceId;

        redisTemplate.opsForZSet()
                .add(KEY, value, score);
    }

    @Override
    public void cancel(ScheduledTaskTypes type,
                       String referenceId) {

        String value = type + ":" + referenceId;

        redisTemplate.opsForZSet()
                .remove(KEY, value);
    }

    @Override
    public void reschedule(ScheduledTaskTypes type,
                           String referenceId,
                           LocalDateTime executeAt) {

        cancel(type, referenceId);
        schedule(type, referenceId, executeAt);
    }
}


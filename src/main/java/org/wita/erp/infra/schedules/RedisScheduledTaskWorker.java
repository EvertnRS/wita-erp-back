package org.wita.erp.infra.schedules;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wita.erp.infra.schedules.handler.ScheduledTaskHandler;
import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisScheduledTaskWorker {

    private final StringRedisTemplate redisTemplate;
    private final List<ScheduledTaskHandler> handlers;

    private static final String KEY = "scheduled_tasks";

    @Scheduled(fixedDelay = 1000)
    public void process() {

        long now = Instant.now().getEpochSecond();

        Set<String> tasks = redisTemplate.opsForZSet()
                .rangeByScore(KEY, 0, now);

        if (tasks.isEmpty())
            return;

        for (String task : tasks) {

            try {

                String[] parts = task.split(":");
                ScheduledTaskTypes type = ScheduledTaskTypes.valueOf(parts[0]);
                String referenceId = parts[1];

                handlers.stream()
                        .filter(h -> h.getType() == type)
                        .findFirst()
                        .ifPresent(h -> {
                            try {
                                h.handle(referenceId);
                            } catch (MessagingException e) {
                                log.error("Error executing task", e);
                            }
                        });

                redisTemplate.opsForZSet()
                        .remove(KEY, task);

            } catch (Exception e) {
                log.error("Error processing task {}", task, e);
            }
        }
    }
}

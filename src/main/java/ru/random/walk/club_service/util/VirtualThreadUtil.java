package ru.random.walk.club_service.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class VirtualThreadUtil {
    public static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public static Future<?> scheduleTask(Runnable task) {
        return executor.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Exception in task with virtual thread!", e);
            }
        });
    }
}

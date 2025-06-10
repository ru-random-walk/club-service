package ru.random.walk.club_service.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class VirtualThreadUtil {
    public static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private static final List<Future<?>> allRunningFutures = new ArrayList<>();

    public static Future<?> scheduleTask(Runnable task) {
        var future = executor.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Exception in task with virtual thread!", e);
            }
        });
        allRunningFutures.add(future);
        return future;
    }

    public static void awaitAllRunningTasks() throws ExecutionException, InterruptedException {
        while (!allRunningFutures.isEmpty()) {
            allRunningFutures.getLast().get();
            allRunningFutures.removeLast();
        }
    }
}

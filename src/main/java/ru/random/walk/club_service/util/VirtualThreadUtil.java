package ru.random.walk.club_service.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadUtil {
    public static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
}

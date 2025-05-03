package ru.random.walk.club_service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.random.walk.client.StorageClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestContextConfiguration {
    @Bean
    public StorageClient storageClient() {
        return mock(StorageClient.class);
    }
}

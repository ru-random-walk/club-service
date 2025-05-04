package ru.random.walk.club_service.service;

import java.util.List;

public interface OutboxSenderService {
    void sendMessage(String topic, Object payload);

    void sendAllMessages(String topic, List<?> payloads);
}

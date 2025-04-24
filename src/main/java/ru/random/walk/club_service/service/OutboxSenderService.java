package ru.random.walk.club_service.service;

public interface OutboxSenderService {
    void sendMessage(String topic, Object payload);
}

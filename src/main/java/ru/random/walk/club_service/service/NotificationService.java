package ru.random.walk.club_service.service;

import java.util.UUID;

public interface NotificationService {
    void sendForAssignedApprover(UUID approverId, UUID answerer, UUID clubId);
}

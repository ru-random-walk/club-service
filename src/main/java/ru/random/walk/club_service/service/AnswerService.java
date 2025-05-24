package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.entity.AnswerEntity;

import java.util.UUID;

public interface AnswerService {
    AnswerEntity createMembersConfirm(UUID approvementId, UUID userId);

    AnswerEntity createForm(UUID approvementId, FormAnswerData formAnswerData, UUID userId);

    AnswerEntity updateForm(UUID answerId, FormAnswerData formAnswerData);

    AnswerEntity setStatusToSent(UUID answerId, UUID userId);

    AnswerEntity setStatusToSentSync(UUID answerId, UUID userId);
}

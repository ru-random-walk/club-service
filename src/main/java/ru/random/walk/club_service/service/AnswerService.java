package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.entity.AnswerEntity;

import java.security.Principal;
import java.util.UUID;

public interface AnswerService {
    AnswerEntity createMembersConfirm(UUID approvementId, Principal principal);

    AnswerEntity createForm(UUID approvementId, FormAnswerData formAnswerData, Principal principal);

    AnswerEntity updateForm(UUID answerId, FormAnswerData formAnswerData, Principal principal);
}

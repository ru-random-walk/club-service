package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.service.AnswerService;

import java.security.Principal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final ApprovementRepository approvementRepository;

    @Override
    public AnswerEntity createMembersConfirm(UUID approvementId, Principal principal) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        var userId = UUID.fromString(principal.getName());
        return answerRepository.save(AnswerEntity.builder()
                .userId(userId)
                .status(AnswerStatus.CREATED)
                .approvement(approvement)
                .data(MembersConfirmAnswerData.DEFAULT)
                .build());
    }

    @Override
    public AnswerEntity createForm(UUID approvementId, FormAnswerData formAnswerData, Principal principal) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        var userId = UUID.fromString(principal.getName());
        return answerRepository.save(AnswerEntity.builder()
                .userId(userId)
                .status(AnswerStatus.CREATED)
                .approvement(approvement)
                .data(formAnswerData)
                .build());
    }
}

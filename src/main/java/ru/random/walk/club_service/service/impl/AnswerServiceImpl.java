package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.exception.AuthenticationException;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.exception.ValidationException;
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
        if (approvement.getType() != ApprovementType.MEMBERS_CONFIRM) {
            throw new ValidationException("Approvement type mismatch with answer type!");
        }
        var userId = UUID.fromString(principal.getName());
        var userAnswerCount = answerRepository.countByApprovementAndUserId(approvement, userId);
        if (userAnswerCount >= 1) {
            throw new ValidationException("You can not have more than one answer for approvement!");
        }
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
        if (approvement.getType() != ApprovementType.FORM) {
            throw new ValidationException("Approvement type mismatch with answer type!");
        }
        var userId = UUID.fromString(principal.getName());
        var userAnswerCount = answerRepository.countByApprovementAndUserId(approvement, userId);
        if (userAnswerCount >= 1) {
            throw new ValidationException("You can not have more than one answer for approvement!");
        }
        return answerRepository.save(AnswerEntity.builder()
                .userId(userId)
                .status(AnswerStatus.CREATED)
                .approvement(approvement)
                .data(formAnswerData)
                .build());
    }

    @Override
    public AnswerEntity updateForm(UUID answerId, FormAnswerData formAnswerData, Principal principal) {
        var answer = authUserByAnswerAndGet(answerId, principal);
        if (answer.getStatus() != AnswerStatus.CREATED) {
            throw new ValidationException("Answer status is not created!");
        }
        answer.setData(formAnswerData);
        return answerRepository.save(answer);
    }

    @Override
    public AnswerEntity setStatusToSent(UUID answerId, Principal principal) {
        var answer = authUserByAnswerAndGet(answerId, principal);
        if (answer.getStatus() != AnswerStatus.CREATED) {
            throw new ValidationException("Answer status is not created!");
        }
        answer.setStatus(AnswerStatus.SENT);
        return answerRepository.save(answer);
    }

    @NotNull
    private AnswerEntity authUserByAnswerAndGet(UUID answerId, Principal principal) {
        var answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new NotFoundException("Answer with such id not found!"));
        var userLogin = UUID.fromString(principal.getName());
        if (!answer.getUserId().equals(userLogin)) {
            throw new AuthenticationException("You do not have access to update this answer!");
        }
        return answer;
    }
}

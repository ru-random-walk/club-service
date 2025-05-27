package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.service.AnswerService;
import ru.random.walk.club_service.service.reviewer.AnswerReviewer;
import ru.random.walk.club_service.service.validation.impl.AnswerValidatorChainImpl;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final ApprovementRepository approvementRepository;
    private final AnswerValidatorChainImpl answerValidatorChain;
    private final AnswerReviewer answerReviewer;

    @Override
    public AnswerEntity createMembersConfirm(UUID approvementId, UUID userId) {
        return saveWithValidateAnswerData(approvementId, MembersConfirmAnswerData.INSTANCE, userId);
    }

    @Override
    public AnswerEntity createForm(UUID approvementId, FormAnswerData formAnswerData, UUID userId) {
        return saveWithValidateAnswerData(approvementId, formAnswerData, userId);
    }

    private void checkUserAnswerCount(ApprovementEntity approvement, UUID userId) {
        var userAnswerCount = answerRepository.countByApprovementAndUserId(approvement, userId);
        if (userAnswerCount >= 1) {
            throw new ValidationException("You can not have more than one answer for approvement!");
        }
    }

    @Override
    public AnswerEntity updateForm(UUID answerId, FormAnswerData formAnswerData) {
        var answer = answerRepository.findById(answerId).orElseThrow();
        if (answer.getStatus() != AnswerStatus.CREATED) {
            throw new ValidationException("Answer status is not created!");
        }
        var approvement = answer.getApprovement();
        answerValidatorChain.validate(formAnswerData, approvement.getData(), approvement.getType());
        answer.setData(formAnswerData);
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public AnswerEntity setStatusToSent(UUID answerId, UUID userId) {
        var answer = updateEntityStatusToSent(answerId);
        var approvement = answer.getApprovement();
        scheduleReview(answerId, answer.getData(), approvement.getData(), userId, approvement.getClubId(), false);
        return answer;
    }

    @Override
    @Transactional
    public AnswerEntity setStatusToSentSync(UUID answerId, UUID userId) {
        var answer = updateEntityStatusToSent(answerId);
        var approvement = answer.getApprovement();
        var status = scheduleReview(answerId, answer.getData(), approvement.getData(), userId, approvement.getClubId(), true);
        answer.setStatus(status);
        return answer;
    }

    @NotNull
    private AnswerEntity saveWithValidateAnswerData(UUID approvementId, AnswerData answerData, UUID userId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such answerId not found!"));
        answerValidatorChain.validate(answerData, approvement.getData(), approvement.getType());
        checkUserAnswerCount(approvement, userId);
        return answerRepository.save(AnswerEntity.builder()
                .userId(userId)
                .status(AnswerStatus.CREATED)
                .approvement(approvement)
                .data(answerData)
                .build());
    }

    @NotNull
    private AnswerEntity updateEntityStatusToSent(UUID answerId) {
        var answer = answerRepository.findById(answerId).orElseThrow();
        if (answer.getStatus() != AnswerStatus.CREATED) {
            throw new ValidationException("Answer status is not equals 'CREATED'!");
        }
        answer.setStatus(AnswerStatus.SENT);
        return answerRepository.save(answer);
    }

    private AnswerStatus scheduleReview(
            UUID answerId,
            AnswerData answerData,
            ApprovementData approvementData,
            UUID userId,
            UUID clubId,
            boolean synchronously
    ) {
        try {
            var reviewData = new ForReviewData(answerId, answerData, approvementData, userId, clubId);
            var future = answerReviewer.scheduleReview(reviewData);
            if (synchronously) {
                return future.get();
            }
        } catch (Exception e) {
            log.error("Error while schedule review", e);
            throw new RuntimeException("Error while schedule review", e);
        }
        return null;
    }
}

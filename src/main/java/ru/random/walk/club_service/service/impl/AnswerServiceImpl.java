package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
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
import ru.random.walk.club_service.service.auth.impl.AuthenticatorImpl;
import ru.random.walk.club_service.service.reviewer.AnswerReviewer;
import ru.random.walk.club_service.service.validation.impl.AnswerValidatorChainImpl;

import java.security.Principal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final ApprovementRepository approvementRepository;
    private final AnswerValidatorChainImpl answerValidatorChain;
    private final AuthenticatorImpl authenticator;
    private final AnswerReviewer answerReviewer;

    @Override
    public AnswerEntity createMembersConfirm(UUID approvementId, Principal principal) {
        return saveWithValidateAnswerData(approvementId, MembersConfirmAnswerData.INSTANCE, principal);
    }

    @Override
    public AnswerEntity createForm(UUID approvementId, FormAnswerData formAnswerData, Principal principal) {
        return saveWithValidateAnswerData(approvementId, formAnswerData, principal);
    }

    @NotNull
    private AnswerEntity saveWithValidateAnswerData(UUID approvementId, AnswerData answerData, Principal principal) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such answerId not found!"));
        answerValidatorChain.validate(answerData, approvement.getData(), approvement.getType());
        var userId = UUID.fromString(principal.getName());
        checkUserAnswerCount(approvement, userId);
        return answerRepository.save(AnswerEntity.builder()
                .userId(userId)
                .status(AnswerStatus.CREATED)
                .approvement(approvement)
                .data(answerData)
                .build());
    }

    private void checkUserAnswerCount(ApprovementEntity approvement, UUID userId) {
        var userAnswerCount = answerRepository.countByApprovementAndUserId(approvement, userId);
        if (userAnswerCount >= 1) {
            throw new ValidationException("You can not have more than one answer for approvement!");
        }
    }

    @Override
    public AnswerEntity updateForm(UUID answerId, FormAnswerData formAnswerData, Principal principal) {
        var answer = authenticator.authUserByAnswerAndGet(answerId, principal);
        if (answer.getStatus() != AnswerStatus.CREATED) {
            throw new ValidationException("Answer status is not created!");
        }
        var approvement = answer.getApprovement();
        answerValidatorChain.validate(formAnswerData, approvement.getData(), approvement.getType());
        answer.setData(formAnswerData);
        return answerRepository.save(answer);
    }

    @Override
    public AnswerEntity setStatusToSent(UUID answerId, Principal principal) {
        var answer = updateEntityStatusToSent(answerId, principal);
        var userId = authenticator.getLogin(principal);
        var approvement = answer.getApprovement();
        scheduleReview(answerId, answer.getData(), approvement.getData(), userId, approvement.getClubId());
        return answer;
    }

    @NotNull
    private AnswerEntity updateEntityStatusToSent(UUID answerId, Principal principal) {
        var answer = authenticator.authUserByAnswerAndGet(answerId, principal);
        if (answer.getStatus() != AnswerStatus.CREATED) {
            throw new ValidationException("Answer status is not equals 'CREATED'!");
        }
        answer.setStatus(AnswerStatus.SENT);
        return answerRepository.save(answer);
    }

    private void scheduleReview(
            UUID answerId,
            AnswerData answerData,
            ApprovementData approvementData,
            UUID userId,
            UUID clubId
    ) {
        var reviewData = new ForReviewData(answerId, answerData, approvementData, userId, clubId);
        answerReviewer.scheduleReview(reviewData);
    }
}

package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.AnswerMapper;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.model.graphql.types.FormAnswerInput;
import ru.random.walk.club_service.service.AnswerService;
import ru.random.walk.club_service.service.auth.Authenticator;
import ru.random.walk.util.KeyRateLimiter;

import java.security.Principal;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
@PreAuthorize("hasAuthority('DEFAULT_USER')")
public class AnswerController {
    private final AnswerMapper answerMapper;
    private final AnswerService answerService;
    private final Authenticator authenticator;
    private final KeyRateLimiter<UUID> setAnswerStatusToSentUserRateLimiter;

    @MutationMapping
    public AnswerEntity createApprovementAnswerMembersConfirm(
            @Argument UUID approvementId,
            Principal principal
    ) {
        log.info("""
                        Create approvement answer members confirm for [{}]
                        with login [{}]
                        for approvement id [{}]
                        """,
                principal, principal.getName(), approvementId
        );
        var userId = authenticator.getLogin(principal);
        return answerService.createMembersConfirm(approvementId, userId);
    }

    @MutationMapping
    public AnswerEntity createApprovementAnswerForm(
            @Argument UUID approvementId,
            @Argument FormAnswerInput formAnswer,
            Principal principal
    ) {
        log.info("""
                        Create approvement answer form for [{}]
                        with login [{}]
                        for approvement id [{}]
                        with form answer [{}]
                        """,
                principal, principal.getName(), approvementId, formAnswer
        );
        var formAnswerData = answerMapper.toFormAnswerData(formAnswer);
        var userId = authenticator.getLogin(principal);
        return answerService.createForm(approvementId, formAnswerData, userId);
    }

    @MutationMapping
    public AnswerEntity updateAnswerForm(
            @Argument UUID answerId,
            @Argument FormAnswerInput formAnswer,
            Principal principal
    ) {
        log.info("""
                        Update approvement answer form for [{}]
                        with login [{}]
                        for answer id [{}]
                        with form answer [{}]
                        """,
                principal, principal.getName(), answerId, formAnswer
        );
        authenticator.authUserByAnswer(answerId, principal);
        var formAnswerData = answerMapper.toFormAnswerData(formAnswer);
        return answerService.updateForm(answerId, formAnswerData);
    }

    @MutationMapping
    public AnswerEntity setAnswerStatusToSent(
            @Argument UUID answerId,
            Principal principal
    ) {
        log.info("""
                        Set approvement answer status to sent for [{}]
                        with login [{}]
                        for answer id [{}]
                        """,
                principal, principal.getName(), answerId
        );
        var userId = authenticator.getLogin(principal);
        setAnswerStatusToSentUserRateLimiter.throwIfRateLimitExceeded(userId, () -> new ValidationException(
                "Rate limit exceeded!"
        ));
        authenticator.authUserByAnswer(answerId, principal);
        return answerService.setStatusToSent(answerId, userId);
    }
}

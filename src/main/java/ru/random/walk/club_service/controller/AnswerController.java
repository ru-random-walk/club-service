package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.AnswerMapper;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.graphql.types.FormAnswerInput;
import ru.random.walk.club_service.service.AnswerService;
import ru.random.walk.club_service.util.StubDataUtil;

import java.security.Principal;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class AnswerController {
    private final AnswerMapper answerMapper;
    private final AnswerService answerService;

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
        return answerService.createMembersConfirm(approvementId, principal);
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
        return answerService.createForm(approvementId, formAnswerData, principal);
    }

    @MutationMapping
    public AnswerEntity updateAnswerForm(
            @Argument UUID approvementId,
            @Argument FormAnswerInput formAnswer,
            Principal principal
    ) {
        log.info("""
                        Update approvement answer form for [{}]
                        with login [{}]
                        for approvement id [{}]
                        with form answer [{}]
                        """,
                principal, principal.getName(), approvementId, formAnswer
        );
        var formAnswerData = answerMapper.toFormAnswerData(formAnswer);
        return StubDataUtil.answerFormEntityWith(approvementId, formAnswerData);
    }

    @MutationMapping
    public AnswerEntity setAnswerFormStatusToSent(
            @Argument UUID answerId,
            Principal principal
    ) {
        log.info("""
                        Set approvement answer form status to sent for [{}]
                        with login [{}]
                        for answer id [{}]
                        """,
                principal, principal.getName(), answerId
        );
        return StubDataUtil.answerFormEntityWith(answerId, AnswerStatus.SENT);
    }

    @MutationMapping
    public AnswerEntity setAnswerMembersConfirmStatusToSent(
            @Argument UUID answerId,
            Principal principal
    ) {
        log.info("""
                        Set approvement answer members confirm status to sent for [{}]
                        with login [{}]
                        for answer id [{}]
                        """,
                principal, principal.getName(), answerId
        );
        return StubDataUtil.answerFormEntityWith(answerId, AnswerStatus.SENT);
    }
}

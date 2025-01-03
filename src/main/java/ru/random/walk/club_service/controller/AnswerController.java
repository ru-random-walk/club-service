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
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class AnswerController {
    private final AnswerMapper answerMapper;

    @MutationMapping
    AnswerEntity createApprovementAnswerMembersConfirm(@Argument UUID approvementId) {
        log.info("""
                        Create approvement answer members confirm
                        for approvement id [{}]
                        """,
                approvementId
        );
        return StubDataUtil.answerMembersConfirmEntityWith(approvementId);
    }

    @MutationMapping
    AnswerEntity createApprovementAnswerForm(
            @Argument UUID approvementId,
            @Argument FormAnswerInput formAnswer
    ) {
        log.info("""
                        Create approvement answer form
                        for approvement id [{}]
                        with form answer [{}]
                        """,
                approvementId, formAnswer
        );
        var formAnswerData = answerMapper.toFormAnswerData(formAnswer);
        return StubDataUtil.answerFormEntityWith(approvementId, formAnswerData);
    }

    @MutationMapping
    AnswerEntity setApprovementAnswerFormStatusToSent(@Argument UUID approvementId) {
        log.info("""
                        Set approvement answer form status to sent
                        for approvement id [{}]
                        """,
                approvementId
        );
        return StubDataUtil.answerFormEntityWith(approvementId, AnswerStatus.SENT);
    }

    @MutationMapping
    AnswerEntity updateApprovementAnswerForm(
            @Argument UUID approvementId,
            @Argument FormAnswerInput formAnswer
    ) {
        log.info("""
                        Update approvement answer form
                        for approvement id [{}]
                        with form answer [{}]
                        """,
                approvementId, formAnswer
        );
        var formAnswerData = answerMapper.toFormAnswerData(formAnswer);
        return StubDataUtil.answerFormEntityWith(approvementId, formAnswerData);
    }
}

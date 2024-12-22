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
    AnswerEntity createTestAnswerMembersConfirm(@Argument UUID testId) {
        log.info("""
                        Create test answer members confirm
                        for test id [{}]
                        """,
                testId
        );
        return StubDataUtil.answerMembersConfirmEntityWith(testId);
    }

    @MutationMapping
    AnswerEntity createTestAnswerForm(
            @Argument UUID testId,
            @Argument FormAnswerInput formAnswer
    ) {
        log.info("""
                        Create test answer form
                        for test id [{}]
                        with form answer [{}]
                        """,
                testId, formAnswer
        );
        var formAnswerData = answerMapper.toFormAnswerData(formAnswer);
        return StubDataUtil.answerFormEntityWith(testId, formAnswerData);
    }

    @MutationMapping
    AnswerEntity setTestAnswerFormStatusToSent(@Argument UUID testId) {
        log.info("""
                        Set test answer form status to sent
                        for test id [{}]
                        """,
                testId
        );
        return StubDataUtil.answerFormEntityWith(testId, AnswerStatus.SENT);
    }

    @MutationMapping
    AnswerEntity updateTestAnswerForm(
            @Argument UUID testId,
            @Argument FormAnswerInput formAnswer
    ) {
        log.info("""
                        Update test answer form
                        for test id [{}]
                        with form answer [{}]
                        """,
                testId, formAnswer
        );
        var formAnswerData = answerMapper.toFormAnswerData(formAnswer);
        return StubDataUtil.answerFormEntityWith(testId, formAnswerData);
    }
}

package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.ApprovementMapper;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.graphql.types.FormInput;
import ru.random.walk.club_service.model.graphql.types.MembersConfirmInput;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class ApprovementController {
    private final ApprovementMapper approvementMapper;

    @MutationMapping
    ApprovementEntity addClubApprovementMembersConfirm(
            @Argument UUID clubId,
            @Argument MembersConfirmInput membersConfirm
    ) {
        log.info("""
                        Add club test members confirm
                        for club id [{}]
                        with membersConfirm [{}]
                        """,
                clubId, membersConfirm
        );
        var membersConfirmTestData = approvementMapper.toMembersConfirmApprovementData(membersConfirm);
        return StubDataUtil.membersConfirmTestEntityWith(membersConfirmTestData);
    }

    @MutationMapping
    ApprovementEntity addClubTestForm(
            @Argument UUID clubId,
            @Argument FormInput form
    ) {
        log.info("""
                        Add club test members confirm
                        for club id [{}]
                        with form [{}]
                        """,
                clubId, form
        );
        var formTestData = approvementMapper.toFormApprovementData(form);
        return StubDataUtil.formTestEntityWith(formTestData);
    }

    @MutationMapping
    ApprovementEntity updateClubTestMembersConfirm(
            @Argument UUID clubId,
            @Argument UUID testId,
            @Argument MembersConfirmInput membersConfirm
    ) {
        log.info("""
                        Update club test members confirm
                        for club id [{}]
                        for test id [{}]
                        with membersConfirm [{}]
                        """,
                clubId, testId, membersConfirm
        );
        var membersConfirmTestData = approvementMapper.toMembersConfirmApprovementData(membersConfirm);
        return StubDataUtil.membersConfirmTestEntityWith(membersConfirmTestData);
    }

    @MutationMapping
    ApprovementEntity updateClubTestForm(
            @Argument UUID clubId,
            @Argument UUID testId,
            @Argument FormInput form
    ) {
        log.info("""
                        Update club test form
                        for club id [{}]
                        for test id [{}]
                        with form [{}]
                        """,
                clubId, testId, form
        );
        var formTestData = approvementMapper.toFormApprovementData(form);
        return StubDataUtil.formTestEntityWith(formTestData);
    }

    @MutationMapping
    UUID removeClubTest(
            @Argument UUID clubId,
            @Argument UUID testId
    ) {
        log.info("""
                        Remove club test
                        for club id [{}]
                        with testId [{}]
                        """,
                clubId, testId
        );
        return testId;
    }
}

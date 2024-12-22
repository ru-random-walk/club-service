package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.TestMapper;
import ru.random.walk.club_service.model.entity.TestEntity;
import ru.random.walk.club_service.model.graphql.types.FormInput;
import ru.random.walk.club_service.model.graphql.types.MembersConfirmInput;
import ru.random.walk.club_service.util.StabDataUtil;

import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class TestController {
    private final TestMapper testMapper;

    @MutationMapping
    TestEntity addClubTestMembersConfirm(
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
        var membersConfirmTestData = testMapper.toMembersConfirmTestData(membersConfirm);
        return StabDataUtil.membersConfirmTestEntityWith(membersConfirmTestData);
    }

    @MutationMapping
    TestEntity addClubTestForm(
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
        var formTestData = testMapper.toFormTestData(form);
        return StabDataUtil.formTestEntityWith(formTestData);
    }

    @MutationMapping
    TestEntity updateClubTestMembersConfirm(
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
        var membersConfirmTestData = testMapper.toMembersConfirmTestData(membersConfirm);
        return StabDataUtil.membersConfirmTestEntityWith(membersConfirmTestData);
    }

    @MutationMapping
    TestEntity updateClubTestForm(
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
        var formTestData = testMapper.toFormTestData(form);
        return StabDataUtil.formTestEntityWith(formTestData);
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

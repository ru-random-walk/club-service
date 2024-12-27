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
import ru.random.walk.club_service.util.StubDataUtil;

import java.security.Principal;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class TestController {
    private final TestMapper testMapper;

    @MutationMapping
    TestEntity addClubTestMembersConfirm(
            @Argument UUID clubId,
            @Argument MembersConfirmInput membersConfirm,
            Principal principal
    ) {
        log.info("""
                        Add club test members confirm for [{}]
                        with login [{}]
                        for club id [{}]
                        with membersConfirm [{}]
                        """,
                principal, principal.getName(), clubId, membersConfirm
        );
        var membersConfirmTestData = testMapper.toMembersConfirmTestData(membersConfirm);
        return StubDataUtil.membersConfirmTestEntityWith(membersConfirmTestData);
    }

    @MutationMapping
    TestEntity addClubTestForm(
            @Argument UUID clubId,
            @Argument FormInput form,
            Principal principal
    ) {
        log.info("""
                        Add club test members confirm for [{}]
                        with login [{}]
                        for club id [{}]
                        with form [{}]
                        """,
                principal, principal.getName(), clubId, form
        );
        var formTestData = testMapper.toFormTestData(form);
        return StubDataUtil.formTestEntityWith(formTestData);
    }

    @MutationMapping
    TestEntity updateClubTestMembersConfirm(
            @Argument UUID clubId,
            @Argument UUID testId,
            @Argument MembersConfirmInput membersConfirm,
            Principal principal
    ) {
        log.info("""
                        Update club test members confirm for [{}]
                        with login [{}]
                        for club id [{}]
                        for test id [{}]
                        with membersConfirm [{}]
                        """,
                principal, principal.getName(), clubId, testId, membersConfirm
        );
        var membersConfirmTestData = testMapper.toMembersConfirmTestData(membersConfirm);
        return StubDataUtil.membersConfirmTestEntityWith(membersConfirmTestData);
    }

    @MutationMapping
    TestEntity updateClubTestForm(
            @Argument UUID clubId,
            @Argument UUID testId,
            @Argument FormInput form,
            Principal principal
    ) {
        log.info("""
                        Update club test form for [{}]
                        with login [{}]
                        for club id [{}]
                        for test id [{}]
                        with form [{}]
                        """,
                principal, principal.getName(), clubId, testId, form
        );
        var formTestData = testMapper.toFormTestData(form);
        return StubDataUtil.formTestEntityWith(formTestData);
    }

    @MutationMapping
    UUID removeClubTest(
            @Argument UUID clubId,
            @Argument UUID testId,
            Principal principal
    ) {
        log.info("""
                        Remove club test for [{}]
                        with login [{}]
                        for club id [{}]
                        with testId [{}]
                        """,
                principal, principal.getName(), clubId, testId
        );
        return testId;
    }
}

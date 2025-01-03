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
    public ApprovementEntity addClubApprovementMembersConfirm(
            @Argument UUID clubId,
            @Argument MembersConfirmInput membersConfirm
    ) {
        log.info("""
                        Add club approvement members confirm
                        for club id [{}]
                        with membersConfirm [{}]
                        """,
                clubId, membersConfirm
        );
        var membersConfirmApprovementData = approvementMapper.toMembersConfirmApprovementData(membersConfirm);
        return StubDataUtil.membersConfirmApprovementEntityWith(membersConfirmApprovementData);
    }

    @MutationMapping
    public ApprovementEntity addClubApprovementForm(
            @Argument UUID clubId,
            @Argument FormInput form
    ) {
        log.info("""
                        Add club approvement members confirm
                        for club id [{}]
                        with form [{}]
                        """,
                clubId, form
        );
        var formApprovementData = approvementMapper.toFormApprovementData(form);
        return StubDataUtil.formApprovementEntityWith(formApprovementData);
    }

    @MutationMapping
    public ApprovementEntity updateClubApprovementMembersConfirm(
            @Argument UUID clubId,
            @Argument UUID approvementId,
            @Argument MembersConfirmInput membersConfirm
    ) {
        log.info("""
                        Update club approvement members confirm
                        for club id [{}]
                        for approvement id [{}]
                        with membersConfirm [{}]
                        """,
                clubId, approvementId, membersConfirm
        );
        var membersConfirmApprovementData = approvementMapper.toMembersConfirmApprovementData(membersConfirm);
        return StubDataUtil.membersConfirmApprovementEntityWith(membersConfirmApprovementData);
    }

    @MutationMapping
    public ApprovementEntity updateClubApprovementForm(
            @Argument UUID clubId,
            @Argument UUID approvementId,
            @Argument FormInput form
    ) {
        log.info("""
                        Update club approvement form
                        for club id [{}]
                        for approvement id [{}]
                        with form [{}]
                        """,
                clubId, approvementId, form
        );
        var formApprovementData = approvementMapper.toFormApprovementData(form);
        return StubDataUtil.formApprovementEntityWith(formApprovementData);
    }

    @MutationMapping
    public UUID removeClubApprovement(
            @Argument UUID clubId,
            @Argument UUID approvementId
    ) {
        log.info("""
                        Remove club approvement
                        for club id [{}]
                        with approvementId [{}]
                        """,
                clubId, approvementId
        );
        return approvementId;
    }
}

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
import ru.random.walk.club_service.service.ApprovementService;
import ru.random.walk.club_service.service.Authenticator;
import ru.random.walk.club_service.util.StubDataUtil;

import java.security.Principal;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class ApprovementController {
    private final ApprovementService approvementService;
    private final ApprovementMapper approvementMapper;
    private final Authenticator authenticator;

    @MutationMapping
    public ApprovementEntity addClubApprovementMembersConfirm(
            @Argument UUID clubId,
            @Argument MembersConfirmInput membersConfirm,
            Principal principal
    ) {
        log.info("""
                        Add club approvement members confirm for [{}]
                        with login [{}]
                        for club id [{}]
                        with membersConfirm [{}]
                        """,
                principal, principal.getName(), clubId, membersConfirm
        );
        var membersConfirmApprovementData = approvementMapper.toMembersConfirmApprovementData(membersConfirm);
        authenticator.authAdminByClubId(principal, clubId);
        return approvementService.addForClub(membersConfirmApprovementData, clubId);
    }

    @MutationMapping
    public ApprovementEntity addClubApprovementForm(
            @Argument UUID clubId,
            @Argument FormInput form,
            Principal principal
    ) {
        log.info("""
                        Add club approvement members confirm for [{}]
                        with login [{}]
                        for club id [{}]
                        with form [{}]
                        """,
                principal, principal.getName(), clubId, form
        );
        var formApprovementData = approvementMapper.toFormApprovementData(form);
        authenticator.authAdminByClubId(principal, clubId);
        return approvementService.addForClub(formApprovementData, clubId);
    }

    @MutationMapping
    public ApprovementEntity updateClubApprovementMembersConfirm(
            @Argument UUID clubId,
            @Argument UUID approvementId,
            @Argument MembersConfirmInput membersConfirm,
            Principal principal
    ) {
        log.info("""
                        Update club approvement members confirm for [{}]
                        with login [{}]
                        for club id [{}]
                        for approvement id [{}]
                        with membersConfirm [{}]
                        """,
                principal, principal.getName(), clubId, approvementId, membersConfirm
        );
        var membersConfirmApprovementData = approvementMapper.toMembersConfirmApprovementData(membersConfirm);
        return StubDataUtil.membersConfirmApprovementEntityWith(membersConfirmApprovementData);
    }

    @MutationMapping
    public ApprovementEntity updateClubApprovementForm(
            @Argument UUID clubId,
            @Argument UUID approvementId,
            @Argument FormInput form,
            Principal principal
    ) {
        log.info("""
                        Update club approvement form for [{}]
                        with login [{}]
                        for club id [{}]
                        for approvement id [{}]
                        with form [{}]
                        """,
                principal, principal.getName(), clubId, approvementId, form
        );
        var formApprovementData = approvementMapper.toFormApprovementData(form);
        return StubDataUtil.formApprovementEntityWith(formApprovementData);
    }

    @MutationMapping
    public UUID removeClubApprovement(
            @Argument UUID clubId,
            @Argument UUID approvementId,
            Principal principal
    ) {
        log.info("""
                        Remove club approvement for [{}]
                        with login [{}]
                        for club id [{}]
                        with approvementId [{}]
                        """,
                principal, principal.getName(), clubId, approvementId
        );
        return approvementId;
    }
}

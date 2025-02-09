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
                        Add club approvement form for [{}]
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
            @Argument UUID approvementId,
            @Argument MembersConfirmInput membersConfirm,
            Principal principal
    ) {
        log.info("""
                        Update club approvement members confirm for [{}]
                        with login [{}]
                        for approvement id [{}]
                        with membersConfirm [{}]
                        """,
                principal, principal.getName(), approvementId, membersConfirm
        );
        var membersConfirmApprovementData = approvementMapper.toMembersConfirmApprovementData(membersConfirm);
        authenticator.authAdminByApprovementId(principal, approvementId);
        return approvementService.update(membersConfirmApprovementData, approvementId);
    }

    @MutationMapping
    public ApprovementEntity updateClubApprovementForm(
            @Argument UUID approvementId,
            @Argument FormInput form,
            Principal principal
    ) {
        log.info("""
                        Update club approvement form for [{}]
                        with login [{}]
                        for approvement id [{}]
                        with form [{}]
                        """,
                principal, principal.getName(), approvementId, form
        );
        var formApprovementData = approvementMapper.toFormApprovementData(form);
        authenticator.authAdminByApprovementId(principal, approvementId);
        return approvementService.update(formApprovementData, approvementId);
    }

    @MutationMapping
    public UUID removeClubApprovement(
            @Argument UUID approvementId,
            Principal principal
    ) {
        log.info("""
                        Remove club approvement for [{}]
                        with login [{}]
                        with approvementId [{}]
                        """,
                principal, principal.getName(), approvementId
        );
        authenticator.authAdminByApprovementId(principal, approvementId);
        return approvementService.delete(approvementId);
    }
}

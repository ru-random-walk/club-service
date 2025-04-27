package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.ConfirmationStatus;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.service.ConfirmationService;
import ru.random.walk.club_service.service.MemberService;
import ru.random.walk.club_service.service.auth.Authenticator;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
@PreAuthorize("hasAuthority('DEFAULT_USER')")
public class ConfirmationController {
    private final ConfirmationService confirmationService;
    private final MemberService memberService;
    private final Authenticator authenticator;

    @QueryMapping
    public List<ConfirmationEntity> getMyWaitingConfirmations(
            @Argument UUID userId,
            @Nullable @Argument PaginationInput confirmationsPagination,
            Principal principal
    ) {
        authenticator.authUserById(userId, principal);
        var pagination = Optional.ofNullable(confirmationsPagination)
                .orElse(new PaginationInput(0, 20));
        return confirmationService.getUserWaitingConfirmations(userId, pagination);
    }

    @QueryMapping
    public List<ConfirmationEntity> getApproverWaitingConfirmations(
            @Argument UUID approverId,
            @Nullable @Argument PaginationInput confirmationsPagination,
            Principal principal
    ) {
        authenticator.authUserById(approverId, principal);
        var pagination = Optional.ofNullable(confirmationsPagination)
                .orElse(new PaginationInput(0, 30));
        return confirmationService.getApproverWaitingConfirmations(approverId, pagination);
    }

    @MutationMapping
    public MemberEntity tryJoinInClub(
            @Argument UUID userId,
            @Argument UUID clubId,
            Principal principal
    ) {
        authenticator.authUserById(userId, principal);
        return memberService.addInClubIfAllTestPassed(userId, clubId).orElseThrow(NotFoundException::new);
    }

    @MutationMapping
    public ConfirmationEntity approveConfirmation(
            @Argument UUID confirmationId,
            Principal principal
    ) {
        authenticator.authApproverByConfirmation(confirmationId, principal);
        return confirmationService.updateConfirmationStatus(confirmationId, ConfirmationStatus.APPLIED);
    }

    @MutationMapping
    public ConfirmationEntity rejectConfirmation(
            @Argument UUID confirmationId,
            Principal principal
    ) {
        authenticator.authApproverByConfirmation(confirmationId, principal);
        return confirmationService.updateConfirmationStatus(confirmationId, ConfirmationStatus.REJECTED);
    }
}

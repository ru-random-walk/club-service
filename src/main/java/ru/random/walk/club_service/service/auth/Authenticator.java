package ru.random.walk.club_service.service.auth;

import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;

import java.security.Principal;
import java.util.UUID;

public interface Authenticator {
    void authAdminByClubId(Principal principal, UUID clubId);

    void authAdminByApprovementId(Principal principal, UUID approvementId);

    AnswerEntity authUserByAnswerAndGet(UUID answerId, Principal principal);

    void authUserById(UUID userId, Principal principal);

    UUID getLogin(Principal principal);

    ConfirmationEntity authApproverByConfirmationAndGet(UUID confirmationId, Principal principal);
}

package ru.random.walk.club_service.service.auth.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.AuthenticationException;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ConfirmationRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.auth.Authenticator;

import java.security.Principal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthenticatorImpl implements Authenticator {
    private final MemberRepository memberRepository;
    private final ApprovementRepository approvementRepository;
    private final AnswerRepository answerRepository;
    private final ConfirmationRepository confirmationRepository;

    @Override
    public void authAdminByClubId(Principal principal, UUID clubId) {
        var login = getLogin(principal);
        var member = memberRepository.findByIdAndClubId(login, clubId)
                .orElseThrow(() -> new AuthenticationException("You are not become member of given club!"));
        if (member.getRole() != MemberRole.ADMIN) {
            throw new AuthenticationException("You are not authorized to access this club members!");
        }
    }

    @Override
    public void authAdminByApprovementId(Principal principal, UUID approvementId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such answerId not found!"));
        authAdminByClubId(principal, approvement.getClubId());
    }

    @Override
    public AnswerEntity authUserByAnswerAndGet(UUID answerId, Principal principal) {
        var answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new NotFoundException("Answer with such answerId not found!"));
        var userLogin = getLogin(principal);
        if (!answer.getUserId().equals(userLogin)) {
            throw new AuthenticationException("You do not have access to update this answer!");
        }
        return answer;
    }

    @Override
    public void authUserById(UUID userId, Principal principal) {
        var login = getLogin(principal);
        if (!login.equals(userId)) {
            throw new AuthenticationException("You do not have the same login with userId!");
        }
    }

    @Override
    public UUID getLogin(Principal principal) {
        return UUID.fromString(principal.getName());
    }

    @Override
    public ConfirmationEntity authApproverByConfirmationAndGet(UUID confirmationId, Principal principal) {
        var confirmation = confirmationRepository.findById(confirmationId)
                .orElseThrow(() -> new NotFoundException("Confirmation with such confirmationId not found!"));
        var login = getLogin(principal);
        if (!confirmation.getApproverId().equals(login)) {
            throw new AuthenticationException("You do not have access to approve this confirmation!");
        }
        return confirmation;
    }
}

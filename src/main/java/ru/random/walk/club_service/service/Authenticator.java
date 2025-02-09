package ru.random.walk.club_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.AuthenticationException;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.MemberRepository;

import java.security.Principal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class Authenticator {
    private final MemberRepository memberRepository;
    private final ApprovementRepository approvementRepository;

    public void authAdminByClubId(Principal principal, UUID clubId) {
        var login = UUID.fromString(principal.getName());
        var member = memberRepository.findByIdAndClubId(login, clubId)
                .orElseThrow(() -> new AuthenticationException("You are not become member of given club!"));
        if (member.getRole() != MemberRole.ADMIN) {
            throw new AuthenticationException("You are not authorized to access this club members!");
        }
    }

    public void authAdminByApprovementId(Principal principal, UUID approvementId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        authAdminByClubId(principal, approvement.getClubId());
    }
}

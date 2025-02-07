package ru.random.walk.club_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.AuthenticationException;
import ru.random.walk.club_service.repository.MembersRepository;

import java.security.Principal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class Authenticator {
    private final MembersRepository membersRepository;

    public void authAdminByClubId(Principal principal, UUID clubId) {
        var login = UUID.fromString(principal.getName());
        var member = membersRepository.findByIdAndClubId(login, clubId)
                .orElseThrow(() -> new AuthenticationException("You are not become member of given club!"));
        if (member.getRole() != MemberRole.ADMIN) {
            throw new AuthenticationException("You are not authorized to access this club members!");
        }
    }
}

package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.MemberMapper;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.graphql.types.MemberRole;
import ru.random.walk.club_service.service.MemberService;
import ru.random.walk.club_service.service.auth.impl.AuthenticatorImpl;

import java.security.Principal;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
@PreAuthorize("hasAuthority('DEFAULT_USER')")
public class MemberController {
    private final MemberService memberService;
    private final MemberMapper memberMapper;
    private final AuthenticatorImpl authenticator;

    @MutationMapping
    public MemberEntity changeMemberRole(
            @Argument UUID clubId,
            @Argument UUID memberId,
            @Argument MemberRole role,
            Principal principal
    ) {
        log.info("""
                        Change member role for [{}]
                        with login [{}]
                        for club id [{}]
                        member id [{}]
                        with role [{}]
                        """,
                principal, principal.getName(), clubId, memberId, role
        );
        authenticator.authAdminByClubId(principal, clubId);
        var memberRole = memberMapper.toDomainMemberRole(role);
        return memberService.changeRole(memberId, clubId, memberRole);
    }

    @MutationMapping
    public UUID removeMemberFromClub(
            @Argument UUID clubId,
            @Argument UUID memberId,
            Principal principal
    ) {
        log.info("""
                        Remove member with id [{}] for [{}]
                        with login [{}]
                        from club with id [{}]
                        """,
                principal, principal.getName(), clubId, memberId
        );
        authenticator.authAdminByClubId(principal, clubId);
        return memberService.removeFromClub(memberId, clubId);
    }

    @MutationMapping
    public MemberEntity addMemberInClub(
            @Argument UUID clubId,
            @Argument UUID memberId,
            Principal principal
    ) {
        log.info("""
                        Add member with id [{}] for [{}]
                        with login [{}]
                        in club with id [{}]
                        """,
                principal, principal.getName(), clubId, memberId
        );
        authenticator.authAdminByClubId(principal, clubId);
        return memberService.addInClub(memberId, clubId);
    }
}

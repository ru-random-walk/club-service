package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.Authenticator;
import ru.random.walk.club_service.service.ClubService;

import java.security.Principal;
import java.util.Collections;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClubServiceImpl implements ClubService {
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final Authenticator authenticator;

    @Override
    public ClubEntity getClubById(
            UUID clubId,
            PaginationInput membersPagination,
            boolean membersIsRequired,
            Principal principal
    ) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Club with such id not found!"));
        if (membersIsRequired) {
            authenticator.authAdminByClubId(principal, clubId);
            var membersPageable = PageRequest.of(membersPagination.getPage(), membersPagination.getSize());
            var membersPage = memberRepository.findAllByClubId(clubId, membersPageable);
            club.setMembers(membersPage.getContent());
        }
        return club;
    }

    @Override
    @Transactional
    public ClubEntity createClub(String clubName, Principal principal) {
        var club = clubRepository.save(ClubEntity.builder()
                .name(clubName)
                .build());
        var adminLogin = UUID.fromString(principal.getName());
        var adminMember = memberRepository.save(MemberEntity.builder()
                .id(adminLogin)
                .clubId(club.getId())
                .role(MemberRole.ADMIN)
                .build());
        club.setMembers(Collections.singletonList(adminMember));
        return club;
    }
}

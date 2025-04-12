package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.projection.ClubIdToMemberRoleToCountProjection;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.ClubService;
import ru.random.walk.club_service.service.auth.Authenticator;
import ru.random.walk.club_service.util.Pair;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClubServiceImpl implements ClubService {
    private final static int MAX_CLUB_COUNT_BY_USER = 3;

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
                .orElseThrow(() -> new NotFoundException("Club with such answerId not found!"));
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
        var adminLogin = UUID.fromString(principal.getName());
        var userClubCount = memberRepository.countByIdAndRole(adminLogin, MemberRole.ADMIN);
        if (userClubCount >= MAX_CLUB_COUNT_BY_USER) {
            throw new ValidationException("You are reached maximum count of clubs!");
        }
        var club = clubRepository.save(ClubEntity.builder()
                .name(clubName)
                .build());
        var adminMember = memberRepository.save(MemberEntity.builder()
                .id(adminLogin)
                .clubId(club.getId())
                .role(MemberRole.ADMIN)
                .build());
        club.setMembers(Collections.singletonList(adminMember));
        return club;
    }

    @Override
    public Map<ClubEntity, Integer> getClubToApproversNumber(List<ClubEntity> clubs) {
        var clubIds = clubs.stream().map(ClubEntity::getId).toList();
        Map<Pair<UUID, MemberRole>, Integer> clubMembersRoleToCount =
                memberRepository.findAllClubIdToRoleToCountByClubIds(clubIds).stream()
                        .collect(Collectors.toMap(
                                row -> Pair.of(row.clubId(), row.memberRole()),
                                ClubIdToMemberRoleToCountProjection::count
                        ));
        return clubs.stream().collect(Collectors.toMap(
                Function.identity(),
                club -> getApproversNumber(club, clubMembersRoleToCount)
        ));
    }

    private static Integer getApproversNumber(ClubEntity club, Map<Pair<UUID, MemberRole>, Integer> clubMembersRoleToCount) {
        var inspectors = clubMembersRoleToCount.getOrDefault(Pair.of(club.getId(), MemberRole.INSPECTOR), 0);
        var admins = clubMembersRoleToCount.getOrDefault(Pair.of(club.getId(), MemberRole.ADMIN), 0);
        return inspectors + admins;
    }
}

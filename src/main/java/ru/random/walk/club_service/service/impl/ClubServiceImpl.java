package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.client.StorageClient;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.projection.ClubIdToMemberRoleToCountProjection;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.graphql.types.PhotoUrl;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.ClubService;
import ru.random.walk.club_service.service.auth.Authenticator;
import ru.random.walk.club_service.util.Pair;
import ru.random.walk.config.StorageProperties;
import ru.random.walk.model.PathKey;

import java.io.InputStream;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClubServiceImpl implements ClubService {
    private final static int MAX_CLUB_PHOTO_SIZE_IN_BYTES = 5 * 1024 * 1024;
    private final static int MAX_CLUB_COUNT_BY_USER = 3;

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final Authenticator authenticator;
    private final StorageClient storageClient;
    private final StorageProperties storageProperties;

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
    public ClubEntity createClub(String clubName, @Nullable String description, Principal principal) {
        var adminLogin = UUID.fromString(principal.getName());
        var userClubCount = memberRepository.countByIdAndRole(adminLogin, MemberRole.ADMIN);
        if (userClubCount >= MAX_CLUB_COUNT_BY_USER) {
            throw new ValidationException("You are reached maximum count of clubs!");
        }
        var club = clubRepository.save(ClubEntity.builder()
                .description(description)
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
    public List<Integer> getClubToApproversNumber(List<ClubEntity> clubs) {
        var clubIds = clubs.stream().map(ClubEntity::getId).toList();
        Map<Pair<UUID, MemberRole>, Integer> clubMembersRoleToCount = memberRepository
                .findAllClubIdToRoleToCountByClubIds(clubIds).stream()
                .collect(Collectors.toMap(
                        row -> Pair.of(row.clubId(), row.memberRole()),
                        ClubIdToMemberRoleToCountProjection::count
                ));
        return clubs.stream()
                .map(club -> getApproversNumber(club, clubMembersRoleToCount))
                .toList();
    }

    @Override
    public PhotoUrl uploadPhotoForClub(UUID clubId, InputStream inputFile) {
        var club = updatePhotoVersionAndGet(clubId);
        var url = storageClient.uploadPngAndGetUrl(inputFile, Map.of(PathKey.CLUB_ID, club.getId()));
        return new PhotoUrl(club.getId().toString(), url, storageProperties.temporaryUrlTtlInMinutes());
    }

    @NotNull
    private ClubEntity updatePhotoVersionAndGet(UUID clubId) {
        var club = clubRepository.findById(clubId).orElseThrow();
        club.setPhotoVersion(Optional.ofNullable(club.getPhotoVersion()).orElse(0) + 1);
        clubRepository.save(club);
        return club;
    }

    private static Integer getApproversNumber(
            ClubEntity club,
            Map<Pair<UUID, MemberRole>, Integer> clubMembersRoleToCount
    ) {
        var inspectors = clubMembersRoleToCount.getOrDefault(Pair.of(club.getId(), MemberRole.INSPECTOR), 0);
        var admins = clubMembersRoleToCount.getOrDefault(Pair.of(club.getId(), MemberRole.ADMIN), 0);
        return inspectors + admins;
    }
}

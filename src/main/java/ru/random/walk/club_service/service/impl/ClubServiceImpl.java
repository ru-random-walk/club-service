package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.client.StorageClient;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.projection.ClubIdToMemberRoleToCountProjection;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.graphql.types.PhotoUrl;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.ClubService;
import ru.random.walk.club_service.service.MemberService;
import ru.random.walk.club_service.util.Pair;
import ru.random.walk.config.StorageProperties;
import ru.random.walk.util.PathBuilder;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClubServiceImpl implements ClubService {
    private final static int MAX_CLUB_COUNT_BY_USER = 3;

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ApprovementRepository approvementRepository;
    private final AnswerRepository answerRepository;

    private final MemberService memberService;
    private final StorageClient storageClient;
    private final StorageProperties storageProperties;

    @Override
    public ClubEntity getClubById(
            UUID clubId,
            PaginationInput membersPagination,
            boolean membersIsRequired
    ) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Club with such answerId not found!"));
        if (membersIsRequired) {
            var membersPageable = PageRequest.of(membersPagination.getPage(), membersPagination.getSize());
            var membersPage = memberRepository.findAllByClubId(clubId, membersPageable);
            club.setMembers(membersPage.getContent());
        }
        return club;
    }

    @Override
    @Transactional
    public ClubEntity createClub(String clubName, @Nullable String description, UUID adminLogin) {
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
        club.getMembers().add(adminMember);
        return clubRepository.save(club);
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
    @Transactional
    public ClubEntity createClubWithApprovement(
            String name,
            String description,
            ApprovementData approvementData,
            UUID adminLogin
    ) {
        var club = createClub(name, description, adminLogin);
        var approvementType = switch (approvementData) {
            case MembersConfirmApprovementData ignored -> ApprovementType.MEMBERS_CONFIRM;
            case FormApprovementData ignored -> ApprovementType.FORM;
            default -> throw new IllegalStateException("Unexpected value: " + approvementData);
        };
        var approvement = approvementRepository.save(ApprovementEntity.builder()
                .club(club)
                .clubId(club.getId())
                .data(approvementData)
                .type(approvementType)
                .build());
        club.getApprovements().add(approvement);
        return clubRepository.save(club);
    }

    @Override
    @Transactional
    public UUID removeClubWithAllItsData(UUID clubId) {
        var club = clubRepository.findById(clubId).orElseThrow();
        answerRepository.deleteAllByClubId(clubId);
        approvementRepository.deleteAllByClubId(club.getId());
        memberService.deleteAllByClubId(club.getId());
        clubRepository.delete(club);
        storageClient.delete(buildPhotoFileKey(clubId));
        return club.getId();
    }

    @Override
    @Transactional
    public PhotoUrl uploadPhotoForClub(UUID clubId, InputStream inputFile) {
        updateClubPhotoVersion(clubId);
        var url = storageClient.uploadAndGetUrl(inputFile, buildPhotoFileKey(clubId));
        return new PhotoUrl(clubId.toString(), url, storageProperties.temporaryUrlTtlInMinutes());
    }

    @Override
    public PhotoUrl getClubPhoto(UUID clubId) {
        var url = storageClient.getUrl(buildPhotoFileKey(clubId));
        return new PhotoUrl(clubId.toString(), url, storageProperties.temporaryUrlTtlInMinutes());
    }

    @Override
    @Transactional
    public ClubEntity removeClubPhoto(UUID clubId) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Club with such id not found!"));
        var photoFileKey = buildPhotoFileKey(clubId);
        if (storageClient.exist(photoFileKey)) {
            storageClient.delete(photoFileKey);
        }
        club.setPhotoVersion(null);
        return club;
    }

    private static String buildPhotoFileKey(UUID clubId) {
        return PathBuilder.init()
                .add("club-photo")
                .add(PathBuilder.Key.CLUB_ID, clubId)
                .build();
    }

    private void updateClubPhotoVersion(UUID clubId) {
        var club = clubRepository.findById(clubId).orElseThrow();
        club.setPhotoVersion(Optional.ofNullable(club.getPhotoVersion()).orElse(0) + 1);
        clubRepository.save(club);
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

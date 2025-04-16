package ru.random.walk.club_service.repository;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.random.walk.club_service.AbstractPostgresContainerTest;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.projection.ClubWithUserRoleProjection;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.entity.type.UserGroupRole;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ClubRepositoryTest extends AbstractPostgresContainerTest {
    private ClubRepository clubRepository;
    private ApprovementRepository approvementRepository;
    private AnswerRepository answerRepository;
    private UserRepository userRepository;
    private MemberRepository memberRepository;

    @Test
    void save() {
        var club = clubRepository.save(ClubEntity.builder()
                .name("Wild memory")
                .build());
        club.getApprovements().add(ApprovementEntity.builder()
                .data(StubDataUtil.membersConfirmApprovementData())
                .type(ApprovementType.MEMBERS_CONFIRM)
                .build());
    }

    @Test
    void findAllClubsWithRoleByUser() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("FIO")
                .build());
        var clubForPendingReview = clubRepository.save(ClubEntity.builder()
                .name("Wild memory")
                .build());
        var clubForAdmin = clubRepository.save(ClubEntity.builder()
                .name("Wild memory")
                .build());
        var clubForInspector = clubRepository.save(ClubEntity.builder()
                .name("Wild memory")
                .build());
        var clubForMember = clubRepository.save(ClubEntity.builder()
                .name("Wild memory")
                .build());
        clubRepository.save(ClubEntity.builder()
                .name("Wild memory")
                .build());
        memberRepository.saveAndFlush(MemberEntity.builder()
                .clubId(clubForAdmin.getId())
                .role(MemberRole.ADMIN)
                .id(user.getId())
                .build());
        memberRepository.saveAndFlush(MemberEntity.builder()
                .clubId(clubForInspector.getId())
                .role(MemberRole.INSPECTOR)
                .id(user.getId())
                .build());
        memberRepository.saveAndFlush(MemberEntity.builder()
                .clubId(clubForMember.getId())
                .role(MemberRole.USER)
                .id(user.getId())
                .build());
        var approvementForPendingReview = approvementRepository.saveAndFlush(ApprovementEntity.builder()
                .data(StubDataUtil.membersConfirmApprovementData())
                .type(ApprovementType.MEMBERS_CONFIRM)
                .clubId(clubForPendingReview.getId())
                .build());
        answerRepository.save(AnswerEntity.builder()
                .userId(user.getId())
                .approvement(approvementForPendingReview)
                .data(StubDataUtil.membersConfirmAnswerData())
                .status(AnswerStatus.SENT)
                .build());
        var approvementForMember = approvementRepository.saveAndFlush(ApprovementEntity.builder()
                .data(StubDataUtil.membersConfirmApprovementData())
                .type(ApprovementType.MEMBERS_CONFIRM)
                .clubId(clubForMember.getId())
                .build());
        answerRepository.save(AnswerEntity.builder()
                .userId(user.getId())
                .approvement(approvementForMember)
                .data(StubDataUtil.membersConfirmAnswerData())
                .status(AnswerStatus.PASSED)
                .build());
        var clubIdToUserRole = clubRepository.findAllClubsWithRoleByUser(user.getId()).stream()
                .collect(Collectors.toMap(
                        ClubWithUserRoleProjection::clubId,
                        ClubWithUserRoleProjection::role
                ));
        assertEquals(4, clubIdToUserRole.size());
        assertArrayEquals(
                new UserGroupRole[]{
                        UserGroupRole.PENDING_APPROVAL,
                        UserGroupRole.ADMIN,
                        UserGroupRole.INSPECTOR,
                        UserGroupRole.MEMBER
                },
                new UserGroupRole[]{
                        clubIdToUserRole.get(clubForPendingReview.getId()),
                        clubIdToUserRole.get(clubForAdmin.getId()),
                        clubIdToUserRole.get(clubForInspector.getId()),
                        clubIdToUserRole.get(clubForMember.getId())
                }
        );
    }
}
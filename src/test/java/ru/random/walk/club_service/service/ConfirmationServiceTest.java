package ru.random.walk.club_service.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.random.walk.club_service.AbstractPostgresContainerTest;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.entity.type.ConfirmationStatus;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
class ConfirmationServiceTest extends AbstractPostgresContainerTest {
    private final ConfirmationService confirmationService;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final AnswerRepository answerRepository;
    private final ApprovementRepository approvementRepository;
    private final MemberRepository memberRepository;

    @Test
    void testGetUserWaitingConfirmations() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Sapporo")
                .build());
        var approvers = userRepository.saveAllAndFlush(
                IntStream.range(0, 10)
                        .mapToObj(i -> UserEntity.builder()
                                .fullName("Inspector #%d".formatted(i))
                                .id(UUID.randomUUID())
                                .build())
                        .toList()
        );
        var club = clubRepository.save(ClubEntity.builder()
                .name("Kayak")
                .build());
        Random random = new Random();
        memberRepository.saveAllAndFlush(
                approvers.stream()
                        .map(inspector -> MemberEntity.builder()
                                .id(inspector.getId())
                                .role(random.nextBoolean() ? MemberRole.INSPECTOR : MemberRole.ADMIN)
                                .clubId(club.getId())
                                .build())
                        .toList()
        );
        var membersConfirmApprovementData = new MembersConfirmApprovementData(3);
        var approvement = approvementRepository.save(ApprovementEntity.builder()
                .data(membersConfirmApprovementData)
                .type(ApprovementType.MEMBERS_CONFIRM)
                .clubId(club.getId())
                .build());
        var answer = answerRepository.save(AnswerEntity.builder()
                .status(AnswerStatus.IN_PROGRESS)
                .data(MembersConfirmAnswerData.INSTANCE)
                .userId(user.getId())
                .approvement(approvement)
                .build());
        confirmationService.assignApprovers(
                ForReviewData.builder()
                        .answerId(answer.getId())
                        .answerData(answer.getData())
                        .approvementData(approvement.getData())
                        .userId(user.getId())
                        .clubId(club.getId())
                        .build(),
                membersConfirmApprovementData
        );

        var userWaitingConfirmations = confirmationService.getUserWaitingConfirmations(user.getId(), new PaginationInput(0, 20));
        assertNotNull(userWaitingConfirmations);
        assertEquals(3, userWaitingConfirmations.size());
        assertEquals(
                userWaitingConfirmations.stream()
                        .map(ConfirmationEntity::getStatus)
                        .collect(Collectors.toSet()),
                Set.of(ConfirmationStatus.WAITING)
        );
    }

    @Test
    void testGetApproverWaitingConfirmations() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Sapporo")
                .build());
        var approvers = userRepository.saveAllAndFlush(
                IntStream.range(0, 10)
                        .mapToObj(i -> UserEntity.builder()
                                .fullName("Inspector #%d".formatted(i))
                                .id(UUID.randomUUID())
                                .build())
                        .toList()
        );
        var club = clubRepository.save(ClubEntity.builder()
                .name("Kayak")
                .build());
        Random random = new Random();
        memberRepository.saveAllAndFlush(
                approvers.stream()
                        .map(inspector -> MemberEntity.builder()
                                .id(inspector.getId())
                                .role(random.nextBoolean() ? MemberRole.INSPECTOR : MemberRole.ADMIN)
                                .clubId(club.getId())
                                .build())
                        .toList()
        );
        var membersConfirmApprovementData = new MembersConfirmApprovementData(3);
        var approvement = approvementRepository.save(ApprovementEntity.builder()
                .data(membersConfirmApprovementData)
                .type(ApprovementType.MEMBERS_CONFIRM)
                .clubId(club.getId())
                .build());
        var answer = answerRepository.save(AnswerEntity.builder()
                .status(AnswerStatus.IN_PROGRESS)
                .data(MembersConfirmAnswerData.INSTANCE)
                .userId(user.getId())
                .approvement(approvement)
                .build());
        confirmationService.assignApprovers(
                ForReviewData.builder()
                        .answerId(answer.getId())
                        .answerData(answer.getData())
                        .approvementData(approvement.getData())
                        .userId(user.getId())
                        .clubId(club.getId())
                        .build(),
                membersConfirmApprovementData
        );

        var singleApproverConfirmationCount = 0;
        var standardPagination = new PaginationInput(0, 30);
        for (var approver : approvers) {
            var approverConfirmations = confirmationService.getApproverWaitingConfirmations(
                    approver.getId(),
                    standardPagination
            );
            try {
                assert 1 == approverConfirmations.size();
                var confirmation = approverConfirmations.getFirst();
                assert Objects.equals(user.getId(), confirmation.getUserId());
                assert ConfirmationStatus.WAITING == confirmation.getStatus();
                singleApproverConfirmationCount++;
            } catch (AssertionError ignored) {
            }
        }
        assertEquals(3, singleApproverConfirmationCount);
    }
}
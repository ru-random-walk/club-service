package ru.random.walk.club_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.MemberEntity.MemberId;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.entity.type.ConfirmationStatus;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.model.model.NotificationAdditionalDataKey;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.util.VirtualThreadUtil;
import ru.random.walk.dto.SendNotificationEvent;
import ru.random.walk.topic.EventTopic;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.random.walk.club_service.mockito.JsonArgMatcher.jsonEqAny;

@AllArgsConstructor(onConstructor_ = @__(@Autowired))
class ConfirmationServiceTest extends AbstractContainerTest {
    private final ConfirmationService confirmationService;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final AnswerRepository answerRepository;
    private final ApprovementRepository approvementRepository;
    private final MemberRepository memberRepository;

    private final ObjectMapper objectMapper;

    @MockitoSpyBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void testMemberConfirmationFlow() throws ExecutionException, InterruptedException {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Sapporo")
                .build());
        var approvers = userRepository.saveAllAndFlush(
                IntStream.range(0, 5)
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
        var membersConfirmApprovementData = new MembersConfirmApprovementData(3, 5);
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

        VirtualThreadUtil.awaitAllRunningTasks();

        for (int i = 0; i < 5; i++) {
            verify(kafkaTemplate, times(5))
                    .send(
                            eq(EventTopic.SEND_NOTIFICATION),
                            jsonEqAny(
                                    approvers.stream()
                                            .map(approver -> {
                                                try {
                                                    return objectMapper.writeValueAsString(SendNotificationEvent.builder()
                                                            .title("Подтверди заявку на вступление в группу 'Kayak'!")
                                                            .userId(approver.getId())
                                                            .body("Пользователь Sapporo хочет вступить в группу 'Kayak'!")
                                                            .additionalData(Map.of(
                                                                    NotificationAdditionalDataKey.ANSWERER.name(), user.getId().toString(),
                                                                    NotificationAdditionalDataKey.CLUB_ID.name(), club.getId().toString()
                                                            ))
                                                            .build());
                                                } catch (Exception e) {
                                                    return "";
                                                }
                                            })
                                            .toList()
                            )
                    );
        }

        var confirmations = testGetUserWaitingConfirmations(user);
        testGetApproverWaitingConfirmations(approvers, user);
        testUpdateConfirmationStatus(confirmations, user, club);
    }

    private List<ConfirmationEntity> testGetUserWaitingConfirmations(UserEntity user) {
        var userWaitingConfirmations = confirmationService.getUserWaitingConfirmations(
                user.getId(),
                new PaginationInput(0, 20)
        );
        assertNotNull(userWaitingConfirmations);
        assertEquals(5, userWaitingConfirmations.size());
        assertEquals(
                userWaitingConfirmations.stream()
                        .map(ConfirmationEntity::getStatus)
                        .collect(Collectors.toSet()),
                Set.of(ConfirmationStatus.WAITING)
        );

        return userWaitingConfirmations;
    }

    private void testGetApproverWaitingConfirmations(List<UserEntity> approvers, UserEntity user) {
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
        assertEquals(5, singleApproverConfirmationCount);
    }

    @SneakyThrows
    private void testUpdateConfirmationStatus(List<ConfirmationEntity> confirmations, UserEntity user, ClubEntity club) {
        confirmations.forEach(confirmation ->
                confirmationService.updateConfirmationStatus(confirmation.getId(), ConfirmationStatus.APPLIED));
        Thread.sleep(1000);
        var member = memberRepository.findById(MemberId.builder()
                .id(user.getId())
                .clubId(club.getId())
                .build());
        assertTrue(member.isPresent());
    }
}
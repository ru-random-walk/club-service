package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.ConfirmationRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.service.reviewer.AnswerReviewer;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AllArgsConstructor(onConstructor = @__(@Autowired))
class AnswerReviewerTest extends AbstractContainerTest {
    private static final int REVIEW_TIMEOUT_IN_SECONDS = 1;

    private final AnswerReviewer answerReviewer;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final ApprovementRepository approvementRepository;
    private final MemberRepository memberRepository;
    private final ConfirmationRepository confirmationRepository;

    @Test
    void reviewCorrectAnswerToFormTest() throws InterruptedException {
        var userId = UUID.randomUUID();
        var reviewAnswerData = userAnswerToApprovement(
                userId,
                StubDataUtil.formApprovementData(),
                StubDataUtil.formCorrectAnswerData()
        );
        answerReviewer.scheduleReview(reviewAnswerData);
        TimeUnit.SECONDS.sleep(REVIEW_TIMEOUT_IN_SECONDS);
        var actualAnswer = answerRepository.findById(reviewAnswerData.answerId()).orElseThrow();
        assertEquals(AnswerStatus.PASSED, actualAnswer.getStatus());
        var member = memberRepository.findById(MemberEntity.MemberId.builder()
                .id(userId)
                .clubId(reviewAnswerData.clubId())
                .build());
        assertTrue(member.isPresent(), "Member must be present!");
    }

    @Test
    void reviewFailedAnswerToFormTest() throws InterruptedException {
        var userId = UUID.randomUUID();
        var reviewAnswerData = userAnswerToApprovement(
                userId,
                StubDataUtil.formApprovementData(),
                StubDataUtil.formWrongAnswerData()
        );
        answerReviewer.scheduleReview(reviewAnswerData);
        TimeUnit.SECONDS.sleep(REVIEW_TIMEOUT_IN_SECONDS);
        var actualAnswer = answerRepository.findById(reviewAnswerData.answerId()).orElseThrow();
        assertEquals(AnswerStatus.FAILED, actualAnswer.getStatus());
        var member = memberRepository.findById(MemberEntity.MemberId.builder()
                .id(userId)
                .clubId(reviewAnswerData.clubId())
                .build());
        assertTrue(member.isEmpty(), "Member must not be present!");
    }

    @Test
    void reviewMembersConfirmTest() throws InterruptedException {
        var userId = UUID.randomUUID();
        var reviewAnswerData = userAnswerToApprovement(
                userId,
                StubDataUtil.membersConfirmApprovementData(),
                StubDataUtil.membersConfirmAnswerData()
        );
        answerReviewer.scheduleReview(reviewAnswerData);
        TimeUnit.SECONDS.sleep(REVIEW_TIMEOUT_IN_SECONDS);
        var actualAnswer = answerRepository.findById(reviewAnswerData.answerId()).orElseThrow();
        assertEquals(AnswerStatus.IN_REVIEW, actualAnswer.getStatus());
        var approversCount = confirmationRepository.countByUserId(userId);
        assertEquals(2, approversCount);
    }

    private ForReviewData userAnswerToApprovement(UUID userId, ApprovementData approvementData, AnswerData answerData) {
        var club = clubRepository.save(ClubEntity.builder()
                .name("Da")
                .build());
        var users = userRepository.saveAllAndFlush(
                IntStream.range(0, 10)
                        .mapToObj(i -> UserEntity.builder()
                                .fullName(String.valueOf(i))
                                .id(UUID.randomUUID())
                                .build())
                        .toList()
        );
        memberRepository.saveAllAndFlush(
                users.stream()
                        .map(user -> MemberEntity.builder()
                                .role(MemberRole.INSPECTOR)
                                .id(user.getId())
                                .clubId(club.getId())
                                .build())
                        .toList()
        );
        var approvement = approvementRepository.save(ApprovementEntity.builder()
                .clubId(club.getId())
                .data(approvementData)
                .type(ApprovementType.FORM)
                .build());
        var user = userRepository.save(UserEntity.builder()
                .id(userId)
                .fullName("ma")
                .build());
        var answer = answerRepository.save(AnswerEntity.builder()
                .status(AnswerStatus.CREATED)
                .userId(user.getId())
                .data(answerData)
                .approvement(approvement)
                .build());
        return new ForReviewData(
                answer.getId(),
                answer.getData(),
                approvement.getData(),
                user.getId(),
                club.getId()
        );
    }
}
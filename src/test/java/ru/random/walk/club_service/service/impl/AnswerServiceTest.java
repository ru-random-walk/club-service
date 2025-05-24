package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.service.AnswerService;
import ru.random.walk.club_service.service.ApprovementService;
import ru.random.walk.club_service.service.ClubService;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AllArgsConstructor(onConstructor = @__(@Autowired))
class AnswerServiceTest extends AbstractContainerTest {
    private final ClubService clubService;
    private final ApprovementService approvementService;
    private final AnswerService answerService;

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @Test
    void testSetStatusToSentSync() {
        var admin = userRepository.saveAndFlush(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Papa")
                .build());
        var club = clubService.createClub("Buba", admin.getId());
        var approvement = approvementService.addForClub(StubDataUtil.formApprovementData(), club.getId());

        var user = userRepository.saveAndFlush(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Lula")
                .build());
        var answer = answerService.createForm(approvement.getId(), StubDataUtil.formCorrectAnswerData(), user.getId());
        assertEquals(AnswerStatus.CREATED, answer.getStatus());
        var actualAnswer = answerService.setStatusToSentSync(answer.getId(), user.getId());

        assertEquals(AnswerStatus.PASSED, actualAnswer.getStatus());
        assertTrue(memberRepository.findById(MemberEntity.MemberId.builder()
                        .id(user.getId())
                        .clubId(club.getId())
                        .build())
                .isPresent());
    }
}
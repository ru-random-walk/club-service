package ru.random.walk.club_service.repository;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.AbstractPostgresContainerTest;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
class AnswerRepositoryTest extends AbstractPostgresContainerTest {
    private final AnswerRepository answerRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ApprovementRepository approvementRepository;

    @Test
    void testUpdateStatus() {
        var answerId = saveAnswer();
        updateAnswerStatusToSent(answerId);
        checkStatusWasChanged(answerId);
    }

    @Transactional
    private void checkStatusWasChanged(UUID answerId) {
        var answer = answerRepository.findById(answerId).orElseThrow();
        assertEquals(AnswerStatus.SENT, answer.getStatus());
    }

    @Transactional
    private void updateAnswerStatusToSent(UUID answerId) {
        answerRepository.updateStatus(answerId, AnswerStatus.SENT);
    }

    @Transactional
    private UUID saveAnswer() {
        var club = clubRepository.save(ClubEntity.builder()
                .name("Da")
                .build());
        var approvement = approvementRepository.save(ApprovementEntity.builder()
                .clubId(club.getId())
                .data(StubDataUtil.formApprovementData())
                .type(ApprovementType.FORM)
                .build());
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("ma")
                .build());
        var answer = answerRepository.save(AnswerEntity.builder()
                .status(AnswerStatus.CREATED)
                .userId(user.getId())
                .data(StubDataUtil.formAnswerData())
                .approvement(approvement)
                .build());
        return answer.getId();
    }
}
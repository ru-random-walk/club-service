package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.random.walk.club_service.AbstractPostgresContainerTest;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity.MemberId;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.service.MemberService;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
class MemberServiceTest extends AbstractPostgresContainerTest {
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final ApprovementRepository approvementRepository;
    private final AnswerRepository answerRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Test
    void addInClubIfAllTestPassedTest() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Guru Mura")
                .build());
        var club = clubRepository.save(ClubEntity.builder()
                .name("Suk")
                .build());
        var approvement = approvementRepository.save(ApprovementEntity.builder()
                .clubId(club.getId())
                .type(ApprovementType.FORM)
                .data(StubDataUtil.formApprovementData())
                .build());
        answerRepository.save(AnswerEntity.builder()
                .approvement(approvement)
                .userId(user.getId())
                .data(StubDataUtil.formCorrectAnswerData())
                .status(AnswerStatus.PASSED)
                .build());
        memberService.addInClubIfAllTestPassed(user.getId(), club.getId());

        var member = memberRepository.findById(MemberId.builder()
                .id(user.getId())
                .clubId(club.getId())
                .build());
        assertTrue(member.isPresent(), "Member must be present!");
    }
}
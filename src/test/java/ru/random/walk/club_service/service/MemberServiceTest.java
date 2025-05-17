package ru.random.walk.club_service.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity.MemberId;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AllArgsConstructor(onConstructor_ = @__(@Autowired))
class MemberServiceTest extends AbstractContainerTest {
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final ApprovementRepository approvementRepository;

    private final MemberService memberService;
    private final ClubService clubService;

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

    @Test
    void testSingleAdminRemoveCase() {
        var admin = userRepository.save(UserEntity.builder()
                .fullName("Nerd")
                .id(UUID.randomUUID())
                .build());
        var club = clubService.createClub("Wooden", admin.getId());
        assertThrows(
                ValidationException.class,
                () -> memberService.removeFromClub(admin.getId(), club.getId())
        );
    }
}
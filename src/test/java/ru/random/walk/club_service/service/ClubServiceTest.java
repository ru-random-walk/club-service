package ru.random.walk.club_service.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.service.job.OutboxSendingJob;
import ru.random.walk.club_service.util.StubDataUtil;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ClubServiceTest extends AbstractContainerTest {
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ApprovementRepository approvementRepository;

    private final ClubService clubService;
    private final ApprovementService approvementService;
    private final UserService userService;
    private final OutboxSendingJob outboxSendingJob;

    @MockitoSpyBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void getClubToApproversNumber() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Boy with dead dad :(")
                .build());
        var user2 = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Boy with dead dad :(")
                .build());
        var user3 = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Boy with dead dad :(")
                .build());
        var user4 = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Boy with dead dad :(")
                .build());
        var club = clubRepository.save(ClubEntity.builder()
                .name("Chainsaw Mans...")
                .build());
        var club2 = clubRepository.save(ClubEntity.builder()
                .name("Chainsaw Mans...")
                .build());
        memberRepository.saveAllAndFlush(List.of(
                MemberEntity.builder()
                        .clubId(club.getId())
                        .role(MemberRole.USER)
                        .id(user.getId())
                        .build(),
                MemberEntity.builder()
                        .clubId(club.getId())
                        .role(MemberRole.ADMIN)
                        .id(user2.getId())
                        .build(),
                MemberEntity.builder()
                        .clubId(club.getId())
                        .role(MemberRole.INSPECTOR)
                        .id(user3.getId())
                        .build(),
                MemberEntity.builder()
                        .clubId(club.getId())
                        .role(MemberRole.INSPECTOR)
                        .id(user4.getId())
                        .build(),

                MemberEntity.builder()
                        .clubId(club2.getId())
                        .role(MemberRole.USER)
                        .id(user.getId())
                        .build(),
                MemberEntity.builder()
                        .clubId(club2.getId())
                        .role(MemberRole.USER)
                        .id(user2.getId())
                        .build()
        ));

        var clubIdToApproversCount = clubService.getClubToApproversNumber(
                Stream.of(club.getId(), club2.getId())
                        .map(id -> ClubEntity.builder()
                                .id(id)
                                .build())
                        .toList()
        );
        assertEquals(3, clubIdToApproversCount.getFirst());
        assertEquals(0, clubIdToApproversCount.get(1));
    }

    @Test
    void testRemoveClubWithAllItsData() {
        var userId = UUID.randomUUID();
        userService.add(UserEntity.builder()
                .fullName("John")
                .id(userId)
                .build());
        var club = clubService.createClub("Majors", (UserPrincipal) userId::toString);
        var approvement = approvementService.addForClub(StubDataUtil.formApprovementData(), club.getId());

        clubRepository.findById(club.getId()).orElseThrow();
        approvementRepository.findById(approvement.getId()).orElseThrow();
        memberRepository.findByIdAndClubId(userId, club.getId()).orElseThrow();

        clubService.removeClubWithAllItsData(club.getId());
        outboxSendingJob.execute(null);

        verify(kafkaTemplate, times(1)).send(any(), any());
        assertTrue(clubRepository.findById(club.getId()).isEmpty());
    }
}
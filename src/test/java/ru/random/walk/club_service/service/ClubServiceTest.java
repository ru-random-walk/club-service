package ru.random.walk.club_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.AbstractContainerTest;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.service.job.OutboxSendingJob;
import ru.random.walk.club_service.util.StubDataUtil;
import ru.random.walk.dto.UserExcludeEvent;
import ru.random.walk.topic.EventTopic;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static ru.random.walk.club_service.mockito.JsonArgMatcher.jsonEq;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ClubServiceTest extends AbstractContainerTest {
    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ApprovementRepository approvementRepository;

    private final ClubService clubService;
    private final ApprovementService approvementService;
    private final UserService userService;
    private final AnswerService answerService;

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
    void testRemoveClubWithAllItsData() throws JsonProcessingException {
        var answererId = UUID.randomUUID();
        userService.add(UserEntity.builder()
                .fullName("John")
                .id(answererId)
                .build());
        var userId = UUID.randomUUID();
        userService.add(UserEntity.builder()
                .fullName("John")
                .id(userId)
                .build());
        var club = clubService.createClub("Majors", userId);
        var approvement = approvementService.addForClub(StubDataUtil.formApprovementData(), club.getId());
        answerService.createForm(approvement.getId(), (FormAnswerData) StubDataUtil.formAnswerData(), answererId);

        clubRepository.findById(club.getId()).orElseThrow();
        approvementRepository.findById(approvement.getId()).orElseThrow();
        memberRepository.findByIdAndClubId(userId, club.getId()).orElseThrow();

        clubService.removeClubWithAllItsData(club.getId());
        outboxSendingJob.execute(null);

        verify(kafkaTemplate).send(
                eq(EventTopic.USER_EXCLUDE),
                jsonEq(
                        objectMapper.writeValueAsString(UserExcludeEvent.builder()
                                .clubId(club.getId())
                                .userId(userId)
                                .build())
                )
        );
        assertTrue(clubRepository.findById(club.getId()).isEmpty());
    }

    @Test
    @Transactional
    void testCreateClubWithMembersConfirmApprovement() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("")
                .build());
        var club = clubService.createClubWithApprovement("", "", StubDataUtil.membersConfirmApprovementData(), user.getId());

        var actualClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(1, actualClub.getMembers().size());
        assertEquals(1, actualClub.getApprovements().size());
        assertEquals(ApprovementType.MEMBERS_CONFIRM, actualClub.getApprovements().getFirst().getType());
        assertEquals(user.getId(), actualClub.getMembers().getFirst().getId());
        assertEquals(MemberRole.ADMIN, actualClub.getMembers().getFirst().getRole());
    }

    @Test
    @Transactional
    void testCreateClubWithFormApprovement() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("")
                .build());
        var club = clubService.createClubWithApprovement("", "", StubDataUtil.formApprovementData(), user.getId());

        var actualClub = clubRepository.findById(club.getId()).orElseThrow();
        assertEquals(1, actualClub.getMembers().size());
        assertEquals(1, actualClub.getApprovements().size());
        assertEquals(ApprovementType.FORM, actualClub.getApprovements().getFirst().getType());
        assertEquals(user.getId(), actualClub.getMembers().getFirst().getId());
        assertEquals(MemberRole.ADMIN, actualClub.getMembers().getFirst().getRole());
    }

    @Test
    void testRemoveClubPhoto() throws IOException {
        var admin = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("")
                .build());
        var club = clubService.createClub("", admin.getId());

        clubService.uploadPhotoForClub(club.getId(), null);

        var actualClub = clubRepository.findById(club.getId())
                .orElseThrow();
        assertEquals(1, actualClub.getPhotoVersion());

        clubService.removeClubPhoto(club.getId());
        actualClub = clubRepository.findById(club.getId())
                .orElseThrow();
        assertNull(actualClub.getPhotoVersion());
    }

    @Test
    void testSearchClubs() {
        var favoriteClub = clubRepository.save(ClubEntity.builder()
                .name("Смешарики")
                .build());
        clubRepository.save(ClubEntity.builder()
                .name("Автомобили Porsche")
                .build());
        var clubs = clubService.searchClubs("Смешарики", PaginationInput.newBuilder()
                .page(0)
                .size(30)
                .build());
        assertEquals(favoriteClub.getId(), clubs.getFirst().getId());
    }
}
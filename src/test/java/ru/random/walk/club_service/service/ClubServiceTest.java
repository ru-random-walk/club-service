package ru.random.walk.club_service.service;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.random.walk.club_service.AbstractPostgresContainerTest;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
class ClubServiceTest extends AbstractPostgresContainerTest {
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    private final ClubService clubService;

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
}
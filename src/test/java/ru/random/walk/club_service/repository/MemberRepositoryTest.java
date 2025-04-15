package ru.random.walk.club_service.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.AbstractPostgresContainerTest;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.entity.projection.ClubIdToMemberRoleToCountProjection;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
@SpringBootTest
@AllArgsConstructor(onConstructor = @__(@Autowired))
class MemberRepositoryTest extends AbstractPostgresContainerTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Transactional
    void save() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Lonely dad :(")
                .build());
        var club = clubRepository.save(ClubEntity.builder()
                .name("Lonely dads")
                .build());
        memberRepository.save(MemberEntity.builder()
                .id(user.getId())
                .clubId(club.getId())
                .role(MemberRole.ADMIN)
                .build());
        entityManager.flush();
        club = clubRepository.findById(club.getId()).orElseThrow();
        var membersPage = memberRepository.findAllByClubId(club.getId(), Pageable.ofSize(20));
        club.setMembers(membersPage.getContent());
        assertNotNull(club.getId());
        assertNotNull(club.getName());
        assertNotNull(club.getMembers());
        assertNotNull(club.getApprovements());
    }

    @Test
    void countByIdAndRole() {
        memberRepository.countByIdAndRole(UUID.randomUUID(), MemberRole.ADMIN);
    }

    @Test
    void saveWithNewInspectorRole() {
        var user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .fullName("Boy with dead dad :(")
                .build());
        var club = clubRepository.save(ClubEntity.builder()
                .name("Chainsaw Mans...")
                .build());
        memberRepository.save(MemberEntity.builder()
                .role(MemberRole.INSPECTOR)
                .id(user.getId())
                .clubId(club.getId())
                .build());
    }

    @Test
    void testFindAllClubIdToRoleToCountByClubIds() {
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
        Map<Pair<UUID, MemberRole>, Integer> clubMembersRoleToCount =
                memberRepository.findAllClubIdToRoleToCountByClubIds(
                                List.of(club.getId(), club2.getId())
                        ).stream()
                        .collect(Collectors.toMap(
                                row -> Pair.of(row.clubId(), row.memberRole()),
                                ClubIdToMemberRoleToCountProjection::count
                        ));
        assertEquals(1, clubMembersRoleToCount.get(Pair.of(club.getId(), MemberRole.USER)));
        assertEquals(1, clubMembersRoleToCount.get(Pair.of(club.getId(), MemberRole.ADMIN)));
        assertEquals(2, clubMembersRoleToCount.get(Pair.of(club.getId(), MemberRole.INSPECTOR)));
        assertEquals(2, clubMembersRoleToCount.get(Pair.of(club2.getId(), MemberRole.USER)));
        assertNull(clubMembersRoleToCount.get(Pair.of(club2.getId(), MemberRole.ADMIN)));
        assertNull(clubMembersRoleToCount.get(Pair.of(club2.getId(), MemberRole.INSPECTOR)));
    }
}
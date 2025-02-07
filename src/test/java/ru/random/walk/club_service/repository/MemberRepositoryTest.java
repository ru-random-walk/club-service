package ru.random.walk.club_service.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.AbstractPostgresContainerTest;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
class MemberRepositoryTest extends AbstractPostgresContainerTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ClubRepository clubRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Transactional
    void save() {
        var club = clubRepository.save(ClubEntity.builder()
                .name("Lonely dads")
                .build());
        memberRepository.save(MemberEntity.builder()
                .id(UUID.randomUUID())
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

}
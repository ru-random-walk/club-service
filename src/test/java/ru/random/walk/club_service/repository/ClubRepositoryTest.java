package ru.random.walk.club_service.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.random.walk.club_service.AbstractPostgresContainerTest;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.util.StubDataUtil;

@SpringBootTest
class ClubRepositoryTest extends AbstractPostgresContainerTest {
    @Autowired
    private ClubRepository clubRepository;

    @Test
    void save() {
        var club = clubRepository.save(ClubEntity.builder()
                .name("Wild memory")
                .build());
        club.getApprovements().add(ApprovementEntity.builder()
                .data(StubDataUtil.membersConfirmApprovementData())
                .type(ApprovementType.MEMBERS_CONFIRM)
                .build());
    }
}
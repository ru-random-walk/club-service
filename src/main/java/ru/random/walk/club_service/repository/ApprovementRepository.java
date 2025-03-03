package ru.random.walk.club_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.random.walk.club_service.model.entity.ApprovementEntity;

import java.util.UUID;

public interface ApprovementRepository extends JpaRepository<ApprovementEntity, UUID> {
    Integer countByClubId(UUID clubId);
}

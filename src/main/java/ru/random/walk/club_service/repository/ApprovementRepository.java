package ru.random.walk.club_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.random.walk.club_service.model.entity.ApprovementEntity;

import java.util.UUID;

@Repository
public interface ApprovementRepository extends CrudRepository<ApprovementEntity, UUID> {
    Integer countByClubId(UUID clubId);
}

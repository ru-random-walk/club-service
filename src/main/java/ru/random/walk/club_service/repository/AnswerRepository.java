package ru.random.walk.club_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;

import java.util.UUID;

public interface AnswerRepository extends JpaRepository<AnswerEntity, UUID> {
    Integer countByApprovementAndUserId(ApprovementEntity approvement, UUID userId);
}

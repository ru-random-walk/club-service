package ru.random.walk.club_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;

import java.util.UUID;

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, UUID> {
    Long countByUserId(UUID userId);
}

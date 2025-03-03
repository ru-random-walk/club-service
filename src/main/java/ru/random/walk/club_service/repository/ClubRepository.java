package ru.random.walk.club_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.random.walk.club_service.model.entity.ClubEntity;

import java.util.UUID;

public interface ClubRepository extends JpaRepository<ClubEntity, UUID> {
}

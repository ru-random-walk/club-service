package ru.random.walk.club_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.random.walk.club_service.model.entity.ClubEntity;

import java.util.UUID;

@Repository
public interface ClubRepository extends CrudRepository<ClubEntity, UUID> {
}

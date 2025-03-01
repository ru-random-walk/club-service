package ru.random.walk.club_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends CrudRepository<AnswerEntity, UUID> {
    Integer countByApprovementAndUserId(ApprovementEntity approvement, UUID userId);

    List<AnswerEntity> findAllByUserId(UUID userId, Pageable pageable);
}

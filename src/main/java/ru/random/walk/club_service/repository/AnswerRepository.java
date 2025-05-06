package ru.random.walk.club_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<AnswerEntity, UUID> {
    Integer countByApprovementAndUserId(ApprovementEntity approvement, UUID userId);

    List<AnswerEntity> findAllByUserId(UUID userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(
            value = """
                    update club.answer
                    set status = cast(:#{#status.toString()} AS club.answer_status)
                    where id = :id
                    """,
            nativeQuery = true
    )
    void updateStatus(@Param("id") UUID id, @Param("status") AnswerStatus status);

    @Query(
            value = """
                    select distinct answer.approvement_id
                    from club.answer answer
                    join club.approvement approvement
                    on answer.approvement_id = approvement.id
                    where answer.user_id = :userId and approvement.club_id = :clubId
                    and answer.status = 'PASSED'
                    """,
            nativeQuery = true
    )
    Set<UUID> findAllPassedIdsByUserIdAndClubId(@Param("userId") UUID userId, @Param("clubId") UUID clubId);

    @Query("""
            delete from AnswerEntity a
            where a.approvement.id in (
                select a.id
                from ApprovementEntity a
                where a.clubId = :clubId
            )
            """)
    @Modifying
    void deleteAllByClubId(UUID clubId);
}

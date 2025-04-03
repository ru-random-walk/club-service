package ru.random.walk.club_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;
import ru.random.walk.club_service.model.entity.type.ConfirmationStatus;

import java.util.List;
import java.util.UUID;

public interface ConfirmationRepository extends JpaRepository<ConfirmationEntity, UUID> {
    Long countByUserId(UUID userId);

    List<ConfirmationEntity> findAllByUserId(UUID userId, Pageable pageable);

    List<ConfirmationEntity> findAllByApproverId(UUID approverId, Pageable pageable);

    @Query("""
            select count(*)
            from ConfirmationEntity c
            where c.answer.id = :answerId
            and cast(c.status as string) = :#{#status.toString()}
            """)
    Integer countAllByAnswerIdAndStatus(@Param("answerId") UUID answerId, @Param("status") ConfirmationStatus status);

    @Query(
            value = """
                    update club.confirmation
                    set status = cast(:#{#status.toString()} AS club.confirmation_status)
                    where id = :id
                    returning *
                    """,
            nativeQuery = true
    )
    ConfirmationEntity updateStatusById(@Param("id") UUID id, @Param("status") ConfirmationStatus status);
}

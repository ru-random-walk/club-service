package ru.random.walk.club_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, MemberEntity.MemberId> {
    Page<MemberEntity> findAllByClubId(UUID clubId, Pageable pageable);

    Optional<MemberEntity> findByIdAndClubId(UUID login, UUID clubId);

    @Query(
            value = """
                    select count(m)
                    from club.member m
                    where m.id = :id and m.role = cast(:#{#role.toString()} AS club.member_role)""",
            nativeQuery = true
    )
    Integer countByIdAndRole(UUID id, MemberRole role);
}

package ru.random.walk.club_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.projection.ClubIdToMemberRoleToCountProjection;
import ru.random.walk.club_service.model.entity.type.MemberRole;

import java.util.List;
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

    Page<MemberEntity> findAllById(UUID userId, Pageable pageable);

    @Query(
            value = """
                    select id
                    from club.member
                    where club_id = :clubId
                    order by random()
                    limit :count
                    """,
            nativeQuery = true
    )
    List<UUID> findRandomApproversByClubId(@Param("clubId") UUID clubId, @Param("count") Integer count);

    @Query("""
            select m.clubId as clubId, m.role as role, count(*) as count
            from MemberEntity m
            where m.clubId in (:clubIds)
            group by m.clubId, m.role
            """)
    List<ClubIdToMemberRoleToCountProjection> findAllClubIdToRoleToCountByClubIds(List<UUID> clubIds);

    List<MemberEntity> deleteAllByClubId(UUID id);

    @Query("""
            select m
            from MemberEntity m
            where m.clubId in (:clubIds) and m.id = :userId
            """)
    List<MemberEntity> findAllByIdAndClubIds(List<UUID> clubIds, UUID userId);
}

package ru.random.walk.club_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.projection.ClubWithUserRoleProjection;

import java.util.List;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<ClubEntity, UUID> {
    @Query(
            value = """
                    select id, role
                    from (
                        select
                            id,
                            case
                    			when role = 'USER' then 'MEMBER'
                    			else role
                    		end as role,
                    		row_number() over (partition by id order by priority) as priority_place
                        from (
                            select
                                m.club_id as id,
                                cast(m.role as text) as role,
                                1 as priority
                            from club.member m
                            where m.id = :user_id

                            union all

                            select
                                distinct approvement.club_id as id,
                                'PENDING_APPROVAL' as role,
                                2 as priority
                            from club.approvement approvement
                            join club.answer
                            on answer.approvement_id = approvement.id
                            where answer.user_id = :user_id
                        )
                    )
                    where priority_place = 1
                   """,
            nativeQuery = true
    )
    List<ClubWithUserRoleProjection> findAllClubsWithRoleByUser(@Param("user_id") UUID userId);
}

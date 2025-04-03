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
                    		c.id,
                    		case
                    			when coalesce(m.role, 'PENDING_APPROVAL') = 'USER' then 'MEMBER'
                    			else coalesce(m.role, 'PENDING_APPROVAL')
                    		end as role,
                    		row_number() over (partition by c.id order by priority) as priority_place
                    	from
                    		club.club c
                    	left join (
                    		select
                    			m.club_id,
                    			cast(m.role as text),
                    			1 as priority
                    		from
                    			club.member m
                    		where
                    			m.id = :user_id
                    	    union all
                    		select
                    			distinct approvement.club_id,
                    			'PENDING_APPROVAL' as role,
                    			2 as priority
                    		from
                    			club.approvement approvement
                    		join club.answer answer on
                    			answer.approvement_id = approvement.id
                    		where
                    			answer.user_id = :user_id
                        ) as m
                        on c.id = m.club_id
                    )
                    where priority_place = 1
                    """,
            nativeQuery = true
    )
    List<ClubWithUserRoleProjection> findAllClubsWithRoleByUser(@Param("user_id") UUID userId);
}

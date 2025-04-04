package ru.random.walk.club_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.projection.ClubWithUserRoleProjection;

import java.util.List;
import java.util.UUID;

public interface ClubRepository extends JpaRepository<ClubEntity, UUID> {
    // Ищем все клубы в которых состоит участник с его ролью из этой группы соответственно
    // (ролями участника группы могут быть: USER, INSPECTOR, ADMIN)
    // ставим этой информации наивысший приоритет - 10
    // далее ищем все ответы отправленные юзером с их аппрувментом и их группой
    // (на один ответ есть только один аппрувмент и на один аппрувмент есть только одна группа)
    // роль юзера выставляется как PENDING_APPROVAL
    // а приоритет этой информации имеет меньший приоритет - 9
    // В итоге выбираем строчки айдишников групп вместе с ролью юзера с наибольшим приоритетом
    // поэтому если участник состоит в группе, то в первую очередь будет отображаться роль в клубе
    // а уже в последнюю очередь будет роль PENDING_APPROVAL из-за того что он имеет какой-то ответ для этого клуба
    // что означает что юзер только отправил или ещё не закончил аппрувмент
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
                    		row_number() over (partition by id order by priority desc) as priority_place
                        from (
                            select
                                m.club_id as id,
                                cast(m.role as text) as role,
                                10 as priority
                            from club.member m
                            where m.id = :user_id

                            union all

                            select
                                distinct approvement.club_id as id,
                                'PENDING_APPROVAL' as role,
                                9 as priority
                            from club.answer answer
                            join club.approvement
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

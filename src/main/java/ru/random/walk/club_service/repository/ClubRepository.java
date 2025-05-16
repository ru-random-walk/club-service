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
                     WITH ClubToRoleWithPriority AS (
                          SELECT
                              m.club_id AS id,
                              CAST(m.role AS TEXT) AS role,
                              10 AS priority
                          FROM
                              club.member m
                          WHERE
                              m.id = :user_id
                      ),
                      PendingApprovalFromAnswerWithPriority AS (
                          SELECT
                              DISTINCT approvement.club_id AS id,
                              'PENDING_APPROVAL' AS role,
                              9 AS priority
                          FROM
                              club.answer answer
                          JOIN
                              club.approvement ON answer.approvement_id = approvement.id
                          WHERE
                              answer.user_id = :user_id
                      ),
                      ClubRoleWithPriorityPlace AS (
                          SELECT
                              id,
                              CASE
                                  WHEN role = 'USER' THEN 'MEMBER'
                                  ELSE role
                              END AS role,
                              ROW_NUMBER() OVER (PARTITION BY id ORDER BY priority DESC) AS priority_place
                          FROM (
                              SELECT * FROM ClubToRoleWithPriority
                              UNION ALL
                              SELECT * FROM PendingApprovalFromAnswerWithPriority
                          )
                      )
                      SELECT
                          id,
                          role
                      FROM
                          ClubRoleWithPriorityPlace
                      WHERE
                          priority_place = 1;
                    """,
            nativeQuery = true
    )
    List<ClubWithUserRoleProjection> findAllClubsWithRoleByUser(@Param("user_id") UUID userId);

    @Query(
            value = """
                    select *
                    from club.club
                    order by similarity(concat(name,' ',description), :query)
                    offset :offset
                    limit :size
                    """,
            nativeQuery = true
    )
    List<ClubEntity> searchClubsByNameWithDescription(String query, int offset, int size);
}

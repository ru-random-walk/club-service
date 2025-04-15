package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.dto.ClubWithUserRole;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void add(UserEntity userEntity);

    Iterable<ClubEntity> getClubs(UUID userId, PaginationInput pagination);

    List<AnswerEntity> getAnswers(UUID userId, PaginationInput pagination);

    List<ClubWithUserRole> getClubsWithRole(UUID userId);
}

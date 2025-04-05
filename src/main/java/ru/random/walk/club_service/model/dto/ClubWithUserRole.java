package ru.random.walk.club_service.model.dto;

import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.type.UserGroupRole;

public record ClubWithUserRole(
        ClubEntity club,
        UserGroupRole userRole
) {
}

package ru.random.walk.club_service.model.dto;

import org.jetbrains.annotations.Nullable;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;

public record ClubWithMemberRole(
        ClubEntity club,
        @Nullable MemberRole memberRole
) {
}

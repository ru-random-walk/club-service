package ru.random.walk.club_service.model.entity.projection;

import org.springframework.beans.factory.annotation.Value;
import ru.random.walk.club_service.model.entity.type.UserGroupRole;

import java.util.UUID;

public interface ClubWithUserRoleProjection {
    @Value("#{target.id}")
    UUID clubId();

    @Value("#{target.role}")
    UserGroupRole role();
}

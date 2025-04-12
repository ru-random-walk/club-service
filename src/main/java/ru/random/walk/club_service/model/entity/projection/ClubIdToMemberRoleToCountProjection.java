package ru.random.walk.club_service.model.entity.projection;

import org.springframework.beans.factory.annotation.Value;
import ru.random.walk.club_service.model.entity.type.MemberRole;

import java.util.UUID;

public interface ClubIdToMemberRoleToCountProjection {
    @Value("#{target.clubId}")
    UUID clubId();

    @Value("#{target.role}")
    MemberRole memberRole();

    @Value("#{target.count}")
    Integer count();
}

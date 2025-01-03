package ru.random.walk.club_service.mapper;

import org.mapstruct.Mapper;
import ru.random.walk.club_service.model.graphql.types.MemberRole;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    ru.random.walk.club_service.model.entity.type.MemberRole toDomainMemberRole(MemberRole role);
}

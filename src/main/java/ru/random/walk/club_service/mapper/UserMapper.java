package ru.random.walk.club_service.mapper;

import org.mapstruct.Mapper;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.dto.RegisteredUserInfoEvent;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(RegisteredUserInfoEvent event);
}

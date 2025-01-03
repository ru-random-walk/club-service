package ru.random.walk.club_service.mapper;

import org.mapstruct.Mapper;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.graphql.types.FormInput;
import ru.random.walk.club_service.model.graphql.types.MembersConfirmInput;

@Mapper(componentModel = "spring")
public interface ApprovementMapper {
    MembersConfirmApprovementData toMembersConfirmApprovementData(MembersConfirmInput input);

    FormApprovementData toFormApprovementData(FormInput input);
}

package ru.random.walk.club_service.mapper;

import org.mapstruct.Mapper;
import ru.random.walk.club_service.model.domain.test.FormTestData;
import ru.random.walk.club_service.model.domain.test.MembersConfirmTestData;
import ru.random.walk.club_service.model.graphql.types.FormInput;
import ru.random.walk.club_service.model.graphql.types.MembersConfirmInput;

@Mapper(componentModel = "spring")
public interface TestMapper {
    MembersConfirmTestData toMembersConfirmTestData(MembersConfirmInput input);

    FormTestData toFormTestData(FormInput input);
}

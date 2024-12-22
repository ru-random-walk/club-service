package ru.random.walk.club_service.mapper;

import org.mapstruct.Mapper;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.graphql.types.FormAnswerInput;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    FormAnswerData toFormAnswerData(FormAnswerInput formAnswer);
}

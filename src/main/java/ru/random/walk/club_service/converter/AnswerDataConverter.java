package ru.random.walk.club_service.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import ru.random.walk.club_service.model.domain.answer.AnswerData;

@Converter
@Slf4j
public class AnswerDataConverter implements AttributeConverter<AnswerData, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AnswerData answerData) {
        try {
            var res = objectMapper.writeValueAsString(answerData);
            log.debug("Answer data: {}", res);
            return res;
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert answerData: [{}] to json", answerData, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AnswerData convertToEntityAttribute(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, AnswerData.class);
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert json: [{}] to entity data", jsonStr, e);
            throw new RuntimeException(e);
        }
    }
}

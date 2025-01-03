package ru.random.walk.club_service.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;

@Converter
@Slf4j
public class ApprovementDataConverter implements AttributeConverter<ApprovementData, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ApprovementData approvementData) {
        try {
            var res = objectMapper.writeValueAsString(approvementData);
            log.debug("Approvement data: {}", res);
            return res;
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert approvementData: [{}] to json", approvementData, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApprovementData convertToEntityAttribute(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, ApprovementData.class);
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert json: [{}] to entity data", jsonStr, e);
            throw new RuntimeException(e);
        }
    }
}

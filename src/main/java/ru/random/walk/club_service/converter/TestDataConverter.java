package ru.random.walk.club_service.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import ru.random.walk.club_service.model.domain.test.TestData;

@Converter
@Slf4j
public class TestDataConverter implements AttributeConverter<TestData, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(TestData testData) {
        try {
            var res = objectMapper.writeValueAsString(testData);
            log.debug("Test data: {}", res);
            return res;
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert testData: [{}] to json", testData, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TestData convertToEntityAttribute(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, TestData.class);
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert json: [{}] to entity data", jsonStr, e);
            throw new RuntimeException(e);
        }
    }
}

package ru.random.walk.club_service.converter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.List;

@Slf4j
public class AnswerDataConverterTest {
    private static final AnswerDataConverter converter = new AnswerDataConverter();

    @Test
    public void testSerialization() {
        List<AnswerData> answerDataList = List.of(
                StubDataUtil.formAnswerData(),
                StubDataUtil.membersConfirmAnswerData()
        );
        answerDataList.forEach(answerData -> {
            var jsonStr = converter.convertToDatabaseColumn(answerData);
            log.info("Successfully converted answer data with output string [{}]", jsonStr);
        });
    }

    @Test
    public void testDeserialization() {
        record JsonAndExpectedClass(String json, Class<? extends AnswerData> expectedClass) {
            public static JsonAndExpectedClass of(String json, Class<? extends AnswerData> expectedClass) {
                return new JsonAndExpectedClass(json, expectedClass);
            }
        }
        List<JsonAndExpectedClass> answerTestDataList = List.of(
                JsonAndExpectedClass.of("""
                                {
                                    "type":"form_answer_data",
                                    "questionAnswers":[
                                        {
                                            "optionNumbers":[2]
                                        }
                                    ]
                                }""",
                        FormAnswerData.class
                ),
                JsonAndExpectedClass.of("""
                                {
                                    "type":"members_confirm_answer_data"
                                }""",
                        MembersConfirmAnswerData.class
                )
        );
        answerTestDataList.forEach(answerTestData -> {
            AnswerData answerData = converter.convertToEntityAttribute(answerTestData.json);
            Assertions.assertEquals(answerData.getClass(), answerTestData.expectedClass);
            log.info("Successfully converted answer data with output class [{}]", answerData);
        });
    }
}

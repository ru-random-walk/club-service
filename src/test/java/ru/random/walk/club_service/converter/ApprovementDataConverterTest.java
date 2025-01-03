package ru.random.walk.club_service.converter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.List;

@Slf4j
class ApprovementDataConverterTest {
    private static final ApprovementDataConverter converter = new ApprovementDataConverter();

    @Test
    public void testSerialization() {
        List<ApprovementData> approvementDataList = List.of(
                StubDataUtil.formApprovementData(),
                StubDataUtil.membersConfirmApprovementData()
        );
        approvementDataList.forEach(approvementData -> {
            var jsonStr = converter.convertToDatabaseColumn(approvementData);
            log.info("Successfully converted approvement data with output string [{}]", jsonStr);
        });
    }

    @Test
    public void testDeserialization() {
        record JsonAndExpectedClass(String json, Class<? extends ApprovementData> expectedClass) {
            public static JsonAndExpectedClass of(String json, Class<? extends ApprovementData> expectedClass) {
                return new JsonAndExpectedClass(json, expectedClass);
            }
        }
        List<JsonAndExpectedClass> approvementTestDataList = List.of(
                JsonAndExpectedClass.of("""
                                {
                                    "type":"form_approvement_data",
                                    "questions":[
                                        {
                                            "text":"How many cards are gold in the board game 'Gnome Pests' when playing with four players?",
                                            "answerOptions":["1","2","3","10"],
                                            "answerType":"SINGLE",
                                            "correctOptionNumbers":[0]
                                        }
                                    ]
                                }""",
                        FormApprovementData.class
                ),
                JsonAndExpectedClass.of("""
                                {
                                    "type":"members_confirm_approvement_data",
                                    "requiredConfirmationNumber":2
                                }""",
                        MembersConfirmApprovementData.class
                )
        );
        approvementTestDataList.forEach(approvementTestData -> {
            ApprovementData approvementData = converter.convertToEntityAttribute(approvementTestData.json);
            Assertions.assertEquals(approvementData.getClass(), approvementTestData.expectedClass);
            log.info("Successfully converted answer data with output class [{}]", approvementTestData);
        });
    }
}
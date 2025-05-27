package ru.random.walk.club_service.util;

import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.domain.answer.QuestionAnswer;
import ru.random.walk.club_service.model.domain.approvement.AnswerType;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.domain.approvement.Question;

import java.util.Collections;
import java.util.List;

public class StubDataUtil {
    public static Question question() {
        return Question.builder()
                .text("How many cards are gold in the board game 'Gnome Pests' when playing with four players?")
                .answerOptions(
                        List.of(
                                "1", "2", "3", "10"
                        )
                )
                .answerType(AnswerType.SINGLE)
                .correctOptionNumbers(
                        Collections.singletonList(0)
                )
                .build();
    }

    public static FormApprovementData formApprovementData() {
        return new FormApprovementData(
                Collections.singletonList(question())
        );
    }

    public static MembersConfirmApprovementData membersConfirmApprovementData() {
        return new MembersConfirmApprovementData(2, 4);
    }

    public static AnswerData membersConfirmAnswerData() {
        return MembersConfirmAnswerData.INSTANCE;
    }

    public static AnswerData formWrongAnswerData() {
        return new FormAnswerData(
                List.of(
                        new QuestionAnswer(
                                List.of(2)
                        )
                )
        );
    }

    public static FormAnswerData formCorrectAnswerData() {
        return new FormAnswerData(
                List.of(
                        new QuestionAnswer(
                                List.of(0)
                        )
                )
        );
    }

    public static AnswerData formAnswerData() {
        return formCorrectAnswerData();
    }
}

package ru.random.walk.club_service.model.domain.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormAnswerData extends AnswerData {
    private List<QuestionAnswer> questionAnswers;
}

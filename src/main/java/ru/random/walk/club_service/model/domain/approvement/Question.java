package ru.random.walk.club_service.model.domain.approvement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private String text;
    private List<String> answerOptions;
    private AnswerType answerType;
    private List<Integer> correctOptionNumbers;
}

package ru.random.walk.club_service.model.domain.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private String text;
    private List<String> answerOptions;
    private AnswerType answerType;
    private List<Integer> correctOptionNumbers;
}

package ru.random.walk.club_service.model.domain.approvement;

import lombok.Builder;

import java.util.List;

@Builder
public record Question(
        String text,
        List<String> answerOptions,
        AnswerType answerType,
        List<Integer> correctOptionNumbers
) {
}

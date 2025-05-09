package ru.random.walk.club_service.model.model;

import lombok.Builder;
import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;

import java.util.UUID;

@Builder
public record ForReviewData(
        UUID answerId,
        AnswerData answerData,
        ApprovementData approvementData,
        UUID userId,
        UUID clubId
) {
}

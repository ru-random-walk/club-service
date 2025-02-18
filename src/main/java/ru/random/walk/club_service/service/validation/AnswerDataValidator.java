package ru.random.walk.club_service.service.validation;

import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.entity.type.ApprovementType;

public interface AnswerDataValidator {
    boolean matches(AnswerData answerData);

    void validate(AnswerData answerData, ApprovementData approvementData, ApprovementType approvementType);
}

package ru.random.walk.club_service.service.validation.impl;

import org.springframework.stereotype.Component;
import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.service.validation.AnswerDataValidator;

@Component
public class MembersConfirmValidator implements AnswerDataValidator {
    @Override
    public boolean matches(AnswerData answerData) {
        return answerData instanceof MembersConfirmAnswerData;
    }

    @Override
    public void validate(AnswerData answerData, ApprovementData approvementData, ApprovementType approvementType) {
        if (approvementType != ApprovementType.MEMBERS_CONFIRM) {
            throw new ValidationException("Approvement type mismatch with answer type!");
        }
    }
}

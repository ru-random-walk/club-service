package ru.random.walk.club_service.service.validation.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.service.validation.AnswerDataValidator;
import ru.random.walk.club_service.service.validation.AnswerValidatorChain;

import java.util.List;

@Service
@AllArgsConstructor
public class AnswerValidatorChainImpl implements AnswerValidatorChain {
    private final List<AnswerDataValidator> validators;

    @Override
    public void validate(AnswerData answerData, ApprovementData approvementData, ApprovementType approvementType) {
        validators.stream()
                .filter(validator -> validator.matches(answerData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found validator for answer: " + answerData))
                .validate(answerData, approvementData, approvementType);
    }
}

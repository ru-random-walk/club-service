package ru.random.walk.club_service.service.validation.impl;

import org.springframework.stereotype.Component;
import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.QuestionAnswer;
import ru.random.walk.club_service.model.domain.approvement.AnswerType;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.Question;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.service.validation.AnswerDataValidator;

import java.util.ArrayList;
import java.util.List;

@Component
public class FormValidator implements AnswerDataValidator {
    @Override
    public boolean matches(AnswerData answerData) {
        return answerData instanceof FormAnswerData;
    }

    @Override
    public void validate(AnswerData answerData, ApprovementData approvementData, ApprovementType approvementType) {
        if (approvementType != ApprovementType.FORM) {
            throw new ValidationException("Approvement type mismatch with answer type!");
        }
        validateAnswers((FormAnswerData) answerData, (FormApprovementData) approvementData);
    }

    private void validateAnswers(FormAnswerData answerData, FormApprovementData approvementData) {
        var answerArray = new ArrayList<>(answerData.getQuestionAnswers());
        var approvementArray = new ArrayList<>(approvementData.getQuestions());
        for (int answerIndex = 0; answerIndex < answerArray.size(); answerIndex++) {
            var answer = answerArray.get(answerIndex);
            var approvement = approvementArray.get(answerIndex);
            validateAnswer(answerIndex, answer, approvement);
        }
    }

    private void validateAnswer(int answerIndex, QuestionAnswer answer, Question approvement) {
        validateAnswerByApprovementType(answerIndex, answer.optionNumbers(), approvement.answerType());
        validateAnswerByApprovementAnswerOptions(answerIndex, answer.optionNumbers(), approvement.answerOptions());
    }

    private void validateAnswerByApprovementType(int answerIndex, List<Integer> answerOptions, AnswerType approvementType) {
        if (approvementType == AnswerType.SINGLE && answerOptions.size() > 1) {
            throw new ValidationException("Answer[%s] does not match the single answer type!".formatted(answerIndex));
        }
    }

    private void validateAnswerByApprovementAnswerOptions(
            int answerIndex,
            List<Integer> answerOptions,
            List<String> approvementAnswerOptions
    ) {
        for (var answerOption : answerOptions) {
            if (answerOption < 0 || answerOption >= approvementAnswerOptions.size()) {
                throw new ValidationException("Answer[%s] option[%s] out of bounds approvement answer options!"
                        .formatted(answerIndex, answerOption));
            }
        }
    }
}

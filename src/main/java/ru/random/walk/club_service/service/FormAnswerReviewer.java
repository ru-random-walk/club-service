package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;

public interface FormAnswerReviewer {
    /**
     * @param formApprovementData data with correct answers to the questions
     * @param formAnswerData      user data answers for reviewing
     * @return true if all answers are correct and false otherwise
     */
    boolean review(FormApprovementData formApprovementData, FormAnswerData formAnswerData);
}

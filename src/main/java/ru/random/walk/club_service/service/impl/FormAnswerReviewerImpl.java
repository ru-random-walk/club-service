package ru.random.walk.club_service.service.impl;

import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.QuestionAnswer;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.Question;
import ru.random.walk.club_service.service.FormAnswerReviewer;

import java.util.HashSet;
import java.util.List;

@Service
public class FormAnswerReviewerImpl implements FormAnswerReviewer {
    @Override
    public boolean review(FormApprovementData formApprovementData, FormAnswerData formAnswerData) {
        var questions = formApprovementData.getQuestions().toArray(new Question[0]);
        var questionAnswers = formAnswerData.getQuestionAnswers().toArray(new QuestionAnswer[0]);
        for (var i = 0; i < questions.length; i++) {
            if (!checkAnswerOptions(questions[i].correctOptionNumbers(), questionAnswers[i].optionNumbers())) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAnswerOptions(List<Integer> correctOptionNumbers, List<Integer> optionNumbers) {
        var correctOptionsSet = new HashSet<>(correctOptionNumbers);
        for (var option : optionNumbers) {
            correctOptionsSet.remove(option);
        }
        return correctOptionsSet.isEmpty();
    }
}

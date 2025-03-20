package ru.random.walk.club_service.service.reviewer;

import ru.random.walk.club_service.model.model.ForReviewAnswerData;

public interface AnswerReviewer {
    void scheduleReview(ForReviewAnswerData forReviewAnswerData);
}

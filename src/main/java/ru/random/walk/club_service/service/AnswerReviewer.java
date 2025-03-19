package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.model.ForReviewAnswerData;

public interface AnswerReviewer {
    void scheduleReview(ForReviewAnswerData forReviewAnswerData);
}

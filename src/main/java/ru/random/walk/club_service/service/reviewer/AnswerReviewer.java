package ru.random.walk.club_service.service.reviewer;

import ru.random.walk.club_service.model.model.ForReviewData;

public interface AnswerReviewer {
    void scheduleReview(ForReviewData forReviewData);
}

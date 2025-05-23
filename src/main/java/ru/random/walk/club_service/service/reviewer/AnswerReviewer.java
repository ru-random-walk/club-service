package ru.random.walk.club_service.service.reviewer;

import ru.random.walk.club_service.model.model.ForReviewData;

import java.util.concurrent.Future;

public interface AnswerReviewer {
    Future<?> scheduleReview(ForReviewData forReviewData);
}

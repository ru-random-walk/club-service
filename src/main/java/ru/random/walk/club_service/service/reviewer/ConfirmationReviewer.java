package ru.random.walk.club_service.service.reviewer;

import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.util.VirtualThreadUtil;

public interface ConfirmationReviewer {
    void review(ForReviewData forReviewData);

    default void reviewAsync(ForReviewData forReviewData) {
        VirtualThreadUtil.scheduleTask(() -> review(forReviewData));
    }
}

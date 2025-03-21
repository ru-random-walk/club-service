package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.model.ForReviewData;

public interface ConfirmationService {
    void assignApprovers(ForReviewData forReviewData, MembersConfirmApprovementData membersConfirmApprovementData);
}

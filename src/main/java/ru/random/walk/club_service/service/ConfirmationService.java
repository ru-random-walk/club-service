package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.model.ForReviewData;

import java.util.List;
import java.util.UUID;

public interface ConfirmationService {
    void assignApprovers(ForReviewData forReviewData, MembersConfirmApprovementData membersConfirmApprovementData);

    List<ConfirmationEntity> getUserWaitingConfirmations(UUID userId, PaginationInput pagination);

    List<ConfirmationEntity> getApproverWaitingConfirmations(UUID approverId, PaginationInput pagination);
}

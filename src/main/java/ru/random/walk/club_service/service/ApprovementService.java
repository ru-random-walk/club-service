package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.ApprovementEntity;

import java.util.UUID;

public interface ApprovementService {
    ApprovementEntity addForClub(MembersConfirmApprovementData membersConfirmData, UUID clubId);

    ApprovementEntity addForClub(FormApprovementData formApprovementData, UUID clubId);

    ApprovementEntity update(MembersConfirmApprovementData membersConfirmData, UUID approvementId);

    ApprovementEntity update(FormApprovementData formApprovementData, UUID approvementId);

    UUID delete(UUID approvementId);
}

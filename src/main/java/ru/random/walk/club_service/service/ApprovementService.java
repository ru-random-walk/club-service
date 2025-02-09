package ru.random.walk.club_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ApprovementService {
    private final ApprovementRepository approvementRepository;
    private final ClubRepository clubRepository;

    public ApprovementEntity addForClub(MembersConfirmApprovementData membersConfirmData, UUID clubId) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Club with such id not found!"));
        var approvement = ApprovementEntity.builder()
                .club(club)
                .clubId(clubId)
                .data(membersConfirmData)
                .type(ApprovementType.MEMBERS_CONFIRM)
                .build();
        return approvementRepository.save(approvement);
    }

    public ApprovementEntity addForClub(FormApprovementData formApprovementData, UUID clubId) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Club with such id not found!"));
        var approvement = ApprovementEntity.builder()
                .club(club)
                .clubId(clubId)
                .data(formApprovementData)
                .type(ApprovementType.FORM)
                .build();
        return approvementRepository.save(approvement);
    }

    public ApprovementEntity update(MembersConfirmApprovementData membersConfirmData, UUID approvementId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        approvement.setData(membersConfirmData);
        approvement.setType(ApprovementType.MEMBERS_CONFIRM);
        return approvementRepository.save(approvement);
    }

    public ApprovementEntity update(FormApprovementData formApprovementData, UUID approvementId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        approvement.setData(formApprovementData);
        approvement.setType(ApprovementType.FORM);
        return approvementRepository.save(approvement);
    }

    public UUID delete(UUID approvementId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        approvementRepository.delete(approvement);
        return approvement.getId();
    }
}

package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.exception.ValidationException;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.service.ApprovementService;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ApprovementServiceImpl implements ApprovementService {
    private final static int MAX_APPROVEMENT_COUNT_BY_CLUB = 10;

    private final ApprovementRepository approvementRepository;
    private final ClubRepository clubRepository;

    @Override
    public ApprovementEntity addForClub(MembersConfirmApprovementData membersConfirmData, UUID clubId) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Club with such id not found!"));
        checkApprovementLimit(clubId);
        var approvement = ApprovementEntity.builder()
                .club(club)
                .clubId(clubId)
                .data(membersConfirmData)
                .type(ApprovementType.MEMBERS_CONFIRM)
                .build();
        return approvementRepository.save(approvement);
    }

    @Override
    public ApprovementEntity addForClub(FormApprovementData formApprovementData, UUID clubId) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Club with such id not found!"));
        checkApprovementLimit(clubId);
        var approvement = ApprovementEntity.builder()
                .club(club)
                .clubId(clubId)
                .data(formApprovementData)
                .type(ApprovementType.FORM)
                .build();
        return approvementRepository.save(approvement);
    }

    private void checkApprovementLimit(UUID clubId) {
        var approvementCount = approvementRepository.countByClubId(clubId);
        if (approvementCount >= MAX_APPROVEMENT_COUNT_BY_CLUB) {
            throw new ValidationException("Your club are reached max count of approvements!");
        }
    }

    @Override
    public ApprovementEntity update(MembersConfirmApprovementData membersConfirmData, UUID approvementId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        approvement.setData(membersConfirmData);
        approvement.setType(ApprovementType.MEMBERS_CONFIRM);
        return approvementRepository.save(approvement);
    }

    @Override
    public ApprovementEntity update(FormApprovementData formApprovementData, UUID approvementId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        approvement.setData(formApprovementData);
        approvement.setType(ApprovementType.FORM);
        return approvementRepository.save(approvement);
    }

    @Override
    public UUID delete(UUID approvementId) {
        var approvement = approvementRepository.findById(approvementId)
                .orElseThrow(() -> new NotFoundException("Approvement with such id not found!"));
        approvementRepository.delete(approvement);
        return approvement.getId();
    }
}

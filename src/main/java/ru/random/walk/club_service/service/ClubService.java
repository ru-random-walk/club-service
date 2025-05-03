package ru.random.walk.club_service.service;

import org.jetbrains.annotations.Nullable;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface ClubService {
    ClubEntity getClubById(UUID clubId, PaginationInput membersPagination, boolean membersIsRequired, Principal principal);

    ClubEntity createClub(String clubName, @Nullable String description, UUID adminLogin);

    List<Integer> getClubToApproversNumber(List<ClubEntity> clubs);

    ClubEntity createClubWithApprovement(String name, String description, ApprovementData approvementData, UUID adminLogin);
}

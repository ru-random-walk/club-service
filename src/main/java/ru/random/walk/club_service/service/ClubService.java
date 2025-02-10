package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;

import java.security.Principal;
import java.util.UUID;

public interface ClubService {
    ClubEntity getClubById(UUID clubId, PaginationInput membersPagination, boolean membersIsRequired, Principal principal);

    ClubEntity createClub(String clubName, Principal principal);
}

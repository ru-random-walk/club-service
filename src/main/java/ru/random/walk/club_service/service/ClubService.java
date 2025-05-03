package ru.random.walk.club_service.service;

import org.jetbrains.annotations.Nullable;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.graphql.types.PhotoUrl;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface ClubService {
    ClubEntity getClubById(UUID clubId, PaginationInput membersPagination, boolean membersIsRequired, Principal principal);

    ClubEntity createClub(String clubName, @Nullable String description, Principal principal);

    List<Integer> getClubToApproversNumber(List<ClubEntity> clubs);

    PhotoUrl uploadPhotoForClub(UUID clubId, InputStream inputFile) throws IOException;
}

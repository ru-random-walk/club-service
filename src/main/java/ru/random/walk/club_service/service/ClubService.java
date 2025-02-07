package ru.random.walk.club_service.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MembersRepository;

import java.security.Principal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClubService {
    private final ClubRepository clubRepository;
    private final MembersRepository membersRepository;
    private final Authenticator authenticator;

    public ClubEntity getClubById(
            UUID clubId,
            PaginationInput membersPagination,
            boolean membersIsRequired,
            Principal principal
    ) {
        var club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Club with such id not found!"));
        if (membersIsRequired) {
            authenticator.authAdminByClubId(principal, clubId);
            var membersPageable = PageRequest.of(membersPagination.getPage(), membersPagination.getSize());
            var membersPage = membersRepository.findAllByClubId(clubId, membersPageable);
            club.setMembers(membersPage.getContent());
        }
        return club;
    }
}

package ru.random.walk.club_service.service;

import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;

import java.util.UUID;

public interface MemberService {
    MemberEntity changeRole(UUID memberId, UUID clubId, MemberRole memberRole);

    UUID removeFromClub(UUID memberId, UUID clubId);

    MemberEntity addInClub(UUID memberId, UUID clubId);

    void addInClubIfAllTestPassed(UUID memberId, UUID clubId);
}

package ru.random.walk.club_service.service;

import java.security.Principal;
import java.util.UUID;

public interface Authenticator {
    void authAdminByClubId(Principal principal, UUID clubId);
}

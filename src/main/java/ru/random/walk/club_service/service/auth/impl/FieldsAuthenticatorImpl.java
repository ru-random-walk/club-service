package ru.random.walk.club_service.service.auth.impl;

import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.exception.AuthenticationException;
import ru.random.walk.club_service.service.auth.Authenticator;
import ru.random.walk.club_service.service.auth.FieldsAuthenticator;

import java.security.Principal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FieldsAuthenticatorImpl implements FieldsAuthenticator {
    private final Authenticator authenticator;

    @Override
    public void authByClubMembers(DataFetchingEnvironment env, UUID clubId, Principal principal) {
        boolean membersIsRequired = env.getSelectionSet().contains("members");
        if (membersIsRequired) {
            authenticator.authAdminByClubId(principal, clubId);
        }
    }

    @Override
    public void authByClubMembers(DataFetchingEnvironment env) {
        boolean membersIsRequired = env.getSelectionSet().contains("members");
        if (membersIsRequired) {
            throw new AuthenticationException("You have not permissions for request members fields!");
        }
    }
}

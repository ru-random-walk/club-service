package ru.random.walk.club_service.service.auth;

import graphql.schema.DataFetchingEnvironment;

import java.security.Principal;
import java.util.UUID;

public interface FieldsAuthenticator {
    void authByClubMembers(DataFetchingEnvironment env, UUID clubId, Principal principal);
    void authByClubMembers(DataFetchingEnvironment env);
}

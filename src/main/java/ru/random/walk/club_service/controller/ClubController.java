package ru.random.walk.club_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.util.StabDataUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
public class ClubController {
    @QueryMapping
    List<MemberEntity> getClubMembers(
            @Argument UUID clubId,
            @Argument PaginationInput pagination
    ) {
        log.info("""
                        Get club members
                        by club id [{}]
                        with pagination [{}]
                        """,
                clubId, pagination
        );
        return Collections.singletonList(StabDataUtil.memberEntity());
    }

    @QueryMapping
    @Nullable ClubEntity getClub(@Argument UUID clubId) {
        log.info("""
                        Get club
                        by clubId [{}]
                        """,
                clubId
        );
        return StabDataUtil.clubEntity();
    }

    @MutationMapping
    ClubEntity createClub(@Argument String name) {
        log.info("""
                        Create club
                        with name [{}]
                        """,
                name
        );
        return StabDataUtil.clubEntityWith(name);
    }
}

package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.MemberMapper;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.graphql.types.MemberRole;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.util.StabDataUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class UserController {
    private final MemberMapper memberMapper;

    @QueryMapping
    public List<ClubEntity> getUserClubs(
            @Argument UUID userId,
            @Argument PaginationInput pagination
    ) {
        log.info("""
                        Get user clubs
                        by user id [{}]
                        with pagination [{}]
                        """,
                userId, pagination
        );
        return Collections.singletonList(StabDataUtil.clubEntity());
    }

    @QueryMapping
    public List<AnswerEntity> getUserAnswers(
            @Argument UUID userId,
            @Argument PaginationInput pagination
    ) {
        log.info("""
                        Get user answers
                        by user id [{}]
                        with pagination [{}]
                        """,
                userId, pagination
        );
        return List.of(
                StabDataUtil.formAnswerEntity(),
                StabDataUtil.membersConfirmAnswerEntity()
        );
    }

    @MutationMapping
    MemberEntity changeMemberRole(
            @Argument UUID clubId,
            @Argument UUID memberId,
            @Argument MemberRole role
    ) {
        log.info("""
                        Change member role
                        for club id [{}]
                        member id [{}]
                        with role [{}]
                        """,
                clubId, memberId, role
        );
        var memberRole = memberMapper.toDomainMemberRole(role);
        return StabDataUtil.memberEntityWith(memberId, memberRole);
    }
}

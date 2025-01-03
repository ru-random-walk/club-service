package ru.random.walk.club_service.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import ru.random.walk.club_service.mapper.MemberMapper;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.graphql.types.MemberRole;
import ru.random.walk.club_service.util.StubDataUtil;

import java.util.UUID;

@Controller
@Slf4j
@AllArgsConstructor
public class MemberController {
    private final MemberMapper memberMapper;

    @MutationMapping
    public MemberEntity changeMemberRole(
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
        return StubDataUtil.memberEntityWith(memberId, memberRole);
    }
}

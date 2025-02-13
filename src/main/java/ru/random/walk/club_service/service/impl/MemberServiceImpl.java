package ru.random.walk.club_service.service.impl;

import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.MemberService;

import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberEntity changeRole(UUID memberId, UUID clubId, MemberRole memberRole) {
        var member = memberRepository.findByIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new NotFoundException("Member not found in club " + clubId));
        member.setRole(memberRole);
        return memberRepository.save(member);
    }

    @Override
    public UUID removeFromClub(UUID memberId, UUID clubId) {
        var member = memberRepository.findByIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new NotFoundException("Member not found in club " + clubId));
        memberRepository.delete(member);
        return member.getId();
    }

    @Override
    public MemberEntity addInClub(UUID memberId, UUID clubId) {
        return memberRepository.save(MemberEntity.builder()
                .id(memberId)
                .role(MemberRole.USER)
                .clubId(clubId)
                .build());
    }
}

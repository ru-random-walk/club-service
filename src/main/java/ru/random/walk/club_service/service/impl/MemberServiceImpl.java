package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.MemberService;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final AnswerRepository answerRepository;
    private final ClubRepository clubRepository;

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

    @Override
    @Transactional
    public Optional<MemberEntity> addInClubIfAllTestPassed(UUID memberId, UUID clubId) {
        Set<UUID> passedApprovements = answerRepository.findAllByUserIdAndClubId(memberId, clubId).stream()
                .filter(answer -> answer.getStatus() == AnswerStatus.PASSED)
                .map(answer -> answer.getApprovement().getId())
                .collect(Collectors.toSet());
        var clubApprovements = clubRepository.findById(clubId).orElseThrow()
                .getApprovements().stream()
                .map(ApprovementEntity::getId)
                .collect(Collectors.toSet());
        if (passedApprovements.containsAll(clubApprovements)) {
            return Optional.of(
                    memberRepository.save(MemberEntity.builder()
                            .id(memberId)
                            .clubId(clubId)
                            .role(MemberRole.USER)
                            .build())
            );
        }
        return Optional.empty();
    }
}

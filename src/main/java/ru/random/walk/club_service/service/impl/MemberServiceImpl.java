package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.exception.NotFoundException;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.MemberService;
import ru.random.walk.club_service.service.OutboxSenderService;
import ru.random.walk.dto.UserExcludeEvent;
import ru.random.walk.dto.UserJoinEvent;
import ru.random.walk.topic.EventTopic;

import java.util.List;
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
    private final OutboxSenderService outboxSenderService;

    @Override
    public MemberEntity changeRole(UUID memberId, UUID clubId, MemberRole memberRole) {
        var member = memberRepository.findByIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new NotFoundException("Member not found in club " + clubId));
        member.setRole(memberRole);
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public UUID removeFromClub(UUID memberId, UUID clubId) {
        var member = memberRepository.findByIdAndClubId(memberId, clubId)
                .orElseThrow(() -> new NotFoundException("Member not found in club " + clubId));
        memberRepository.delete(member);
        outboxSenderService.sendMessage(
                EventTopic.USER_EXCLUDE,
                UserExcludeEvent.builder()
                        .userId(memberId)
                        .clubId(clubId)
                        .build()
        );
        return member.getId();
    }

    @Override
    @Transactional
    public MemberEntity addInClub(UUID memberId, UUID clubId) {
        var member = memberRepository.save(MemberEntity.builder()
                .id(memberId)
                .role(MemberRole.USER)
                .clubId(clubId)
                .build());
        outboxSenderService.sendMessage(
                EventTopic.USER_JOIN,
                UserJoinEvent.builder()
                        .userId(memberId)
                        .clubId(clubId)
                        .build()
        );
        return member;
    }

    @Override
    @Transactional
    public Optional<MemberEntity> addInClubIfAllTestPassed(UUID memberId, UUID clubId) {
        Set<UUID> passedApprovements = answerRepository.findAllPassedIdsByUserIdAndClubId(memberId, clubId);
        var clubApprovements = clubRepository.findById(clubId).orElseThrow()
                .getApprovements().stream()
                .map(ApprovementEntity::getId)
                .collect(Collectors.toSet());
        if (passedApprovements.containsAll(clubApprovements)) {
            return Optional.of(addInClub(memberId, clubId));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteAllByClubId(UUID clubId) {
        var members = memberRepository.deleteAllByClubId(clubId);
        List<UserExcludeEvent> eventList = members.stream()
                .map(memberEntity -> UserExcludeEvent.builder()
                        .clubId(memberEntity.getClubId())
                        .userId(memberEntity.getId())
                        .build())
                .toList();
        outboxSenderService.sendAllMessages(EventTopic.USER_EXCLUDE, eventList);
    }
}

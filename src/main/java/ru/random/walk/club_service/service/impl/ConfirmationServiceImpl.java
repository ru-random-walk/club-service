package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;
import ru.random.walk.club_service.model.entity.type.ConfirmationStatus;
import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ConfirmationRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.ConfirmationService;

@Service
@AllArgsConstructor
@Slf4j
public class ConfirmationServiceImpl implements ConfirmationService {
    private final ConfirmationRepository confirmationRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void assignApprovers(
            ForReviewData forReviewData,
            MembersConfirmApprovementData membersConfirmApprovementData
    ) {
        var answer = answerRepository.findById(forReviewData.answerId()).orElseThrow();
        var approvers = memberRepository.findRandomApproversByClubId(
                forReviewData.clubId(),
                membersConfirmApprovementData.getRequiredConfirmationNumber()
        );

        log.info("Save all assigned approvers: {}", approvers);
        confirmationRepository.saveAllAndFlush(
                approvers.stream()
                        .map(approver -> ConfirmationEntity.builder()
                                .approverId(approver)
                                .userId(forReviewData.userId())
                                .status(ConfirmationStatus.WAITING)
                                .answer(answer)
                                .build())
                        .toList()
        );
    }
}

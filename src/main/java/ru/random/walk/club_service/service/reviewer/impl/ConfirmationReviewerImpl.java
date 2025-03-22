package ru.random.walk.club_service.service.reviewer.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ConfirmationStatus;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ConfirmationRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.reviewer.ConfirmationReviewer;

@Service
@AllArgsConstructor
@Slf4j
public class ConfirmationReviewerImpl implements ConfirmationReviewer {
    private final ConfirmationRepository confirmationRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void review(ForReviewData forReviewData) {
        log.info("Start to review confirmation answer");
        var appliedConfirmations = confirmationRepository.findAllByAnswerId(forReviewData.answerId()).stream()
                .filter(confirmation -> confirmation.getStatus().equals(ConfirmationStatus.APPLIED))
                .count();
        var confirmApprovementData = ((MembersConfirmApprovementData) forReviewData.approvementData());
        var requiredConfirmationNumber = confirmApprovementData.getRequiredConfirmationNumber();
        if (appliedConfirmations >= requiredConfirmationNumber) {
            answerRepository.updateStatus(forReviewData.answerId(), AnswerStatus.PASSED);
            memberRepository.saveAndFlush(MemberEntity.builder()
                    .id(forReviewData.userId())
                    .role(MemberRole.USER)
                    .clubId(forReviewData.clubId())
                    .build());
        }
    }
}

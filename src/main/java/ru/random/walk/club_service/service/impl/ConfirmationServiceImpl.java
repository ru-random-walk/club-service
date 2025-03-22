package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ConfirmationEntity;
import ru.random.walk.club_service.model.entity.type.ConfirmationStatus;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ApprovementRepository;
import ru.random.walk.club_service.repository.ConfirmationRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.ConfirmationService;
import ru.random.walk.club_service.service.reviewer.ConfirmationReviewer;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ConfirmationServiceImpl implements ConfirmationService {
    private final ConfirmationReviewer confirmationReviewer;
    private final ConfirmationRepository confirmationRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final ApprovementRepository approvementRepository;

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

    @Override
    public List<ConfirmationEntity> getUserWaitingConfirmations(UUID userId, PaginationInput pagination) {
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        return confirmationRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public List<ConfirmationEntity> getApproverWaitingConfirmations(UUID approverId, PaginationInput pagination) {
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        return confirmationRepository.findAllByApproverId(approverId, pageable);
    }

    @Override
    public ConfirmationEntity updateConfirmationStatus(UUID confirmationId, ConfirmationStatus status) {
        log.info("Start update confirmation status");
        ConfirmationWithApprovement result = updateStatus(confirmationId, status);
        confirmationReviewer.reviewAsync(ForReviewData.builder()
                .answerId(result.confirmation().getAnswer().getId())
                .answerData(MembersConfirmAnswerData.INSTANCE)
                .approvementData(result.approvement.getData())
                .userId(result.confirmation().getUserId())
                .clubId(result.approvement.getClubId())
                .build());
        return result.confirmation();
    }

    @NotNull
    @Transactional
    private ConfirmationWithApprovement updateStatus(UUID confirmationId, ConfirmationStatus status) {
        var confirmation = confirmationRepository.updateStatusById(confirmationId, status);
        var answer = answerRepository.findById(confirmation.getAnswer().getId()).orElseThrow();
        var approvement = approvementRepository.findById(answer.getApprovement().getId()).orElseThrow();
        return new ConfirmationWithApprovement(confirmation, approvement);
    }

    private record ConfirmationWithApprovement(ConfirmationEntity confirmation, ApprovementEntity approvement) {
    }
}

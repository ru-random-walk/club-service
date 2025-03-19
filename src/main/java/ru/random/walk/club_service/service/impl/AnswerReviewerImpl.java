package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.model.ForReviewAnswerData;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.service.AnswerReviewer;
import ru.random.walk.club_service.service.FormAnswerReviewer;
import ru.random.walk.club_service.util.VirtualThreadUtil;

@Service
@AllArgsConstructor
public class AnswerReviewerImpl implements AnswerReviewer {
    private final AnswerRepository answerRepository;
    private final FormAnswerReviewer formAnswerReviewer;
    private final MemberRepository memberRepository;

    @Override
    public void scheduleReview(ForReviewAnswerData forReviewAnswerData) {
        VirtualThreadUtil.executor.submit(() -> {
            answerRepository.updateStatus(forReviewAnswerData.id(), AnswerStatus.IN_PROGRESS);
            reviewAnswerData(forReviewAnswerData);
        });
    }

    private void reviewAnswerData(ForReviewAnswerData forReviewAnswerData) {
        switch (forReviewAnswerData.approvementData()) {
            case FormApprovementData formApprovementData ->
                    reviewForm(forReviewAnswerData, formApprovementData, (FormAnswerData) forReviewAnswerData.answerData());
            case MembersConfirmApprovementData membersConfirmApprovementData -> {
                // TODO
            }
            default -> throw new IllegalStateException("Unexpected value: " + forReviewAnswerData.approvementData());
        }
    }

    private void reviewForm(ForReviewAnswerData forReviewAnswerData, FormApprovementData formApprovementData, FormAnswerData formAnswerData) {
        var success = formAnswerReviewer.review(formApprovementData, formAnswerData);
        if (success) {
            answerRepository.updateStatus(forReviewAnswerData.id(), AnswerStatus.PASSED);
            memberRepository.save(MemberEntity.builder()
                    .clubId(forReviewAnswerData.clubId())
                    .id(forReviewAnswerData.userId())
                    .role(MemberRole.USER)
                    .build());
        } else {
            answerRepository.updateStatus(forReviewAnswerData.id(), AnswerStatus.FAILED);
        }
    }
}

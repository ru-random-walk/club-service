package ru.random.walk.club_service.service.reviewer.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.model.ForReviewData;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.service.ConfirmationService;
import ru.random.walk.club_service.service.MemberService;
import ru.random.walk.club_service.service.reviewer.AnswerReviewer;
import ru.random.walk.club_service.service.reviewer.FormAnswerReviewer;
import ru.random.walk.club_service.util.VirtualThreadUtil;

import java.util.concurrent.Future;

@Service
@AllArgsConstructor
public class AnswerReviewerImpl implements AnswerReviewer {
    private final AnswerRepository answerRepository;
    private final FormAnswerReviewer formAnswerReviewer;
    private final MemberService memberService;
    private final ConfirmationService confirmationService;

    @Override
    public Future<AnswerStatus> scheduleReview(ForReviewData forReviewData) {
        return VirtualThreadUtil.executor.submit(() -> {
            answerRepository.updateStatus(forReviewData.answerId(), AnswerStatus.IN_PROGRESS);
            return reviewAnswerData(forReviewData);
        });
    }

    private AnswerStatus reviewAnswerData(ForReviewData forReviewData) {
        return switch (forReviewData.approvementData()) {
            case FormApprovementData formApprovementData -> reviewForm(
                    forReviewData,
                    formApprovementData,
                    (FormAnswerData) forReviewData.answerData()
            );
            case MembersConfirmApprovementData membersConfirmApprovementData -> reviewConfirmation(
                    forReviewData,
                    membersConfirmApprovementData
            );
            default -> throw new IllegalStateException("Unexpected value: " + forReviewData.approvementData());
        };
    }

    private AnswerStatus reviewForm(
            ForReviewData forReviewData,
            FormApprovementData formApprovementData,
            FormAnswerData formAnswerData
    ) {
        var success = formAnswerReviewer.review(formApprovementData, formAnswerData);
        if (success) {
            answerRepository.updateStatus(forReviewData.answerId(), AnswerStatus.PASSED);
            memberService.addInClubIfAllTestPassed(forReviewData.userId(), forReviewData.clubId());
            return AnswerStatus.PASSED;
        } else {
            answerRepository.updateStatus(forReviewData.answerId(), AnswerStatus.FAILED);
            return AnswerStatus.FAILED;
        }
    }

    private AnswerStatus reviewConfirmation(
            ForReviewData forReviewData,
            MembersConfirmApprovementData membersConfirmApprovementData
    ) {
        confirmationService.assignApprovers(forReviewData, membersConfirmApprovementData);
        answerRepository.updateStatus(forReviewData.answerId(), AnswerStatus.IN_REVIEW);
        return AnswerStatus.IN_REVIEW;
    }
}

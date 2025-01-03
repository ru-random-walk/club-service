package ru.random.walk.club_service.util;

import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.domain.answer.QuestionAnswer;
import ru.random.walk.club_service.model.domain.approvement.AnswerType;
import ru.random.walk.club_service.model.domain.approvement.FormApprovementData;
import ru.random.walk.club_service.model.domain.approvement.MembersConfirmApprovementData;
import ru.random.walk.club_service.model.domain.approvement.Question;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ApprovementEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.ApprovementType;
import ru.random.walk.club_service.model.entity.type.MemberRole;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StubDataUtil {
    public static Question question() {
        return Question.builder()
                .text("How many cards are gold in the board game 'Gnome Pests' when playing with four players?")
                .answerOptions(
                        List.of(
                                "1", "2", "3", "10"
                        )
                )
                .answerType(AnswerType.SINGLE)
                .correctOptionNumbers(
                        Collections.singletonList(0)
                )
                .build();
    }

    public static FormApprovementData formApprovementData() {
        return new FormApprovementData(
                Collections.singletonList(question())
        );
    }

    public static ApprovementEntity formApprovementEntity() {
        return ApprovementEntity.builder()
                .id(UUID.randomUUID())
                .type(ApprovementType.FORM)
                .data(formApprovementData())
                .build();
    }

    public static MembersConfirmApprovementData membersConfirmApprovementData() {
        return new MembersConfirmApprovementData(2);
    }

    public static ApprovementEntity membersConfirmApprovementEntity() {
        return ApprovementEntity.builder()
                .id(UUID.randomUUID())
                .type(ApprovementType.MEMBERS_CONFIRM)
                .data(membersConfirmApprovementData())
                .build();
    }

    public static ClubEntity clubEntity() {
        return ClubEntity.builder()
                .id(UUID.randomUUID())
                .name("Board games club")
                .members(
                        Collections.singletonList(
                                MemberEntity.builder()
                                        .id(UUID.randomUUID())
                                        .role(MemberRole.ADMIN)
                                        .build()
                        )
                )
                .approvements(
                        List.of(formApprovementEntity(), membersConfirmApprovementEntity())
                )
                .build();
    }

    public static MemberEntity memberEntity() {
        return MemberEntity.builder()
                .id(UUID.randomUUID())
                .role(MemberRole.ADMIN)
                .build();
    }

    public static AnswerData membersConfirmAnswerData() {
        return new MembersConfirmAnswerData(1);
    }

    public static AnswerEntity membersConfirmAnswerEntity() {
        return AnswerEntity.builder()
                .approvement(membersConfirmApprovementEntity())
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .status(AnswerStatus.SENT)
                .data(membersConfirmAnswerData())
                .build();
    }

    public static AnswerData formAnswerData() {
        return new FormAnswerData(
                List.of(
                        new QuestionAnswer(
                                List.of(2)
                        )
                )
        );
    }

    public static AnswerEntity formAnswerEntity() {
        return AnswerEntity.builder()
                .approvement(formApprovementEntity())
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .status(AnswerStatus.SENT)
                .data(formAnswerData())
                .build();
    }

    public static ClubEntity clubEntityWith(String name) {
        return ClubEntity.builder()
                .id(UUID.randomUUID())
                .name(name)
                .approvements(Collections.emptyList())
                .members(
                        Collections.singletonList(memberEntity())
                )
                .build();
    }

    public static ApprovementEntity membersConfirmApprovementEntityWith(MembersConfirmApprovementData membersConfirmApprovementData) {
        var membersConfirmApprovementEntity = membersConfirmApprovementEntity();
        membersConfirmApprovementEntity.setData(membersConfirmApprovementData);
        return membersConfirmApprovementEntity;
    }

    public static ApprovementEntity formApprovementEntityWith(FormApprovementData formApprovementData) {
        var formApprovementEntity = formApprovementEntity();
        formApprovementEntity.setData(formApprovementData);
        return formApprovementEntity;
    }

    public static MemberEntity memberEntityWith(UUID memberId, MemberRole memberRole) {
        return MemberEntity.builder()
                .id(memberId)
                .role(memberRole)
                .build();
    }

    public static AnswerEntity answerMembersConfirmEntityWith(UUID approvementId) {
        var membersConfirmApprovementEntity = membersConfirmApprovementEntity();
        membersConfirmApprovementEntity.setId(approvementId);
        return AnswerEntity.builder()
                .approvement(membersConfirmApprovementEntity)
                .userId(UUID.randomUUID())
                .status(AnswerStatus.SENT)
                .build();
    }

    public static AnswerEntity answerFormEntityWith(UUID approvementId, FormAnswerData formAnswerData) {
        var formApprovementEntity = formApprovementEntity();
        formApprovementEntity.setId(approvementId);
        return AnswerEntity.builder()
                .approvement(formApprovementEntity)
                .userId(UUID.randomUUID())
                .data(formAnswerData)
                .status(AnswerStatus.CREATED)
                .build();
    }

    public static AnswerEntity answerFormEntityWith(UUID approvementId, AnswerStatus answerStatus) {
        var formApprovementEntity = formApprovementEntity();
        formApprovementEntity.setId(approvementId);
        return AnswerEntity.builder()
                .approvement(formApprovementEntity)
                .userId(UUID.randomUUID())
                .data(formAnswerData())
                .status(answerStatus)
                .build();
    }
}

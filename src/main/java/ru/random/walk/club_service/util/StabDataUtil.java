package ru.random.walk.club_service.util;

import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.domain.answer.FormAnswerData;
import ru.random.walk.club_service.model.domain.answer.MembersConfirmAnswerData;
import ru.random.walk.club_service.model.domain.answer.QuestionAnswer;
import ru.random.walk.club_service.model.domain.test.AnswerType;
import ru.random.walk.club_service.model.domain.test.FormTestData;
import ru.random.walk.club_service.model.domain.test.MembersConfirmTestData;
import ru.random.walk.club_service.model.domain.test.Question;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.TestEntity;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;
import ru.random.walk.club_service.model.entity.type.MemberRole;
import ru.random.walk.club_service.model.entity.type.TestType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StabDataUtil {
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

    public static FormTestData formTestData() {
        return new FormTestData(
                Collections.singletonList(question())
        );
    }

    public static TestEntity formTestEntity() {
        return TestEntity.builder()
                .id(UUID.randomUUID())
                .type(TestType.FORM)
                .data(formTestData())
                .build();
    }

    public static MembersConfirmTestData membersConfirmTestData() {
        return new MembersConfirmTestData(2);
    }

    public static TestEntity membersConfirmTestEntity() {
        return TestEntity.builder()
                .id(UUID.randomUUID())
                .type(TestType.MEMBERS_CONFIRM)
                .data(membersConfirmTestData())
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
                .tests(
                        List.of(formTestEntity(), membersConfirmTestEntity())
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
                .test(membersConfirmTestEntity())
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
                .test(formTestEntity())
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
                .tests(Collections.emptyList())
                .members(
                        Collections.singletonList(memberEntity())
                )
                .build();
    }

    public static TestEntity membersConfirmTestEntityWith(MembersConfirmTestData membersConfirmTestData) {
        var membersConfirmTestEntity = membersConfirmTestEntity();
        membersConfirmTestEntity.setData(membersConfirmTestData);
        return membersConfirmTestEntity;
    }

    public static TestEntity formTestEntityWith(FormTestData formTestData) {
        var formTestEntity = formTestEntity();
        formTestEntity.setData(formTestData);
        return formTestEntity;
    }

    public static MemberEntity memberEntityWith(UUID memberId, MemberRole memberRole) {
        return MemberEntity.builder()
                .id(memberId)
                .role(memberRole)
                .build();
    }

    public static AnswerEntity answerMembersConfirmEntityWith(UUID testId) {
        var membersConfirmTestEntity = membersConfirmTestEntity();
        membersConfirmTestEntity.setId(testId);
        return AnswerEntity.builder()
                .test(membersConfirmTestEntity)
                .userId(UUID.randomUUID())
                .status(AnswerStatus.SENT)
                .build();
    }

    public static AnswerEntity answerFormEntityWith(UUID testId, FormAnswerData formAnswerData) {
        var formTestEntity = formTestEntity();
        formTestEntity.setId(testId);
        return AnswerEntity.builder()
                .test(formTestEntity)
                .userId(UUID.randomUUID())
                .data(formAnswerData)
                .status(AnswerStatus.CREATED)
                .build();
    }

    public static AnswerEntity answerFormEntityWith(UUID testId, AnswerStatus answerStatus) {
        var formTestEntity = formTestEntity();
        formTestEntity.setId(testId);
        return AnswerEntity.builder()
                .test(formTestEntity)
                .userId(UUID.randomUUID())
                .data(formAnswerData())
                .status(answerStatus)
                .build();
    }
}

package ru.random.walk.club_service.model.domain.answer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MembersConfirmAnswerData extends AnswerData {
    public static final MembersConfirmAnswerData INSTANCE = new MembersConfirmAnswerData();
}

package ru.random.walk.club_service.model.domain.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class MembersConfirmAnswerData extends AnswerData {
    public static final MembersConfirmAnswerData DEFAULT = MembersConfirmAnswerData.of(0);

    private Integer actualConfirmationNumber;
}

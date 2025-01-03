package ru.random.walk.club_service.model.domain.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembersConfirmAnswerData extends AnswerData {
    private Integer actualConfirmationNumber;
}

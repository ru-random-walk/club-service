package ru.random.walk.club_service.model.domain.answer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MembersConfirmAnswerData.class, name = "members_confirm_answer_data"),
        @JsonSubTypes.Type(value = FormAnswerData.class, name = "form_answer_data"),
})
@AllArgsConstructor
@Getter
@Setter
public abstract class AnswerData {
}

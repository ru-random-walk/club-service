package ru.random.walk.club_service.model.domain.approvement;

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
        @JsonSubTypes.Type(value = MembersConfirmApprovementData.class, name = "members_confirm_approvement_data"),
        @JsonSubTypes.Type(value = FormApprovementData.class, name = "form_approvement_data"),
})
@AllArgsConstructor
@Getter
@Setter
public abstract class ApprovementData {
}

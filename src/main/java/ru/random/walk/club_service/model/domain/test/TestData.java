package ru.random.walk.club_service.model.domain.test;

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
        @JsonSubTypes.Type(value = MembersConfirmTestData.class, name = "members_confirm_test_data"),
        @JsonSubTypes.Type(value = FormTestData.class, name = "form_test_data"),
})
@AllArgsConstructor
@Getter
@Setter
public abstract class TestData {
}

package ru.random.walk.club_service.model.domain.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembersConfirmTestData extends TestData {
    private Integer requiredConfirmationNumber;
}

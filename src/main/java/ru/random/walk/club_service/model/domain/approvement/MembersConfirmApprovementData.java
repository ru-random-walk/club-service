package ru.random.walk.club_service.model.domain.approvement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembersConfirmApprovementData extends ApprovementData {
    private Integer requiredConfirmationNumber;
}

package ru.random.walk.club_service.model.domain.approvement;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MembersConfirmApprovementData extends ApprovementData {
    private Integer requiredConfirmationNumber;
    @Nullable
    private Integer approversToNotifyCount;

    public MembersConfirmApprovementData(Integer requiredConfirmationNumber) {
        this.requiredConfirmationNumber = requiredConfirmationNumber;
    }
}

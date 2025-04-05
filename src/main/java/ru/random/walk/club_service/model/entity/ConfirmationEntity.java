package ru.random.walk.club_service.model.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.random.walk.club_service.model.entity.type.ConfirmationStatus;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "confirmation", schema = "club")
public class ConfirmationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "approver_id", nullable = false)
    private UUID approverId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfirmationStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private AnswerEntity answer;

    @Override
    public String toString() {
        return "ConfirmationEntity{" +
                "id=" + id +
                ", approverId=" + approverId +
                ", userId=" + userId +
                ", status=" + status +
                '}';
    }
}

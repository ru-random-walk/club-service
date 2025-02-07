package ru.random.walk.club_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.random.walk.club_service.model.entity.type.MemberRole;

import java.io.Serializable;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@IdClass(MemberEntity.MemberId.class)
@Table(name = "member", schema = "club")
public class MemberEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Id
    @Column(name = "club_id", nullable = false)
    private UUID clubId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberId implements Serializable {
        private UUID id;
        private UUID clubId;
    }
}

package ru.random.walk.club_service.model.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "club", schema = "club")
public class ClubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    @Nullable
    private String description;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false, insertable = false, updatable = false)
    private List<MemberEntity> members;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false, insertable = false, updatable = false)
    @Builder.Default
    private List<ApprovementEntity> approvements = new ArrayList<>();
}

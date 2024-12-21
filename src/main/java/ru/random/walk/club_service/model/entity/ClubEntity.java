package ru.random.walk.club_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany
    @JoinColumn(name = "club_id", nullable = false)
    private List<MemberEntity> members;

    @ManyToMany
    @JoinTable(
            name = "tests",
            schema = "club",
            joinColumns = @JoinColumn(name = "club_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "test_id", nullable = false)
    )
    private List<TestEntity> tests;
}

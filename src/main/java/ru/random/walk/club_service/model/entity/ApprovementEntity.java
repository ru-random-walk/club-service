package ru.random.walk.club_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.random.walk.club_service.converter.ApprovementDataConverter;
import ru.random.walk.club_service.model.domain.approvement.ApprovementData;
import ru.random.walk.club_service.model.entity.type.ApprovementType;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "approvement", schema = "club")
public class ApprovementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovementType type;

    @Convert(converter = ApprovementDataConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private ApprovementData data;
}

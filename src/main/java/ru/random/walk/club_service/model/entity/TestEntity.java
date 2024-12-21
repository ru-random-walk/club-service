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
import ru.random.walk.club_service.converter.TestDataConverter;
import ru.random.walk.club_service.model.domain.test.TestData;
import ru.random.walk.club_service.model.entity.type.TestType;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "test", schema = "club")
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType type;

    @Convert(converter = TestDataConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private TestData data;
}

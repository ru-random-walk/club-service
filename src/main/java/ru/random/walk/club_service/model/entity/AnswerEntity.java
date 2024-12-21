package ru.random.walk.club_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.random.walk.club_service.converter.AnswerDataConverter;
import ru.random.walk.club_service.model.domain.answer.AnswerData;
import ru.random.walk.club_service.model.entity.type.AnswerStatus;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "answer", schema = "club")
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "test_id", nullable = false)
    private TestEntity test;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Convert(converter = AnswerDataConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private AnswerData data;

    @Column(nullable = false)
    private AnswerStatus status;
}

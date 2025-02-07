package ru.random.walk.club_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.random.walk.club_service.model.entity.MemberEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MembersRepository extends CrudRepository<MemberEntity, MemberEntity.MemberId> {
    Page<MemberEntity> findAllByClubId(UUID clubId, Pageable pageable);

    Optional<MemberEntity> findByIdAndClubId(UUID login, UUID clubId);
}

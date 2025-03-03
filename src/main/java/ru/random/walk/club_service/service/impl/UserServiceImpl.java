package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.entity.AnswerEntity;
import ru.random.walk.club_service.model.entity.ClubEntity;
import ru.random.walk.club_service.model.entity.MemberEntity;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.model.graphql.types.PaginationInput;
import ru.random.walk.club_service.repository.AnswerRepository;
import ru.random.walk.club_service.repository.ClubRepository;
import ru.random.walk.club_service.repository.MemberRepository;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final AnswerRepository answerRepository;

    @Override
    public void add(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    public Iterable<ClubEntity> getClubs(UUID userId, PaginationInput pagination) {
        var pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        var clubIds = memberRepository.findAllById(userId, pageable)
                .map(MemberEntity::getClubId)
                .toList();
        return clubRepository.findAllById(clubIds);
    }

    @Override
    public List<AnswerEntity> getAnswers(UUID userId, PaginationInput pagination) {
        var pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        return answerRepository.findAllByUserId(userId, pageable);
    }
}

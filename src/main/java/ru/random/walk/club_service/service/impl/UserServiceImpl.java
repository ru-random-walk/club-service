package ru.random.walk.club_service.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.random.walk.club_service.model.entity.UserEntity;
import ru.random.walk.club_service.repository.UserRepository;
import ru.random.walk.club_service.service.UserService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void add(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
}

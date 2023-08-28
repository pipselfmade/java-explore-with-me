package ru.practicum.ewmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.input.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.ewmservice.exception.EmailConflictException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.mapper.UserMapper;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto created(NewUserDto newUser) {
        log.info("Task created new user {}", newUser);
        final User user = UserMapper.toModel(newUser);
        log.debug("try save new user {}", user);
        validateEmail(newUser.getEmail());
        final User savedUser = userRepository.save(user);
        log.debug("User created successfully, user id {}", savedUser.getId());
        return UserMapper.toDto(savedUser);
    }

    @Override
    public List<UserDto> findUserById(List<Long> ids, Pageable page) {
        log.info("Task get collection users");
        List<User> result = (ids != null) ?
                userRepository.findByIdIn(ids, page) :
                userRepository.findAll(page).getContent();
        log.debug("Number of users found '{}'", result.size());
        return result.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        log.info("Task deleted user by id {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id '" + userId + "' not found");
        }

        userRepository.deleteById(userId);
        log.debug("User with id {} deleted - {}", userId, !userRepository.existsById(userId));
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailConflictException("Email '" + email + "' is exist");
        }
    }
}

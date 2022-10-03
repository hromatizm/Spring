package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * ORM implementation.
 * Uses default implementations of checking methods from BookService interface.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkUserDtoForNull(userDto);

        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user (dto -> entity): {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    /**
     * Mapper is used for entity updating.
     */
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        checkUserIdForNull(userId);
        checkUserDtoForNull(userDto);

        Person incomingUser = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user (dto -> entity): {}", incomingUser);

        Person currentUser = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User with id %d not found", userId)));
        log.info("Current user found for update: {}", currentUser);

        Person updatedUser = userMapper.updatePerson(incomingUser, currentUser);
        log.info("User updated: {}", updatedUser);

        userRepository.save(updatedUser);
        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        checkUserIdForNull(userId);

        Person user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User with id %d not found", userId)));
        log.info("User found: {}", user);

        return userMapper.personToUserDto(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUserIdForNull(userId);
        UserDto userToDelete = getUserById(userId);
        userRepository.deleteById(userId);
        log.info("User deleted: {}", userToDelete);
    }
}

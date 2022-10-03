package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.WrongUserException;
import org.springframework.stereotype.Repository;

@Repository
public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long id);

    UserDto getUserById(Long id);

    void deleteUserById(Long id);

    default void checkUserIdForNull(Long id) {
        if (id == null) {
            throw new WrongUserException("User id is null");
        }
    }

    default void checkUserDtoForNull(UserDto userDto) {
        if (userDto == null) {
            throw new WrongUserException("User is null");
        }
    }
}

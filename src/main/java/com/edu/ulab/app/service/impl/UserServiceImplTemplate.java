package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.RowToUserDtoMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.util.GeneratedKeyHolderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

/**
 * JDBC Template implementation.
 * Uses default implementations of checking methods from BookService interface.
 */
@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final GeneratedKeyHolderFactory keyHolderFactory;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, GeneratedKeyHolderFactory keyHolderFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.keyHolderFactory = keyHolderFactory;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkUserDtoForNull(userDto);

        final String INSERT_SQL = "INSERT INTO ULAB_EDU.PERSON(ID, FULL_NAME, TITLE, AGE) " +
                "VALUES (nextval('sequence'),?,?,?)";
        KeyHolder keyHolder = keyHolderFactory.newKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        checkUserIdForNull(userId);
        checkUserDtoForNull(userDto);

        final String UPDATE_SQL = "UPDATE ULAB_EDU.PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ?  WHERE ID = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setInt(3, userDto.getAge());
                    ps.setLong(4, userId);
                    return ps;
                }
        );
        log.info("User updated: {}", userId);
        return getUserById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        checkUserIdForNull(userId);

        final String SELECT_SQL = "SELECT * FROM ULAB_EDU.PERSON WHERE ID = ?";
        List<UserDto> userList = jdbcTemplate.query(
                SELECT_SQL, preparedStatement -> preparedStatement.setLong(1, userId),
                new RowToUserDtoMapper());

        if (userList.size() == 0) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
        log.info("User found: {}", userId);
        return userList.get(0);
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUserIdForNull(userId);

        final String DELETE_SQL = "DELETE FROM ULAB_EDU.PERSON WHERE ID = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
                    ps.setLong(1, userId);
                    return ps;
                }
        );
        log.info("User deleted: {}", userId);
    }
}

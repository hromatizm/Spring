package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.RowToUserDtoMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.util.JdbcHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;

/**
 * Pure JDBC implementation.
 * Uses methods from utility class JdbcHelper for:
 * 1) JDBC Driver activation
 * 2) Connection activation
 * 3) SQLException handling
 * 4) Resource closing
 * Uses default implementations of checking methods from BookService interface.
 */
@Slf4j
@Service
public class UserServiceImplJDBC implements UserService {

    static {
        JdbcHelper.activateDriver();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkUserDtoForNull(userDto);

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String INSERT_SQL = "INSERT INTO ULAB_EDU.PERSON(ID, FULL_NAME, TITLE, AGE) " +
                    "VALUES (nextval('sequence'),?,?,?)";
            ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userDto.getFullName());
            ps.setString(2, userDto.getTitle());
            ps.setLong(3, userDto.getAge());
            ps.executeUpdate();

            resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                Long userId = resultSet.getLong(1);
                userDto.setId(userId);
                log.info("User id set: {}", userId);
            }
        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(resultSet, ps, connection);
        }
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        checkUserIdForNull(userId);
        checkUserDtoForNull(userDto);

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String UPDATE_SQL = "UPDATE ULAB_EDU.PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ?  WHERE ID = ?";
            ps = connection.prepareStatement(UPDATE_SQL);
            ps.setString(1, userDto.getFullName());
            ps.setString(2, userDto.getTitle());
            ps.setInt(3, userDto.getAge());
            ps.setLong(4, userId);
            ps.executeUpdate();
            log.info("User updated: {}", userId);

        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(ps, connection);
        }
        return getUserById(userId);
    }

    /**
     * In order to avoid overhead, gets the data from the row using the column number.
     * Because using a column name might add overhead: need to get column metadata info.
     */
    @Override
    public UserDto getUserById(Long userId) {
        checkUserIdForNull(userId);

        UserDto userDto = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String SELECT_SQL = "SELECT FULL_NAME, TITLE, AGE FROM ULAB_EDU.PERSON WHERE ID = ?";
            ps = connection.prepareStatement(SELECT_SQL);
            ps.setLong(1, userId);

            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                userDto = new UserDto();
                userDto.setId(userId);
                userDto.setFullName(resultSet.getString(1));
                userDto.setTitle(resultSet.getString(2));
                userDto.setAge(resultSet.getInt(3));
                log.info("Row converted to UserDto: {}", userDto);
            }
        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(resultSet, ps, connection);
        }

        if (userDto == null) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
        log.info("User found: {}", userId);
        return userDto;
    }

    @Override
    public void deleteUserById(Long userId) {
        checkUserIdForNull(userId);

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String DELETE_SQL = "DELETE FROM ULAB_EDU.PERSON WHERE ID = ?";
            ps = connection.prepareStatement(DELETE_SQL);
            ps.setLong(1, userId);
            ps.executeUpdate();
            log.info("User deleted: {}", userId);

        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(ps, connection);
        }
    }
}

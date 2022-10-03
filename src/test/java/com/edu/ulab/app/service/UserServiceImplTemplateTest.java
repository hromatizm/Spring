package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.WrongUserException;
import com.edu.ulab.app.mapper.RowToUserDtoMapper;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.util.GeneratedKeyHolderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing Template user functionality.")
public class UserServiceImplTemplateTest {
    @InjectMocks
    UserServiceImplTemplate userService;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    GeneratedKeyHolderFactory keyHolderFactory;

    UserDto createTestUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setAge(11);
        return userDto;
    }

    @Test
    @DisplayName("Создание User. Должно пройти успешно. 1 вызов jdbcTemplate.update().")
    void saveUser_Test() {
        //given

        UserDto userDto = createTestUserDto();

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        keyHolder.getKeyList().add(Map.of("key", 1L));

        //when

        when(keyHolderFactory.newKeyHolder()).thenReturn(keyHolder);

        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
        assertEquals(11, userDtoResult.getAge());

        verify(jdbcTemplate, times(1))
                .update(any(PreparedStatementCreator.class), any(GeneratedKeyHolder.class));
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Обновление User. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void updateUser_Test() {
        //given

        UserDto incomingUserDto = createTestUserDto();
        incomingUserDto.setId(1L);

        UserDto updatedUserDto = createTestUserDto();
        updatedUserDto.setId(1L);
        List<UserDto> userDtoList = List.of(updatedUserDto);

        final String SELECT_SQL = "SELECT * FROM ULAB_EDU.PERSON WHERE ID = ?";

        //when

        when(jdbcTemplate.query(
                eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToUserDtoMapper.class))
        ).thenReturn(userDtoList);

        //then

        UserDto userDtoResult = userService.updateUser(incomingUserDto, 1L);
        assertEquals(1L, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
        assertEquals(11, userDtoResult.getAge());

        verify(jdbcTemplate, times(1))
                .update(any(PreparedStatementCreator.class));

        verify(jdbcTemplate, times(1))
                .query(eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToUserDtoMapper.class));

        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void getUser_Test() {
        //given

        UserDto userDto = createTestUserDto();
        userDto.setId(1L);
        List<UserDto> userDtoList = List.of(userDto);

        final String SELECT_SQL = "SELECT * FROM ULAB_EDU.PERSON WHERE ID = ?";

        //when

        when(jdbcTemplate.query(
                eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToUserDtoMapper.class))
        ).thenReturn(userDtoList);

        //then

        UserDto userDtoResult = userService.getUserById(1L);
        assertEquals(1L, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
        assertEquals(11, userDtoResult.getAge());

        verify(jdbcTemplate, times(1))
                .query(eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToUserDtoMapper.class));
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void deleteUser_Test() {
        //given

        //when

        //then
        userService.deleteUserById(1L);
        verify(jdbcTemplate, times(1))
                .update(any(PreparedStatementCreator.class));
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Получение несуществующего юзера. NotFoundException.")
    void getNotExistUser_Test() {
        //given

        //when

        Throwable throwable = catchThrowable(() ->
                userService.getUserById(1L)
        );

        //then

        assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id 1 not found");
    }

    @Test
    @DisplayName("Сохранение null UserDto. WrongUserException")
    void saveNullUserDto_Test() {
        //given

        UserDto nullUserDto = null;

        //when

        Throwable throwable = catchThrowable(() ->
                userService.createUser(nullUserDto)
        );

        //then

        assertThat(throwable)
                .isInstanceOf(WrongUserException.class)
                .hasMessage("User is null");
    }
}
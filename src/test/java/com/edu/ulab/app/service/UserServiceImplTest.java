package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.WrongUserException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing JPA user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    UserDto createTestUserDto() {
        UserDto userDto = new UserDto();
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setAge(11);
        return userDto;
    }

    Person createTestPerson() {
        Person person = new Person();
        person.setId(1L);
        person.setFullName("test name");
        person.setTitle("test title");
        person.setAge(11);
        return person;
    }

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void savePerson_Test() {
        //given

        UserDto userDto = createTestUserDto();
        Person person = createTestPerson();

        Person savedPerson = createTestPerson();
        savedPerson.setId(1L);

        UserDto result = createTestUserDto();
        result.setId(1L);

        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
        assertEquals(11, userDtoResult.getAge());

        verify(userMapper, times(1)).userDtoToPerson(userDto);
        verify(userRepository, times(1)).save(person);
        verify(userMapper, times(1)).personToUserDto(savedPerson);

        verifyNoMoreInteractions(userMapper);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void updatePerson_Test() {
        //given

        UserDto incomingUser = createTestUserDto();
        incomingUser.setId(1L);

        Person incomingPerson = createTestPerson();
        incomingPerson.setId(1L);

        Person currentPerson = new Person();
        currentPerson.setId(1L);
        currentPerson.setAge(22);
        currentPerson.setFullName("current name");
        currentPerson.setTitle("current title");

        Person updatedPerson = createTestPerson();
        updatedPerson.setId(1L);

        UserDto updatedUser = createTestUserDto();
        updatedUser.setId(1L);
        //when

        when(userMapper.userDtoToPerson(incomingUser)).thenReturn(incomingPerson);
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentPerson));
        when(userMapper.updatePerson(incomingPerson, currentPerson)).thenReturn(updatedPerson);
        when(userMapper.personToUserDto(updatedPerson)).thenReturn(updatedUser);

        //then

        UserDto userDtoResult = userService.updateUser(incomingUser, 1L);
        assertEquals(1L, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
        assertEquals(11, userDtoResult.getAge());

        verify(userMapper, times(1)).userDtoToPerson(incomingUser);
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).updatePerson(incomingPerson, currentPerson);
        verify(userRepository, times(1)).save(updatedPerson);
        verify(userMapper, times(1)).personToUserDto(updatedPerson);

        verifyNoMoreInteractions(userMapper);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void getPerson_Test() {
        //given

        Person person = createTestPerson();
        person.setId(1L);

        UserDto userDto = createTestUserDto();
        userDto.setId(1L);

        //when

        when(userRepository.findById(1L)).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(userDto);

        //then

        UserDto userDtoResult = userService.getUserById(1L);
        assertEquals(1L, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
        assertEquals(11, userDtoResult.getAge());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).personToUserDto(person);

        verifyNoMoreInteractions(userMapper);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void deletePerson_Test() {
        //given

        Person person = createTestPerson();
        person.setId(1L);

        UserDto userDto = createTestUserDto();
        userDto.setId(1L);

        //when

        when(userRepository.findById(1L)).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(userDto);

        //then
        userService.deleteUserById(1L);
        verify(userRepository, times(1)).deleteById(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).personToUserDto(person);

        verifyNoMoreInteractions(userMapper);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Получение несуществующего пользователя. NotFoundException.")
    void getNotExistPerson_Test() {
        //given

        //when

        when(userRepository.findById(1L)).thenThrow(
                new NotFoundException("User with id 1 not found")
        );

        Throwable throwable = catchThrowable(() ->
                userService.getUserById(1L)
        );

        //then

        assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id 1 not found");
    }

    @Test
    @DisplayName("Сохранение null пользователя. WrongUserException")
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

    @Test
    @DisplayName("Сохранение null Person. InvalidDataAccessApiUsageException")
    void createNullPerson_Test() {
        //given

        UserDto userDto = createTestUserDto();
        userDto.setId(1L);

        Person nullPerson = null;

        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(nullPerson);
        doThrow(new InvalidDataAccessApiUsageException("Entity must not be null."))
                .when(userRepository).save(nullPerson);

        //then

        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessage("Entity must not be null.");
    }

    // * failed
    //         doThrow(dataInvalidException).when(testRepository)
    //                .save(same(test));
    // example failed
    //  assertThatThrownBy(() -> testeService.createTest(testRequest))
    //                .isInstanceOf(DataInvalidException.class)
    //                .hasMessage("Invalid data set");
}

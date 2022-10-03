package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
@DisplayName("Testing User Repository.")
@Rollback
@Sql({"classpath:sql/1_clear_schema.sql",
        "classpath:sql/2_insert_book_data.sql",
        "classpath:sql/3_insert_person_data.sql"})
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @Test
    @DisplayName("Сохранить Person. Проверить: сохраненные поля, кол-во Person в БД, кол-во DML.")
    void insertPerson_thenAssert_Fields_PersonCount_DmlCount() {
        // Given

        Person person = new Person();
        person.setFullName("Test Test");
        person.setTitle("reader");
        person.setAge(111);
        person.setCountry("Russia");

        // When

        Person result = userRepository.saveAndFlush(person);
        long personCount = userRepository.count();

        // Then

        assertThat(result.getFullName()).isEqualTo("Test Test");
        assertThat(result.getTitle()).isEqualTo("reader");
        assertThat(result.getAge()).isEqualTo(111);
        assertThat(result.getCountry()).isEqualTo("Russia");

        assertThat(personCount).isEqualTo(6);

        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName(
            "Получить Person, обновить поля, сохранить. Проверить: новые поля, кол-во Person в БД, кол-во DML-вызовов."
    )
    void updatePerson_thenAssert_Fields_PersonCount_DmlCount() {
        // Given - Persons from 3_insert_person_data.sql

        Person person = userRepository.findById(1001L).get();
        person.setFullName("updated fullName");
        person.setTitle("updated reader");
        person.setAge(66);
        person.setCountry("updated country");

        // When

        Person result = userRepository.save(person);
        long personCount = userRepository.count();

        // Then

        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getFullName()).isEqualTo("updated fullName");
        assertThat(result.getTitle()).isEqualTo("updated reader");
        assertThat(result.getAge()).isEqualTo(66);
        assertThat(result.getCountry()).isEqualTo("updated country");

        assertThat(personCount).isEqualTo(5);

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(1);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Получить Person. Проверить: новые поля, кол-во Person в БД, кол-во DML-вызовов")
    void getPerson_thenAssert_Fields_PersonCount_DmlCount() {
        // Given - Persons from 3_insert_person_data.sql

        // When

        Person result = userRepository.findById(1001L).get();
        long personCount = userRepository.count();

        // Then

        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getFullName()).isEqualTo("user1");
        assertThat(result.getTitle()).isEqualTo("reader1");
        assertThat(result.getAge()).isEqualTo(11);
        assertThat(result.getCountry()).isEqualTo("country1");

        assertThat(personCount).isEqualTo(5);

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Получить всех Person. Проверить: кол-во полученных Person, кол-во DML-вызовов.")
    void getAllPersons_thenAssert_PersonCount_DmlCount() {
        // Given - Persons from 3_insert_person_data.sql

        // When

        List<Person> personList = userRepository.findAll();

        // Then

        assertThat(personList.size()).isEqualTo(5);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Удалить 2-х Person из 5-ти. Проверить: кол-во Person в БД, кол-во DML-вызовов.")
    void delete2Persons_thenAssert_PersonCount_DmlCount() {
        // Given - Persons from 3_insert_person_data.sql

        // When

        userRepository.deleteById(1001L);
        userRepository.deleteById(1002L);

        long personCount = userRepository.count();

        // Then

        assertThat(personCount).isEqualTo(3);

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    @DisplayName("Удалить Person с id = 1. Проверить: EmptyResultDataAccessException.")
    void DeleteNotExistingPerson_thenAssertException() {

        // When

        Throwable throwable = catchThrowable(() -> userRepository.deleteById(1L));

        // Then

        assertThat(throwable)
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessageStartingWith("No class")
                .hasMessageEndingWith("Person entity with id 1 exists!")
                .hasStackTraceContaining("Unknown Source")
                .hasNoCause();
    }

    @Test
    @DisplayName("Добавить Person с повторяющимся title. Проверить: DataIntegrityViolationException.")
    void AddPersonWithRepeatedTitle_thenAssertException() {
        // Given

        Person person = new Person();
        person.setFullName("Test Test");
        person.setTitle("reader1");
        person.setAge(111);
        person.setCountry("Russia");

        // When

        Throwable throwable = catchThrowable(() ->
            userRepository.saveAndFlush(person)
        );

         // Then

        assertThat(throwable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("could not execute statement")
                .hasStackTraceContaining("duplicate key value")
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Добавить Person с null полями. Проверить: DataIntegrityViolationException.")
    void AddPersonWithNullField_thenAssertException() {
        // Given

        Person nullNamePerson = new Person();
        nullNamePerson.setTitle("reader");
        nullNamePerson.setAge(111);
        nullNamePerson.setCountry("Russia");

        Person nullTitlePerson = new Person();
        nullTitlePerson.setFullName("Test Test");
        nullTitlePerson.setAge(111);
        nullTitlePerson.setCountry("Russia");

        Person nullAgePerson = new Person();
        nullAgePerson.setFullName("Test Test");
        nullAgePerson.setTitle("reader");
        nullAgePerson.setCountry("Russia");

        Person nullCountryPerson = new Person();
        nullCountryPerson.setFullName("Test Test");
        nullCountryPerson.setTitle("reader");
        nullCountryPerson.setAge(111);

        // When

        Throwable nullNameThrowable = catchThrowable(() ->
                userRepository.saveAndFlush(nullNamePerson)
        );

        Throwable nullTitleThrowable = catchThrowable(() ->
                userRepository.saveAndFlush(nullTitlePerson)
        );

        Throwable nullAgeThrowable = catchThrowable(() ->
                userRepository.saveAndFlush(nullAgePerson)
        );

        Throwable nullCountryThrowable = catchThrowable(() ->
                userRepository.saveAndFlush(nullCountryPerson)
        );

        // Then

        assertThat(nullNameThrowable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("not-null property references a null")
                .hasStackTraceContaining("Person.fullName")
                .hasCauseExactlyInstanceOf(PropertyValueException.class);

        assertThat(nullTitleThrowable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("not-null property references a null")
                .hasStackTraceContaining("Person.title")
                .hasCauseExactlyInstanceOf(PropertyValueException.class);

        assertThat(nullAgeThrowable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("not-null property references a null")
                .hasStackTraceContaining("Person.age")
                .hasCauseExactlyInstanceOf(PropertyValueException.class);

        assertThat(nullCountryThrowable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("not-null property references a null")
                .hasStackTraceContaining("Person.country")
                .hasCauseExactlyInstanceOf(PropertyValueException.class);
    }
}

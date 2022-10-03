package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
@DisplayName("Testing Book Repository.")
@Rollback
@Sql({"classpath:sql/1_clear_schema.sql",
        "classpath:sql/2_insert_book_data.sql",
        "classpath:sql/3_insert_person_data.sql"})
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @Test
    @DisplayName("Сохранить Person и Book. Проверить: новые поля, кол-во Book в БД, кол-во DML-вызовов.")
    void insertPersonAndBook_thenAssert_Fields_DmlCount_BookCount() {
        //Given

        Person person = new Person();
        person.setAge(89);
        person.setTitle("reader");
        person.setCountry("Russia");
        person.setFullName("Test Test");

        Book book = new Book();
        book.setTitle("test");
        book.setAuthor("Test Author");
        book.setPageCount(1000);
        book.setPersonId(1L);

        //When

        Person savedPerson = userRepository.save(person);
        Book result = bookRepository.save(book);
        long bookCount = bookRepository.count();

        //Then

        assertThat(result.getTitle()).isEqualTo("test");
        assertThat(result.getAuthor()).isEqualTo("Test Author");
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getPersonId()).isEqualTo(1L);

        assertThat(bookCount).isEqualTo(6);

        assertSelectCount(2);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName(
            "Получить Book, обновить поля, сохранить. Проверить: новые поля, кол-во Book в БД, кол-во DML-вызовов."
    )
    void updateBook_thenAssert_Fields_BookCount_DmlCount() {
        // Given - Books from 2_insert_book_data.sql

        Book book = bookRepository.findById(1001L).get();
        book.setTitle("updated title");
        book.setAuthor("updated author");
        book.setPageCount(999);

        // When

        Book result = bookRepository.save(book);
        long bookCount = bookRepository.count();

        // Then

        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getTitle()).isEqualTo("updated title");
        assertThat(result.getAuthor()).isEqualTo("updated author");
        assertThat(result.getPageCount()).isEqualTo(999);

        assertThat(bookCount).isEqualTo(5);

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(1);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Получить Book. Проверить: сохраненные поля, кол-во Book в БД, кол-во DML-вызовов")
    void getBook_thenAssert_Fields_DmlCount_BookCount() {
        // Given - Books from 2_insert_book_data.sql

        // When

        Book result = bookRepository.findById(1001L).get();
        long bookCount = bookRepository.count();

        // Then

        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getTitle()).isEqualTo("book1");
        assertThat(result.getAuthor()).isEqualTo("author1");
        assertThat(result.getPageCount()).isEqualTo(111);

        assertThat(bookCount).isEqualTo(5);

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Получить все Book. Проверить: кол-во полученных Book, кол-во DML-вызовов.")
    void getAllBook_thenAssert_BookCount_DmlCount() {
        // Given - Books from 2_insert_book_data.sql

        // When

        List<Book> bookList = bookRepository.findAll();

        // Then

        assertThat(bookList.size()).isEqualTo(5);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Получить все Book одного Person. Проверить: кол-во полученных Book, кол-во DML-вызовов.")
    void getBooksOfPerson_thenAssert_BookCount_DmlCount() {
        // Given - Books from 2_insert_book_data.sql

        // When

        List<Book> bookList = bookRepository.findBooksByPersonId(1001L);

        // Then

        assertThat(bookList.size()).isEqualTo(3);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @Test
    @DisplayName("Удалить 2-х Book из 5-ти. Проверить: кол-во Book в БД, кол-во DML-вызовов.")
    void delete2Books_thenAssert_BookCount_DmlCount() {
        // Given - Books from 2_insert_book_data.sql

        // When

        bookRepository.deleteById(1001L);
        bookRepository.deleteById(1002L);

        long bookCount = bookRepository.count();

        // Then

        assertThat(bookCount).isEqualTo(3);

        assertSelectCount(3);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(2);
    }

    @Test
    @DisplayName("Удалить все Book одного Person. Проверить: кол-во Book в БД, кол-во DML-вызовов.")
    void deleteBooksOfPerson_thenAssert_BookCount_DmlCount() {
        // Given - Books from 2_insert_book_data.sql

        // When

        bookRepository.deleteBooksByPersonId(1001L);

        long bookCount = bookRepository.count();

        // Then

        assertThat(bookCount).isEqualTo(2);

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(3);
    }

    @Test
    @DisplayName("Удалить Book с id = 1. Проверить: EmptyResultDataAccessException.")
    void DeleteNotExistingBook_thenAssertException() {

        // When

        Throwable throwable = catchThrowable(() -> bookRepository.deleteById(1L));

        // Then

        assertThat(throwable)
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessageStartingWith("No class")
                .hasMessageEndingWith("Book entity with id 1 exists!")
                .hasStackTraceContaining("Unknown Source")
                .hasNoCause();
    }

    @Test
    @DisplayName("Добавить Book с повторяющимся title. Проверить: DataIntegrityViolationException.")
    void AddBookWithRepeatedTitle_thenAssertException() {
        Book book = new Book();
        book.setTitle("book1");
        book.setAuthor("Test Author");
        book.setPageCount(1000);
        book.setPersonId(1L);

        // When

        Throwable throwable = catchThrowable(() -> {
            bookRepository.save(book);
            bookRepository.flush();
        });
        // Then

        assertThat(throwable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("could not execute statement")
                .hasStackTraceContaining("duplicate key value")
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Добавить Book с null полями. Проверить: DataIntegrityViolationException.")
    void AddBookWithNullField_thenAssertException() {
        Book nullTitleBook = new Book();
        nullTitleBook.setAuthor("Test Author");
        nullTitleBook.setPageCount(1000);
        nullTitleBook.setPersonId(1L);

        Book nullAuthorBook = new Book();
        nullAuthorBook.setTitle("Test Title 2");
        nullAuthorBook.setPageCount(1000);
        nullAuthorBook.setPersonId(1L);

        Book nullPageCountBook = new Book();
        nullPageCountBook.setTitle("Test Title 3");
        nullPageCountBook.setAuthor("Test Author");
        nullPageCountBook.setPersonId(1L);

        Book nullPersonIdBook = new Book();
        nullPersonIdBook.setTitle("Test Title 4");
        nullPersonIdBook.setAuthor("Test Author");
        nullPersonIdBook.setPageCount(1000);

        // When

        Throwable nullTitleThrowable = catchThrowable(() ->
            bookRepository.saveAndFlush(nullTitleBook)
        );

        Throwable nullAuthorThrowable = catchThrowable(() ->
            bookRepository.saveAndFlush(nullAuthorBook)
        );

        Throwable nullPageCountThrowable = catchThrowable(() ->
            bookRepository.saveAndFlush(nullPageCountBook)
        );

        Throwable nullPersonIdThrowable = catchThrowable(() ->
            bookRepository.saveAndFlush(nullPersonIdBook)
        );

        // Then

        assertThat(nullTitleThrowable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("not-null property references a null")
                .hasStackTraceContaining("Book.title")
                .hasCauseExactlyInstanceOf(PropertyValueException.class);

        assertThat(nullAuthorThrowable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("not-null property references a null")
                .hasStackTraceContaining("Book.author")
                .hasCauseExactlyInstanceOf(PropertyValueException.class);

        assertThat(nullPageCountThrowable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("not-null property references a null")
                .hasStackTraceContaining("Book.pageCount")
                .hasCauseExactlyInstanceOf(PropertyValueException.class);

        assertThat(nullPersonIdThrowable)
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageStartingWith("not-null property references a null")
                .hasStackTraceContaining("Book.personId")
                .hasCauseExactlyInstanceOf(PropertyValueException.class);
    }

    // example failed test
}

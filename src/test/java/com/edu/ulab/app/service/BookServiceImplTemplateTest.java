package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.WrongBookException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.RowToBookDtoMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.util.GeneratedKeyHolderFactory;
import com.edu.ulab.app.util.JdbcHelper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing Template book functionality.")
public class BookServiceImplTemplateTest {
    @InjectMocks
    BookServiceImplTemplate bookService;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    GeneratedKeyHolderFactory keyHolderFactory;

    BookDto createTestBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);
        bookDto.setUserId(1L);
        return bookDto;
    }

    @Test
    @DisplayName("Создание книги. Должно пройти успешно. 1 вызов jdbcTemplate.update().")
    void saveBook_Test() {
        //given

        BookDto bookDto = createTestBookDto();

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        keyHolder.getKeyList().add(Map.of("key", 1L));

        //when

        when(keyHolderFactory.newKeyHolder()).thenReturn(keyHolder);

        //then

        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals(1000, bookDtoResult.getPageCount());

        verify(jdbcTemplate, times(1))
                .update(any(PreparedStatementCreator.class), any(GeneratedKeyHolder.class));
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void updateBook_Test() {
        //given

        BookDto incomingBookDto = createTestBookDto();
        incomingBookDto.setId(1L);

        BookDto updatedBookDto = createTestBookDto();
        updatedBookDto.setId(1L);
        List<BookDto> bookDtoList = List.of(updatedBookDto);

        final String SELECT_SQL = "SELECT * FROM ULAB_EDU.BOOK WHERE ID = ?";

        //when

        when(jdbcTemplate.query(
                eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToBookDtoMapper.class))
        ).thenReturn(bookDtoList);

        //then

        BookDto bookDtoResult = bookService.updateBook(incomingBookDto, 1L);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals(1000, bookDtoResult.getPageCount());

        verify(jdbcTemplate, times(1))
                .update(any(PreparedStatementCreator.class));

        verify(jdbcTemplate, times(1))
                .query(eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToBookDtoMapper.class));

        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void getBook_Test() {
        //given

        BookDto bookDto = createTestBookDto();
        bookDto.setId(1L);
        List<BookDto> bookDtoList = List.of(bookDto);

        final String SELECT_SQL = "SELECT * FROM ULAB_EDU.BOOK WHERE ID = ?";

        //when

        when(jdbcTemplate.query(
                eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToBookDtoMapper.class))
        ).thenReturn(bookDtoList);

        //then

        BookDto bookDtoResult = bookService.getBookById(1L);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals(1000, bookDtoResult.getPageCount());

        verify(jdbcTemplate, times(1))
                .query(eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToBookDtoMapper.class));
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Получение всех книг юзера. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void getBooksByUserId_Test() {
        //given

        BookDto bookDto1 = createTestBookDto();
        bookDto1.setId(1L);

        BookDto bookDto2 = createTestBookDto();
        bookDto2.setId(2L);

        BookDto bookDto3 = createTestBookDto();
        bookDto3.setId(3L);

        List<BookDto> bookDtoList = List.of(bookDto1, bookDto2, bookDto3);

        final String SELECT_SQL = "SELECT * FROM ULAB_EDU.BOOK WHERE PERSON_ID = ?";

        //when

        when(jdbcTemplate.query(
                anyString(), any(PreparedStatementSetter.class), any(RowToBookDtoMapper.class))
        ).thenReturn(bookDtoList);

        //then

        List<BookDto> resultList = bookService.getBooksByUserId(1L);
        assertEquals(resultList, bookDtoList);

        verify(jdbcTemplate, times(1))
                .query(eq(SELECT_SQL), any(PreparedStatementSetter.class), any(RowToBookDtoMapper.class));

        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void deleteBook_Test() {
        //given

        //when

        //then
        bookService.deleteBookById(1L);
        verify(jdbcTemplate, times(1))
                .update(any(PreparedStatementCreator.class));
        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("Удаление всех книг юзера. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void deleteBooksByUserId_Test() {
        //given

        //given

        //then
        bookService.deleteBooksByUserId(1L);
        verify(jdbcTemplate, times(1))
                .update(any(PreparedStatementCreator.class));
        verifyNoMoreInteractions(jdbcTemplate);
    }


    @Test
    @DisplayName("Получение несуществующей книги. NotFoundException.")
    void getNotExistBook_Test() {
        //given

        //when

        Throwable throwable = catchThrowable(() ->
                bookService.getBookById(1L)
        );

        //then

        assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book with id 1 not found");
    }

    @Test
    @DisplayName("Сохранение null BookDto. WrongBookException")
    void saveNullBookDto_Test() {
        //given

        BookDto nullBookDto = null;

        //when

        Throwable throwable = catchThrowable(() ->
                bookService.createBook(nullBookDto)
        );

        //then

        assertThat(throwable)
                .isInstanceOf(WrongBookException.class)
                .hasMessage("Book is null");
    }
}
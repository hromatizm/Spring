package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.RowToBookDtoMapper;
import com.edu.ulab.app.service.BookService;
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
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        checkBookDtoForNull(bookDto);
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto, Long bookId) {
        checkBookIdForNull(bookId);
        checkBookDtoForNull(bookDto);

        final String UPDATE_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?  WHERE ID = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookId);
                    return ps;
                }
        );
        log.info("Book updated: {}", bookId);
        return getBookById(bookId);
    }

    @Override
    public BookDto getBookById(Long bookId) {
        checkBookIdForNull(bookId);

        final String SELECT_SQL = "SELECT * FROM BOOK WHERE ID = ?";
        List<BookDto> bookList = jdbcTemplate.query(
                SELECT_SQL, preparedStatement -> preparedStatement.setLong(1, bookId),
                new RowToBookDtoMapper());

        if (bookList.size() == 0) {
            throw new NotFoundException(String.format("Book with id %d not found", bookId));
        }
        log.info("Book found: {}", bookId);
        return bookList.get(0);
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        checkUserIdForNull(userId);

        final String SELECT_SQL = "SELECT * FROM BOOK WHERE USER_ID = ?";
        List<BookDto> bookList = jdbcTemplate.query(
                SELECT_SQL, preparedStatement -> preparedStatement.setLong(1, userId),
                new RowToBookDtoMapper());

        if (bookList.size() == 0) {
            throw new NotFoundException(String.format("Books of the User with id %d not found", userId));
        }
        log.info("Books found: {}", bookList);
        return bookList;
    }

    @Override
    public void deleteBookById(Long bookId) {
        checkBookIdForNull(bookId);

        final String DELETE_SQL = "DELETE FROM BOOK WHERE ID = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
                    ps.setLong(1, bookId);
                    return ps;
                }
        );
        log.info("Book deleted: {}", bookId);
    }

    @Override
    public void deleteBooksByUserId(Long userId) {
        checkUserIdForNull(userId);

        final String DELETE_SQL = "DELETE FROM BOOK WHERE USER_ID = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
                    ps.setLong(1, userId);
                    return ps;
                }
        );
        log.info("Books deleted by user id: {}", userId);
    }
}

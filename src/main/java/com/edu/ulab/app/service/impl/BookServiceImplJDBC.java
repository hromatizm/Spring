package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.RowToBookDtoMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.util.JdbcHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
public class BookServiceImplJDBC implements BookService {

    static {
        JdbcHelper.activateDriver();
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        checkBookDtoForNull(bookDto);

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String INSERT_SQL =
                    "INSERT INTO ULAB_EDU.BOOK(ID, TITLE, AUTHOR, PAGE_COUNT, PERSON_ID) " +
                            "VALUES (nextval('sequence'),?,?,?,?)";
            ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, bookDto.getTitle());
            ps.setString(2, bookDto.getAuthor());
            ps.setLong(3, bookDto.getPageCount());
            ps.setLong(4, bookDto.getUserId());
            ps.executeUpdate();

            resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                Long bookId = resultSet.getLong(1);
                bookDto.setId(bookId);
                log.info("Book id set: {}", bookId);
            }
        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(resultSet, ps, connection);
        }
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto, Long bookId) {
        checkBookIdForNull(bookId);
        checkBookDtoForNull(bookDto);

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String UPDATE_SQL = "UPDATE ULAB_EDU.BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?  WHERE ID = ?";
            ps = connection.prepareStatement(UPDATE_SQL);
            ps.setString(1, bookDto.getTitle());
            ps.setString(2, bookDto.getAuthor());
            ps.setLong(3, bookDto.getPageCount());
            ps.setLong(4, bookId);
            ps.executeUpdate();
            log.info("Book updated: {}", bookId);

        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(ps, connection);
        }
        return getBookById(bookId);
    }

    /**
     * In order to avoid overhead, gets the data from the row using the column number.
     * Because using a column name might add overhead: need to get column metadata info.
     */
    @Override
    public BookDto getBookById(Long bookId) {
        checkBookIdForNull(bookId);

        BookDto bookDto = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String SELECT_SQL =
                    "SELECT PERSON_ID, TITLE, AUTHOR, PAGE_COUNT FROM ULAB_EDU.BOOK WHERE ID = ?";
            ps = connection.prepareStatement(SELECT_SQL);
            ps.setLong(1, bookId);

            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                bookDto = new BookDto();
                bookDto.setId(bookId);
                bookDto.setUserId(resultSet.getLong(1));
                bookDto.setTitle(resultSet.getString(2));
                bookDto.setAuthor(resultSet.getString(3));
                bookDto.setPageCount(resultSet.getLong(4));
                log.info("Row converted to BookDto: {}", bookDto);
            } else {
                throw new NotFoundException(String.format("Book with id %d not found", bookId));
            }
        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(resultSet, ps, connection);
        }
        log.info("Book found: {}", bookId);
        return bookDto;
    }

    /**
     * In order to avoid overhead, gets the data from the row using the column number.
     * Because using a column name might add overhead: need to get column metadata info.
     */
    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        checkUserIdForNull(userId);

        List<BookDto> bookList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String SELECT_SQL =
                    "SELECT ID, TITLE, AUTHOR, PAGE_COUNT FROM ULAB_EDU.BOOK WHERE PERSON_ID = ?";
            ps = connection.prepareStatement(SELECT_SQL);
            ps.setLong(1, userId);

            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                BookDto bookDto = new BookDto();
                bookDto.setId(resultSet.getLong(1));
                bookDto.setTitle(resultSet.getString(2));
                bookDto.setAuthor(resultSet.getString(3));
                bookDto.setPageCount(resultSet.getLong(4));
                bookDto.setUserId(userId);

                log.info("Row converted to BookDto: {}", bookDto);
                bookList.add(bookDto);
            }
        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(resultSet, ps, connection);
        }

        if (bookList.size() == 0) {
            throw new NotFoundException(String.format("Books of the User with id %d not found", userId));
        }
        log.info("Books found: {}", bookList);
        return bookList;
    }

    @Override
    public void deleteBookById(Long bookId) {
        checkBookIdForNull(bookId);

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String DELETE_SQL = "DELETE FROM ULAB_EDU.BOOK WHERE ID = ?";
            ps = connection.prepareStatement(DELETE_SQL);
            ps.setLong(1, bookId);
            ps.executeUpdate();
            log.info("Book deleted: {}", bookId);

        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(ps, connection);
        }
    }

    @Override
    public void deleteBooksByUserId(Long userId) {
        checkUserIdForNull(userId);

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = JdbcHelper.activateConnection();

            final String DELETE_SQL = "DELETE FROM ULAB_EDU.BOOK WHERE PERSON_ID = ?";
            ps = connection.prepareStatement(DELETE_SQL);
            ps.setLong(1, userId);
            ps.executeUpdate();
            log.info("Books deleted by user id: {}", userId);

        } catch (SQLException exc) {
            JdbcHelper.handleSqlException(exc);
        } finally {
            JdbcHelper.closeAll(ps, connection);
        }
    }
}

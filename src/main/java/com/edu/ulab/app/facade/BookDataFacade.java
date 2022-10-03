package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplJDBC;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.BookResponse;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class BookDataFacade {
    private final BookService bookService;
    private final BookMapper bookMapper;

    public BookDataFacade(BookServiceImplTemplate bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    public BookResponse createBook(BookRequest request) {
        log.info("Got book request: {}", request);
        BookDto bookDto = bookFromRequest(request);
        BookDto createdBook = bookService.createBook(bookDto);
        log.info("Created book: {}", createdBook);
        return buildBookResponse(createdBook);
    }

    public BookResponse getBookById(Long bookId) {
        log.info("Got book request: {}", bookId);
        BookDto bookDto = bookService.getBookById(bookId);
        log.info("Book got: {}", bookDto);
        return buildBookResponse(bookDto);
    }

    public BookResponse updateBook(BookRequest request, Long id) {
        log.info("Got book update request: {}", request);
        BookDto bookDto = bookFromRequest(request);
        BookDto updatedBook = bookService.updateBook(bookDto, id);
        log.info("Updated book: {}", updatedBook);
        return buildBookResponse(updatedBook);
    }

    public void deleteBook(Long bookId) {
        log.info("Got 'delete book' request: {}", bookId);
        BookDto deletedBook = bookService.getBookById(bookId);
        bookService.deleteBookById(bookId);
        log.info("Deleted book: {}", deletedBook);
    }

    BookDto bookFromRequest(BookRequest request) {
        BookDto bookDto = bookMapper.bookRequestToBookDto(request);
        log.info("Mapped book request: {}", bookDto);
        return bookDto;
    }

    BookResponse buildBookResponse(BookDto book) {
        BookResponse response = BookResponse.builder()
                .id(book.getId())
                .userId(book.getUserId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .pageCount(book.getPageCount())
                .build();
        log.info("Book response built: {}", response);

        return response;
    }
}

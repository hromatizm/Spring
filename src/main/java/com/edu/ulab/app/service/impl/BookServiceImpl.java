package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * ORM implementation.
 * Uses default implementations of checking methods from BookService interface.
 */

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        checkBookDtoForNull(bookDto);
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book (dto -> entity): {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    /**
     * Mapper is used for entity updating.
     */
    @Override
    public BookDto updateBook(BookDto bookDto, Long bookId) {
        checkBookIdForNull(bookId);
        checkBookDtoForNull(bookDto);

        Book incomingBook = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book (dto -> entity): {}", incomingBook);

        Book currentBook = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Book with id %d not found", bookId)));
        log.info("Current book found for update: {}", currentBook);

        Book updatedBook = bookMapper.updateBook(incomingBook, currentBook);
        log.info("Updated book: {}", updatedBook);
        bookRepository.save(updatedBook);

        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long bookId) {
        checkBookIdForNull(bookId);

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Book with id %d not found", bookId)));
        log.info("Found book: {}", book);

        return bookMapper.bookToBookDto(book);
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        checkUserIdForNull(userId);

        List<BookDto> bookList = bookRepository
                .findBooksByUserId(userId)
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookToBookDto)
                .peek(book -> log.info("Mapped book (entity -> dto): {}", book))
                .toList();
        if (bookList.size() == 0) {
            throw new NotFoundException(String.format("Books of the User with id %d not found", userId));
        }
        return bookList;
    }

    @Override
    public void deleteBookById(Long bookId) {
        checkBookIdForNull(bookId);
        BookDto bookToDelete = getBookById(bookId);
        bookRepository.deleteById(bookId);
        log.info("Book deleted: {}", bookToDelete);
    }

    @Override
    public void deleteBooksByUserId(Long userId) {
        checkUserIdForNull(userId);
        List<BookDto> bookList = getBooksByUserId(userId);
        bookRepository.deleteBooksByUserId(userId);
        log.info("Books deleted: {}", bookList);
    }
}

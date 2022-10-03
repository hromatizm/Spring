package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.WrongBookException;
import com.edu.ulab.app.exception.WrongUserException;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto, Long id);

    BookDto getBookById(Long id);

    List<BookDto> getBooksByUserId(Long userId);

    void deleteBookById(Long id);

    void deleteBooksByUserId(Long id);

    default void checkBookIdForNull(Long id) {
        if (id == null) {
            throw new WrongBookException("Book id is null");
        }
    }
    default void checkBookDtoForNull(BookDto bookDto) {
        if (bookDto == null) {
            throw new WrongBookException("Book is null");
        }
    }

    default void checkUserIdForNull(Long id) {
        if (id == null) {
            throw new WrongUserException("User id is null");
        }
    }
}

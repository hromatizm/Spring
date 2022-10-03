package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.service.impl.*;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImplTemplate userService,
                          BookServiceImplTemplate bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest request) {
        log.info("Got user book create request: {}", request);

        UserDto userDto = userFromRequest(request);
        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = createBooksFromRequest(request, createdUser.getId());
        log.info("Collected book ids: {}", bookIdList);

        return buildUserBookResponse(createdUser, bookIdList);
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest request, Long userId) {
        log.info("Got 'user with books' update request: {}", request);

        UserDto userDto = userFromRequest(request);
        UserDto updatedUser = userService.updateUser(userDto, userId);
        log.info("Updated user: {}", updatedUser);

        bookService.deleteBooksByUserId(userId);
        createBooksFromRequest(request, userId);
        List<Long> bookIdList = getBooksIdByUser(userId);

        return buildUserBookResponse(updatedUser, bookIdList);
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got 'user with books' request: {}", userId);

        UserDto userDto = userService.getUserById(userId);
        List<Long> bookIdList = getBooksIdByUser(userId);

        return buildUserBookResponse(userDto, bookIdList);
    }

    public void deleteUserWithBooks(Long userId) {
        log.info("Got 'delete user with books' request: {}", userId);

        UserDto deletedUser = userService.getUserById(userId);
        List<Long> bookIdList = getBooksIdByUser(userId);

        userService.deleteUserById(userId);
        log.info("Deleted user: {}", deletedUser);

        bookIdList.forEach(deletedBook -> log.info("Deleted book: {}", deletedBook));
    }

    UserDto userFromRequest(UserBookRequest request) {
        UserDto userDto = userMapper.userRequestToUserDto(request.getUserRequest());
        log.info("Mapped user request: {}", userDto);
        return userDto;
    }

    List<Long> createBooksFromRequest(UserBookRequest request, Long userId) {
        request.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .forEach(book -> {
                    book.setUserId(userId);
                    log.info("User id set for book: {}", book);
                    bookService.createBook(book);
                    log.info("Created book: {}", book);
                });

        List<Long> bookIdList = getBooksIdByUser(userId);
        log.info("Created book list: {}", bookIdList);
        return bookIdList;
    }

    List<Long> getBooksIdByUser(Long userId) {
        return bookService.getBooksByUserId(userId)
                .stream()
                .filter(Objects::nonNull)
                .map(BookDto::getId)
                .toList();
    }

    UserBookResponse buildUserBookResponse(UserDto user, List<Long> books) {
        return UserBookResponse.builder()
                .userId(user.getId())
                .booksIdList(books)
                .build();
    }
}

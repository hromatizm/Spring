package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.WrongBookException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing JPA book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    Person createTestPerson() {
        Person person = new Person();
        person.setFullName("test name");
        person.setTitle("test title");
        person.setAge(11);
        return person;
    }

    BookDto createTestBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);
        bookDto.setUserId(1L);
        return bookDto;
    }

    Book createTestBook() {
        Book book = new Book();
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPageCount(1000);
        book.setPersonId(1L);
        return book;
    }

    @Test
    @DisplayName("Создание книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void saveBook_Test() {
        //given

        Person person = createTestPerson();
        person.setId(1L);

        BookDto bookDto = createTestBookDto();

        BookDto result = createTestBookDto();
        result.setId(1L);

        Book book = createTestBook();

        Book savedBook = createTestBook();
        savedBook.setId(1L);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then

        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals(1000, bookDtoResult.getPageCount());

        verify(bookMapper, times(1)).bookDtoToBook(bookDto);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).bookToBookDto(savedBook);

        verifyNoMoreInteractions(bookMapper);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void updateBook_Test() {
        //given

        BookDto incomingBookDto = createTestBookDto();
        incomingBookDto.setId(1L);

        Book incomingBook = createTestBook();
        incomingBook.setId(1L);

        Book currentBook = new Book();
        currentBook.setId(1L);
        currentBook.setTitle("current title");
        currentBook.setAuthor("current author");
        currentBook.setPageCount(2000);
        currentBook.setPersonId(1L);

        Book updatedBook = createTestBook();
        updatedBook.setId(1L);

        BookDto updatedBookDto = createTestBookDto();
        updatedBookDto.setId(1L);

        //when

        when(bookMapper.bookDtoToBook(incomingBookDto)).thenReturn(incomingBook);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(currentBook));
        when(bookMapper.updateBook(incomingBook, currentBook)).thenReturn(updatedBook);
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(updatedBookDto);

        //then

        BookDto bookDtoResult = bookService.updateBook(incomingBookDto, 1L);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals(1000, bookDtoResult.getPageCount());

        verify(bookMapper, times(1)).bookDtoToBook(incomingBookDto);
        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, times(1)).updateBook(incomingBook, currentBook);
        verify(bookRepository, times(1)).save(updatedBook);
        verify(bookMapper, times(1)).bookToBookDto(updatedBook);

        verifyNoMoreInteractions(bookMapper);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void getBook_Test() {
        //given

        BookDto bookDto = createTestBookDto();
        bookDto.setId(1L);

        Book book = createTestBook();
        book.setId(1L);

        //when

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        //then

        BookDto bookDtoResult = bookService.getBookById(1L);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals(1000, bookDtoResult.getPageCount());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, times(1)).bookToBookDto(book);

        verifyNoMoreInteractions(bookMapper);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Получение всех книг юзера. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void getBooksByUserId_Test() {
        //given

        Book book1 = createTestBook();
        book1.setId(1L);

        Book book2 = createTestBook();
        book2.setId(2L);

        Book book3 = createTestBook();
        book3.setId(3L);

        List<Book> bookList = List.of(book1, book2, book3);

        BookDto bookDto1 = createTestBookDto();
        bookDto1.setId(1L);

        BookDto bookDto2 = createTestBookDto();
        bookDto2.setId(2L);

        BookDto bookDto3 = createTestBookDto();
        bookDto3.setId(3L);

        List<BookDto> bookDtoList = List.of(bookDto1, bookDto2, bookDto3);

        //when

        when(bookRepository.findBooksByPersonId(1L)).thenReturn(bookList);
        when(bookMapper.bookToBookDto(book1)).thenReturn(bookDto1);
        when(bookMapper.bookToBookDto(book2)).thenReturn(bookDto2);
        when(bookMapper.bookToBookDto(book3)).thenReturn(bookDto3);

        //then

        List<BookDto> resultList = bookService.getBooksByUserId(1L);
        assertEquals(resultList, bookDtoList);

        verify(bookRepository, times(1)).findBooksByPersonId(1L);
        verify(bookMapper, times(1)).bookToBookDto(book1);
        verify(bookMapper, times(1)).bookToBookDto(book2);
        verify(bookMapper, times(1)).bookToBookDto(book3);

        verifyNoMoreInteractions(bookMapper);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void deleteBook_Test() {
        //given

        BookDto bookDto = createTestBookDto();
        bookDto.setId(1L);

        Book book = createTestBook();
        book.setId(1L);

        //when

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        //then
        bookService.deleteBookById(1L);
        verify(bookRepository, times(1)).deleteById(1L);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, times(1)).bookToBookDto(book);

        verifyNoMoreInteractions(bookMapper);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Удаление всех книг юзера. Должно пройти успешно. 1 вызов каждого вложенного метода.")
    void deleteBooksByUserId_Test() {
        //given

        Book book1 = createTestBook();
        book1.setId(1L);

        Book book2 = createTestBook();
        book2.setId(2L);

        Book book3 = createTestBook();
        book3.setId(3L);

        List<Book> bookList = List.of(book1, book2, book3);

        BookDto bookDto1 = createTestBookDto();
        bookDto1.setId(1L);

        BookDto bookDto2 = createTestBookDto();
        bookDto2.setId(2L);

        BookDto bookDto3 = createTestBookDto();
        bookDto3.setId(3L);

        //when

        when(bookRepository.findBooksByPersonId(1L)).thenReturn(bookList);
        when(bookMapper.bookToBookDto(book1)).thenReturn(bookDto1);
        when(bookMapper.bookToBookDto(book2)).thenReturn(bookDto2);
        when(bookMapper.bookToBookDto(book3)).thenReturn(bookDto3);

        //then

        bookService.deleteBooksByUserId(1L);

        verify(bookRepository, times(1)).findBooksByPersonId(1L);
        verify(bookMapper, times(1)).bookToBookDto(book1);
        verify(bookMapper, times(1)).bookToBookDto(book2);
        verify(bookMapper, times(1)).bookToBookDto(book3);

        verify(bookRepository, times(1)).deleteBooksByPersonId(1L);

        verifyNoMoreInteractions(bookMapper);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Получение несуществующей книги. NotFoundException.")
    void getNotExistBook_Test() {
        //given

        //when

        when(bookRepository.findById(1L)).thenThrow(
                new NotFoundException("Book with id 1 not found")
        );

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

    @Test
    @DisplayName("Сохранение null Book. InvalidDataAccessApiUsageException")
    void createNullBook_Test() {
        //given

        BookDto bookDto = createTestBookDto();
        bookDto.setId(1L);

        Book nullBook = null;

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(nullBook);
        doThrow(new InvalidDataAccessApiUsageException("Entity must not be null."))
                .when(bookRepository).save(nullBook);

        //then

        assertThatThrownBy(() -> bookService.createBook(bookDto))
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessage("Entity must not be null.");
    }
}

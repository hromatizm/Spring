package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.web.request.BookRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto bookRequestToBookDto(BookRequest bookRequest);

    BookRequest bookDtoToBookRequest(BookDto bookDto);

    @Mapping(source = "userId", target = "personId")
    Book bookDtoToBook(BookDto bookDto);

    @Mapping(source = "personId", target = "userId")
    BookDto bookToBookDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "personId", ignore = true)
    Book updateBook(Book update, @MappingTarget Book target);
}

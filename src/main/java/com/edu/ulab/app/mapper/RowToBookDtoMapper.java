package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RowToBookDtoMapper implements RowMapper<BookDto> {
    @Override
    public BookDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        BookDto bookDto = new BookDto();
        bookDto.setId(rs.getLong("id"));
        bookDto.setUserId(rs.getLong("person_id"));
        bookDto.setTitle(rs.getString("title"));
        bookDto.setAuthor(rs.getString("author"));
        bookDto.setPageCount(rs.getLong("page_count"));
        return bookDto;
    }
}

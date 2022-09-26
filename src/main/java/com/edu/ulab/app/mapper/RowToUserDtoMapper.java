package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.UserDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RowToUserDtoMapper implements RowMapper<UserDto> {
    @Override
    public UserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserDto userDto = new UserDto();
        userDto.setId(rs.getLong("id"));
        userDto.setFullName(rs.getString("full_name"));
        userDto.setTitle(rs.getString("title"));
        userDto.setAge(rs.getInt("age"));
        return userDto;
    }
}

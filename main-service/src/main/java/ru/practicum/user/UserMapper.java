package ru.practicum.user;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(UserShortDto userShortDto);

    UserDto toUserDto(User user);
}

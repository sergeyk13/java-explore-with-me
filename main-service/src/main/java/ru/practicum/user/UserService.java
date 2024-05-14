package ru.practicum.user;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

import java.util.List;

public interface UserService {
    UserDto postUser(UserShortDto userShortDto);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(long id);
}

package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;
import ru.practicum.util.MapperPageToList;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto postUser(UserShortDto userShortDto) {
        log.info("Save user: name:{}, email:{}", userShortDto.getName(), userShortDto.getEmail());
        return UserMapper.INSTANCE.toUserDto(userRepository.save(UserMapper.INSTANCE.toUser(userShortDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        Page<User> usersPage = userRepository.findAllByIdIn(ids, page);
        log.info("Get users: {}", usersPage);
        return MapperPageToList.mapPageToList(usersPage, from, size, UserMapper.INSTANCE::toUserDto);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        log.info("Remove user: {}", userId);
        userRepository.deleteById(userId);
    }
}

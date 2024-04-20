package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto postUser(UserShortDto userShortDto) {
        log.info("Save user: name:{}, email:{}", userShortDto.getName(), userShortDto.getEmail());
        return UserMapper.INSTANCE.toUserDto(userRepository.save(UserMapper.INSTANCE.toUser(userShortDto)));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from, size, sortById);
        Page<User> usersPage = userRepository.findAllByIdIn(ids, page);
        log.info("Get users: {}", usersPage);
        return getUserDtos(usersPage, from, size);
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Remove user: {}", userId);
        userRepository.deleteById(userId);
    }

    private List<UserDto> getUserDtos(Page<User> usersPage, int from, int size) {
        int elementOnPage = from % size;
        return usersPage.stream()
                .skip(elementOnPage)
                .limit(size)
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }
}

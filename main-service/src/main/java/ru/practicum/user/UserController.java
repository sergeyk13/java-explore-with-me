package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@Valid @RequestBody final UserShortDto userShortDto) {
        log.info("Post user: {}", userShortDto);
        return userService.postUser(userShortDto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam List<Long> ids,
                                  @RequestParam(required = false, name = "from", defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(required = false, name = "size", defaultValue = "10") @PositiveOrZero int size) {
        log.info("Get users: {}", ids);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Delete user: {}", userId);
        userService.deleteUser(userId);
    }

}

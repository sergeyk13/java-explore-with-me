package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto createRequest(@PathVariable("userId") long userId,
                                                 @RequestParam(name = "eventId") long eventId) {
        log.info("Request post for user {} with event {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequest(@PathVariable("userId") long userId) {
        log.info("Request get request for user {}", userId);
        return requestService.getRequest(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable long requestId, @PathVariable("userId") long userId) {
        log.info("Request cancel request for user {} with request {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}

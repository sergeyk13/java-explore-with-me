package ru.practicum.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") long userId,
                                                 @RequestParam(name = "eventId") long eventId) {
        log.info("Request post from user: {} for event: {}", userId, eventId);
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
        return requestService.cancelRequest(requestId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable long userId, @PathVariable("eventId") long eventId) {
        log.info("Request get List requests for user {} with event {}", userId, eventId);
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult patchStatusRequests(@PathVariable long userId, @PathVariable long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        log.info("Request update status for user {} with event {}", userId, eventId);
        return requestService.patchStatusRequests(userId, eventId, updateRequest);
    }

}

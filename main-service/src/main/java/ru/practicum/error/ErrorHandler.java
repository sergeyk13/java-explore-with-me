package ru.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.model.ConflictException;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.error.model.ValidationException;

import javax.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private List<ApiError> errors = new ArrayList<>();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiError> handleValidationException(final ValidationException e) {
        log.error(e.getMessage());
        return buildApiError(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiError> handleNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return buildApiError(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiError> handleDefaultValidation(final MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String fieldName = fieldError.getField();
        String errorMessage = fieldError.getDefaultMessage();
        log.error(errorMessage);
        return buildApiError(HttpStatus.BAD_REQUEST, e);
    }


    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected List<ApiError> handleConversionFailed(ConversionFailedException e) {
        log.error(e.getMessage());
        return buildApiError(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ApiError> handleNotFoundEntity(final EntityNotFoundException e) {
        log.error(cleanErrorMessage(e.getMessage()));
        return buildApiError(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ApiError> handleConflict(final ConflictException e) {
        log.error(e.getMessage(), e);
        return buildApiError(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ApiError> handleConstraintViolationException(final ConstraintViolationException e) {
        log.error(e.getMessage());
        return buildApiError(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ApiError> handleConflict(final AccessDeniedException e) {
        log.error(e.getMessage(), e);
        return buildApiError(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<ApiError> handleNullPointer(final NullPointerException e) {
        log.error(e.getMessage());
        return buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({DataIntegrityViolationException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ApiError> handleForeignKeyViolation(DataIntegrityViolationException e) {
        log.error(cleanErrorMessage(e.getMessage()));
        return buildApiError(HttpStatus.CONFLICT, e);
    }

    private List<ApiError> buildApiError(HttpStatus status, Throwable exception) {
        String cause = (exception.getCause() != null) ? exception.getCause().toString() : "Unknown cause";
        errors.add(new ApiError(
                exception.getMessage(),
                cause,
                status.toString(),
                LocalDateTime.now().toString()));
        return errors;
    }

    private String cleanErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            int lastDotIndex = errorMessage.lastIndexOf(".");
            if (lastDotIndex != -1) {
                errorMessage = "Not found " + errorMessage.substring(lastDotIndex + 1);
            }
            return errorMessage;
        }
        return errorMessage;
    }
}

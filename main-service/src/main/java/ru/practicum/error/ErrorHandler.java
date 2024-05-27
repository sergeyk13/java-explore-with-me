package ru.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.error.model.ConflictException;
import ru.practicum.error.model.NotFoundException;
import ru.practicum.error.model.ValidationException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ru.practicum.util.Constant.FORMATTER;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MissingServletRequestParameterException.class, ValidationException.class,
            ConversionFailedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiError> handleValidationException(Exception e) {
        List<ApiError> errors = new ArrayList<>();
        log.error(e.getMessage());
        errors.add(buildApiError(HttpStatus.BAD_REQUEST, e));
        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public List<ApiError> handleForbiddenException(Exception e) {
        List<ApiError> errors = new ArrayList<>();
        log.error(e.getMessage());
        errors.add(buildApiError(HttpStatus.FORBIDDEN, e));
        return errors;
    }

    @ExceptionHandler({NotFoundException.class, EntityNotFoundException.class,})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public List<ApiError> handleNotFoundException(Exception e) {
        List<ApiError> errors = new ArrayList<>();
        log.error(e.getMessage());
        errors.add(buildApiError(HttpStatus.NOT_FOUND, e));
        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiError> handleConstraintViolationException(javax.validation.ConstraintViolationException e) {
        List<ApiError> errors = new ArrayList<>();
        String errorMessage = e.getMessage();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMessage = violation.getPropertyPath().toString() + ": " + violation.getMessage();
            log.error(errorMessage);
            ValidationException validationException = new ValidationException(errorMessage);
            validationException.setCause(e.getCause());
            errors.add(buildApiError(HttpStatus.BAD_REQUEST, validationException));
        }
        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ApiError> handleDefaultValidation(MethodArgumentNotValidException e) {
        List<ApiError> errors = new ArrayList<>();
        FieldError fieldError = e.getBindingResult().getFieldError();
        String fieldName = fieldError.getField();
        String errorMessage = fieldName + ": " + fieldError.getDefaultMessage();
        log.error(errorMessage);
        ValidationException validationException = new ValidationException(errorMessage);
        validationException.setCause(e.getCause());
        errors.add(buildApiError(HttpStatus.BAD_REQUEST, validationException));
        return errors;
    }

    @ExceptionHandler({ConflictException.class, ConstraintViolationException.class, AccessDeniedException.class,
            DataIntegrityViolationException.class, DataAccessException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public List<ApiError> handleConflict(final Exception e) {
        List<ApiError> errors = new ArrayList<>();
        log.error(e.getMessage());
        errors.add(buildApiError(HttpStatus.CONFLICT, e));
        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<ApiError> handleNullPointer(NullPointerException e) {
        List<ApiError> errors = new ArrayList<>();
        log.error(e.getMessage());
        errors.add(buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, e));
        return errors;
    }

    private ApiError buildApiError(HttpStatus status, Throwable exception) {
        String cause;
        if (exception.getCause() == null) {
            switch (status) {
                case BAD_REQUEST:
                    cause = "Incorrectly made request.";
                    break;
                case CONFLICT:
                    cause = "Integrity constraint has been violated.";
                    break;
                case NOT_FOUND:
                    cause = "The required object was not found.";
                    break;
                case FORBIDDEN:
                    cause = "For the requested operation the conditions are not met.";
                    break;
                default:
                    cause = "Unknown cause";
                    break;
            }
        } else {
            cause = exception.getCause().toString();
        }
        return (new ApiError(
                exception.getMessage(),
                cause,
                status.toString(),
                LocalDateTime.now().format(FORMATTER)));
    }

    private String cleanErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            int lastDotIndex = errorMessage.lastIndexOf(".");
            if (lastDotIndex != -1) {
                errorMessage = "Not found " + errorMessage.substring(lastDotIndex + 1);
            }
            return errorMessage;
        }
        return null;
    }
}

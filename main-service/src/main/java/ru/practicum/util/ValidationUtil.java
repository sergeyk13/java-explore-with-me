package ru.practicum.util;

import javax.validation.*;
import java.util.Set;


public class ValidationUtil {
    public static <T> void checkValidation(T object) throws ConstraintViolationException {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

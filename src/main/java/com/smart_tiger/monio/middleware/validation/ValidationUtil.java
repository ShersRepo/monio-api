package com.smart_tiger.monio.middleware.validation;

import com.smart_tiger.monio.utils.StringUtils;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static ValidationFailure extractFieldErrorMessage(FieldError fieldError) {
        Set<String> errorCodes = Arrays.stream(fieldError.getCodes())
                .filter(Objects::nonNull)
                .filter(StringUtils::isNotNullOrEmpty)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (errorCodes.contains("notnull")) {
            return ValidationFailure.NOT_NULL;
        } else if (errorCodes.contains("notblank")) {
            return ValidationFailure.NOT_BLANK;
        } else if (errorCodes.contains("size")) {
            return ValidationFailure.INVALID_LENGTH;
        } else {
            return ValidationFailure.SOMETHING_WENT_WRONG;
        }
    }


}
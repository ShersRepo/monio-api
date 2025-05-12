package com.smart_tiger.monio.utils;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public interface EnumWithConverter<T extends Enum<T> &  WithDtoValue> {

    @JsonCreator
    static <T extends Enum<T> & WithDtoValue> T fromString(Class<T> enumClass, String value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        } else {
            return Arrays.stream(enumClass.getEnumConstants())
                    .filter(e -> e.name().equals(value) || e.getDtoValue().equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid enum value: " + value));
        }
    }
}


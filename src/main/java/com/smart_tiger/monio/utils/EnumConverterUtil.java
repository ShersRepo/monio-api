package com.smart_tiger.monio.utils;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

/**
 * Utility interface for working with enumerations that implement the {@link WithDtoValue} interface.
 * Provides methods for converting string representations to corresponding enum constants.
 * <p>
 * Enum classes implementing this interface can support both toString-based and custom value-based
 * serialization/deserialization using the {@link WithDtoValue#getDtoValue()} method.
 * </p>
 * @param <T> The type of enumeration that implements both {@link Enum} and {@link WithDtoValue}.
 */
public final class EnumConverterUtil<T extends Enum<T> & WithDtoValue> {

    private EnumConverterUtil() {
    }

    /**
     * Converts a string representation to the corresponding enum constant from the provided enum class.
     * The method attempts to match the string value against both the enum constant name and the custom DTO value
     * returned by the getDtoValue method of the WithDtoValue interface.
     *
     * @param enumClass the class of the enumeration implementing both*/
    @JsonCreator
    public static <T extends Enum<T> & WithDtoValue> T fromString(Class<T> enumClass, String value)
            throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.toString().equals(value) || e.getDtoValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid enum value: " + value));
    }

}


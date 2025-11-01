package com.smart_tiger.monio.utils;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("UnitTest")
class EnumConverterUtilTest {

    @Test
    void EnumWithConverter_fromString_returns_enum() {
        String stringValue = "Mango";

        TestEnumWithDtoValue result = assertDoesNotThrow(() -> EnumConverterUtil.fromString(TestEnumWithDtoValue.class, stringValue));

        assertThat(result)
                .isNotNull()
                .isEqualTo(TestEnumWithDtoValue.MANGO);
    }

    @Test
    void EnumWithConverter_fromString_throws_error_when_null() {
        String stringValue = null;

        assertThrows(
                IllegalArgumentException.class,
                () -> EnumConverterUtil.fromString(TestEnumWithDtoValue.class, stringValue),
                "Value cannot be null"
        );
    }

    @Test
    void EnumWithConverter_fromString_throws_error_when_invalid_value() {
        String stringValue = "Not Mango or Banana";

        assertThrows(
                IllegalArgumentException.class,
                () -> EnumConverterUtil.fromString(TestEnumWithDtoValue.class, stringValue),
                "Invalid enum value: Not Mango or Banana"
        );
    }

    @Test
    void EnumWithConverter_fromString_throws_error_when_invalid_value_case_sensitive() {
        String stringValue = "mango";

        assertThrows(
                IllegalArgumentException.class,
                () -> EnumConverterUtil.fromString(TestEnumWithDtoValue.class, stringValue),
                "Invalid enum value: mango"
        );
    }

    enum TestEnumWithDtoValue implements WithDtoValue {
        BANANA("Banana"),
        MANGO("Mango");

        @Getter
        private final String value;

        TestEnumWithDtoValue(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String getDtoValue() {
            return toString();
        }

        @Override
        public String toString() {
            return value;
        }
    }

}

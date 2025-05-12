package com.smart_tiger.monio.modules.ledger.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.smart_tiger.monio.utils.EnumWithConverter;
import com.smart_tiger.monio.utils.WithDtoValue;

public enum Currency implements WithDtoValue {
    GBP("GBP");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

    @JsonValue
    public String getDtoValue() {
        return value;
    }

    @JsonCreator
    public static Currency fromString(String value) throws IllegalArgumentException {
        return EnumWithConverter.fromString(Currency.class, value);
    }


}

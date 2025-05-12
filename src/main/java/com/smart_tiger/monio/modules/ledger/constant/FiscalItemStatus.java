package com.smart_tiger.monio.modules.ledger.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.smart_tiger.monio.utils.EnumWithConverter;
import com.smart_tiger.monio.utils.WithDtoValue;

public enum FiscalItemStatus implements WithDtoValue {
    ACTIVE("Active"),
    DRAFT("Draft"),
    REMOVED("Removed");

    private final String status;

    FiscalItemStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getDtoValue() {
        return status;
    }

    @JsonCreator
    public static FiscalItemStatus fromString(String value) throws IllegalArgumentException {
        return EnumWithConverter.fromString(FiscalItemStatus.class, value);
    }

}

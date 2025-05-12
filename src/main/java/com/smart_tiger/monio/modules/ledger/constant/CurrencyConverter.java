package com.smart_tiger.monio.modules.ledger.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import static com.smart_tiger.monio.utils.StringUtils.isNullOrEmpty;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {


    @Override
    public String convertToDatabaseColumn(Currency attribute) {
        if (attribute == null) {
            return null;
        } else {
            return attribute.toString();
        }
    }

    @Override
    public Currency convertToEntityAttribute(String dbData) {
        if (isNullOrEmpty(dbData)) {
            return null;
        } else {
            return Currency.valueOf(dbData);
        }
    }

}

package com.smart_tiger.monio.modules.ledger.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FiscalItemStatusConverter implements AttributeConverter<FiscalItemStatus, String> {


    @Override
    public String convertToDatabaseColumn(FiscalItemStatus attribute) {
        return attribute.toString();
    }

    @Override
    public FiscalItemStatus convertToEntityAttribute(String dbData) {
        return FiscalItemStatus.valueOf(dbData);
    }

}

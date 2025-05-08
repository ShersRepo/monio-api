package com.smart_tiger.monio.modules.calendarrecurrencerules;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RecurrenceFrequencyConverter implements AttributeConverter<RecurrenceFrequency, String> {


    @Override
    public String convertToDatabaseColumn(RecurrenceFrequency attribute) {
        return attribute.toString();
    }

    @Override
    public RecurrenceFrequency convertToEntityAttribute(String dbData) {
        return RecurrenceFrequency.valueOf(dbData);
    }

}

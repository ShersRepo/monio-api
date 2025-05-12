package com.smart_tiger.monio.modules.user.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserAccountStatusConverter implements AttributeConverter<UserAccountStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserAccountStatus attribute) {
        return attribute.toString();
    }

    @Override
    public UserAccountStatus convertToEntityAttribute(String dbData) {
        return UserAccountStatus.valueOf(dbData);
    }

}

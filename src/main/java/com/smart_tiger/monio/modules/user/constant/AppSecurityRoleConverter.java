package com.smart_tiger.monio.modules.user.constant;

import com.smart_tiger.monio.middleware.security.verification.AppSecurityRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AppSecurityRoleConverter implements AttributeConverter<AppSecurityRole, String> {

    @Override
    public String convertToDatabaseColumn(AppSecurityRole role) {
        return role.toString();
    }

    @Override
    public AppSecurityRole convertToEntityAttribute(String name) {
        return AppSecurityRole.fromString(name);
    }

}

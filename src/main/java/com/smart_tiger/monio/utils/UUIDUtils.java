package com.smart_tiger.monio.utils;


import com.smart_tiger.monio.middleware.exception.BadRequestException;

import java.util.UUID;

public final class UUIDUtils {

    private UUIDUtils() {
    }

    public static UUID idFromString(String id) throws BadRequestException {
        if (StringUtils.isNullOrEmpty(id)) {
            throw new BadRequestException();
        }

        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException();
        }
    }

}

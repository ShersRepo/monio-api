package com.smart_tiger.monio.utils;

/**
 * Instead of using Jackson configuration and adding custom serializers, have decided to use this interface.
 * Apply to Enum classes and when used in conjuction with EnumWithConverter for spring serializing/deserializing.
 */
public interface WithDtoValue {

    /**
     * Annotate this with @JsonValue to enable deserialization for the implemented class
     * @return String of deserialized value to retrieve from JSON
     */
    String getDtoValue();

}

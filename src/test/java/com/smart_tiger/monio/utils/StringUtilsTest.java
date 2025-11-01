package com.smart_tiger.monio.utils;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag( "UnitTest")
class StringUtilsTest {

    @Test
    void StringUtils_isNotNullOrEmpty_true_when_valid() {
        String testValue = "A valid string";

        assertThat(StringUtils.isNotNullOrEmpty(testValue)).isTrue();
    }

    @Test
    void StringUtils_isNotNullOrEmpty_false_when_null() {
        String testValue = null;

        assertThat(StringUtils.isNotNullOrEmpty(testValue)).isFalse();
    }

    @Test
    void StringUtils_isNotNullOrEmpty_false_when_empty() {
        String testValue = "";

        assertThat(StringUtils.isNotNullOrEmpty(testValue)).isFalse();
    }

    @Test
    void StringUtils_isNotNullOrEmpty_false_when_empty_spaces() {
        String testValue = "         ";

        assertThat(StringUtils.isNotNullOrEmpty(testValue)).isFalse();
    }

    @Test
    void StringUtils_isNullOrEmpty_true_when_empty_spaces() {
        String testValue = "         ";

        assertThat(StringUtils.isNullOrEmpty(testValue)).isTrue();
    }

    @Test
    void StringUtils_isNullOrEmpty_true_when_empty() {
        String testValue = "";

        assertThat(StringUtils.isNullOrEmpty(testValue)).isTrue();
    }

    @Test
    void StringUtils_isNullOrEmpty_true_when_null() {
        String testValue = null;

        assertThat(StringUtils.isNullOrEmpty(testValue)).isTrue();
    }

    @Test
    void StringUtils_isNullOrEmpty_false_when_valid() {
        String testValue = "A valid string";

        assertThat(StringUtils.isNullOrEmpty(testValue)).isFalse();
    }

}

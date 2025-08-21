package com.ureca.ocean.jjh.stat.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Period {
    _7D("7D"),
    _30D("30D"),
    _180D("180D"),
    _365D("365D"),
    ALL("ALL");

    private final String value;

    Period(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static Period from(String input) {
        for (Period period : Period.values()) {
            if (period.value.equalsIgnoreCase(input)) {
                return period;
            }
        }
        throw new IllegalArgumentException("Invalid period: " + input);
    }
}


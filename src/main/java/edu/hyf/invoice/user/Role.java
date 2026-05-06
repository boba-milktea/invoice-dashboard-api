package edu.hyf.invoice.user;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    ADMIN,
    USER;

    @JsonValue
    public String toValue() { return name().toLowerCase(); }
}

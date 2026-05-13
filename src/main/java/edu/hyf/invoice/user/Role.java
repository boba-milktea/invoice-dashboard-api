package edu.hyf.invoice.user;

import com.fasterxml.jackson.annotation.JsonValue;

// see if we create a table for roles for later
public enum Role {
    ADMIN,
    USER;

    /*@JsonValue
    public String toValue() { return name().toLowerCase(); }*/
}

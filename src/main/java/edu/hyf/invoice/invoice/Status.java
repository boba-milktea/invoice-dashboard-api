package edu.hyf.invoice.invoice;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    DRAFT,
    PENDING,
    PAID,
    OVERDUE,
    CANCELLED;


    @JsonValue
    public String toValue() { return name().toLowerCase(); }
}

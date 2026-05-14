package edu.hyf.invoice.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)

public class InvoiceAlreadyExistsException extends RuntimeException {

    public InvoiceAlreadyExistsException(String reference) {
        super("Invoice with this reference: " + reference + " already exists.");
    }
}

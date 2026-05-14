package edu.hyf.invoice.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)

public class InvoiceItemNotFoundException extends RuntimeException{
    public InvoiceItemNotFoundException(Long id) {
        super ("Invoice Item not found with id: " + id);
    }
}

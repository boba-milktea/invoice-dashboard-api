package edu.hyf.invoice.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)

public class InvoiceNotFoundException extends RuntimeException{
    public InvoiceNotFoundException (String ref) {
        super ("Invoice not found with reference: " + ref);
    }
}

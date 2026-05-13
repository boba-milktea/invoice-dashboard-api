package edu.hyf.invoice.invoice;

import edu.hyf.invoice.invoice.dto.InvoiceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//TODO check later if we can have a main /api/v1 globally somewhere
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor

public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices () {
        return ResponseEntity.ok(invoiceService.findAllInvoices());
    }


}

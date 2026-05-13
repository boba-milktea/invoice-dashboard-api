package edu.hyf.invoice.invoice;

import edu.hyf.invoice.client.Client;
import edu.hyf.invoice.common.exception.InvoiceNotFoundException;
import edu.hyf.invoice.invoice.dto.InvoiceRequestDTO;
import edu.hyf.invoice.invoice.dto.InvoiceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class InvoiceService {

    private final InvoiceRepository repository;
    private final InvoiceMapper invoiceMapper;

    public @Nullable List<InvoiceResponseDTO> findAllInvoices() {
        return repository.findAll()
                .stream()
                .map(invoiceMapper::toResponseDTO)
                .toList();
    }

    // TODO findByReference
    // TODO findDueInvoices
    // TODO findInvoicesByClient


}

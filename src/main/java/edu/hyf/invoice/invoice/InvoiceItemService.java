package edu.hyf.invoice.invoice;

import edu.hyf.invoice.common.exception.InvoiceItemNotFoundException;
import edu.hyf.invoice.invoice.dto.InvoiceItemPatchRequest;
import edu.hyf.invoice.invoice.dto.InvoiceItemRequest;
import edu.hyf.invoice.invoice.dto.InvoiceResponse;
import edu.hyf.invoice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor

public class InvoiceItemService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemMapper invoiceItemMapper;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceService invoiceService;

    public InvoiceResponse addItem (String invoiceReference, UserPrincipal userPrincipal, InvoiceItemRequest dto) {

        Invoice invoice = invoiceService.getInvoiceForPrincipal(invoiceReference, userPrincipal);

        InvoiceItem item = invoiceItemMapper.toEntity(dto);

        recalculateLineTotal(item);

        item.setInvoice(invoice);

        invoice.getInvoiceItems().add(item);

        invoiceService.recalculateInvoiceTotals(invoice);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponseDTO(savedInvoice);
    }

    public InvoiceResponse updateItem(String invoiceReference, Long itemId, UserPrincipal userPrincipal, InvoiceItemPatchRequest dto){

        Invoice invoice = invoiceService.getInvoiceForPrincipal(invoiceReference, userPrincipal);

        InvoiceItem item = findInvoiceItemByInvoice(invoice, itemId);

        invoiceItemMapper.updatePatching(dto, item);

        recalculateLineTotal(item);

        invoiceService.recalculateInvoiceTotals(invoice);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponseDTO(savedInvoice);

    }

    public void deleteItem(String invoiceReference, Long itemId, UserPrincipal userPrincipal){

        Invoice invoice = invoiceService.getInvoiceForPrincipal(invoiceReference, userPrincipal);

        InvoiceItem item = findInvoiceItemByInvoice(invoice, itemId);

        invoice.getInvoiceItems().remove(item);

        invoiceService.recalculateInvoiceTotals(invoice);

        invoiceRepository.save(invoice);
    }

    public InvoiceItem findInvoiceItemByInvoice(Invoice invoice, Long itemId) {
        return invoice.getInvoiceItems()
                .stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new InvoiceItemNotFoundException(itemId));
    }

    private void recalculateLineTotal(InvoiceItem item) {
        BigDecimal lineTotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        item.setLineTotal(lineTotal);
    }

}

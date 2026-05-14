package edu.hyf.invoice.invoice;

import edu.hyf.invoice.client.Client;
import edu.hyf.invoice.client.ClientRepository;
import edu.hyf.invoice.common.exception.ClientNotFoundException;
import edu.hyf.invoice.common.exception.InvoiceAlreadyExistsException;
import edu.hyf.invoice.common.exception.InvoiceNotFoundException;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.invoice.dto.InvoicePatchRequest;
import edu.hyf.invoice.invoice.dto.InvoiceRequest;
import edu.hyf.invoice.invoice.dto.InvoiceResponse;
import edu.hyf.invoice.user.User;
import edu.hyf.invoice.user.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.21");

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public List<InvoiceResponse> findAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoiceMapper::toResponseDTO)
                .toList();
    }

    public InvoiceResponse findInvoiceByReference(String ref, UUID userId) {
        return invoiceMapper.toResponseDTO(invoiceRepository.findByReferenceAndUserId(ref, userId).orElseThrow(()
                -> new InvoiceNotFoundException(ref)));
    }

    public Page<@NonNull InvoiceResponse> findMyInvoices(UUID userId, Pageable pageable) {
        return invoiceRepository.findByUserId(userId, pageable)
                .map(invoiceMapper::toResponseDTO);
    }

    public List<InvoiceResponse> getDueInvoice(UUID userId) {
        return invoiceRepository.getDueInvoice(userId, List.of(Status.OVERDUE, Status.PENDING))
                .stream()
                .map(invoiceMapper::toResponseDTO)
                .toList();
    }

    public List<InvoiceResponse> findInvoicesWithMinMax(UUID userId, BigDecimal min, BigDecimal max) {
        return invoiceRepository.findInvoicesWithMinMax(userId, min, max)
                .stream()
                .map(invoiceMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public InvoiceResponse saveInvoice(UUID userId, InvoiceRequest dto) {
        if (invoiceRepository.existsByReferenceAndUserId(dto.reference(), userId)) {
            throw new InvoiceAlreadyExistsException(dto.reference());
        }
        if (dto.dueDate().isBefore(dto.issueDate())) {
            throw new IllegalArgumentException("Due date must be on or after issue date.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundByIdException(userId));
        Client client = clientRepository.findByIdAndUser_Id(dto.clientId(), userId)
                .orElseThrow(() -> new ClientNotFoundException(dto.clientId()));

        Invoice invoice = invoiceMapper.toEntity(dto);
        invoice.setSubtotal(BigDecimal.ZERO.setScale(2));
        invoice.setTaxAmount(BigDecimal.ZERO.setScale(2));
        invoice.setTotalAmount(BigDecimal.ZERO.setScale(2));
        invoice.setClient(client);
        invoice.setUser(user);

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponseDTO(savedInvoice);
    }

    @Transactional
    public InvoiceResponse updateInvoice(String reference, UUID userId, InvoicePatchRequest dto) {
        Invoice invoice = findInvoice(userId, reference);
        invoiceMapper.updatePatching(dto, invoice);
        if (dto.clientId() != null) {
            Client client = clientRepository.findByIdAndUser_Id(dto.clientId(), userId)
                    .orElseThrow(() -> new ClientNotFoundException(dto.clientId()));
            invoice.setClient(client);
        }
        if (invoice.getDueDate().isBefore(invoice.getIssueDate())) {
            throw new IllegalArgumentException("Due date must be on or after issue date.");
        }
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponseDTO(savedInvoice);
    }

    @Transactional
    public void deleteInvoice(UUID userId, String reference) {
        Invoice invoice = findInvoice(userId, reference);
        invoiceRepository.delete(invoice);
    }

    public Invoice findInvoice(UUID userId, String reference) {
        return invoiceRepository.findByReferenceAndUserId(reference, userId).orElseThrow(()
                -> new InvoiceNotFoundException(reference));
    }

    public void recalculateInvoiceTotals(Invoice invoice) {
        BigDecimal subtotal = invoice.getInvoiceItems()
                .stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxAmount = subtotal.multiply(VAT_RATE);
        BigDecimal totalAmount = subtotal.add(taxAmount);

        invoice.setSubtotal(subtotal.setScale(2, java.math.RoundingMode.HALF_UP));
        invoice.setTaxAmount(taxAmount.setScale(2, java.math.RoundingMode.HALF_UP));
        invoice.setTotalAmount(totalAmount.setScale(2, java.math.RoundingMode.HALF_UP));
    }

}

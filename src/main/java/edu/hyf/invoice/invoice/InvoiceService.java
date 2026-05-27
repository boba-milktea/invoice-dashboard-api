package edu.hyf.invoice.invoice;

import edu.hyf.invoice.client.Client;
import edu.hyf.invoice.client.ClientRepository;
import edu.hyf.invoice.common.exception.ClientNotFoundException;
import edu.hyf.invoice.common.exception.InvoiceAlreadyExistsException;
import edu.hyf.invoice.common.exception.InvoiceNotFoundException;
import edu.hyf.invoice.common.exception.UserNotFoundByIdException;
import edu.hyf.invoice.common.utils.AccessHelper;
import edu.hyf.invoice.invoice.dto.InvoicePatchRequest;
import edu.hyf.invoice.invoice.dto.InvoiceRequest;
import edu.hyf.invoice.invoice.dto.InvoiceResponse;
import edu.hyf.invoice.security.UserPrincipal;
import edu.hyf.invoice.user.User;
import edu.hyf.invoice.user.UserRepository;
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

<<<<<<< HEAD
// TODO Constratint - One invoice should have at least one item. Empty invoice is not allowed.


=======
>>>>>>> 1c82202 (add invoice testing)
public class InvoiceService {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.21");

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AccessHelper accessHelper;


    public Page<InvoiceResponse> findAllInvoices(UserPrincipal userPrincipal, Pageable pageable) {

        if (userPrincipal.isSuperAdmin()) {
            return invoiceRepository.findAll(pageable).map(invoiceMapper::toResponseDTO);
        }
        return invoiceRepository.findByUserId(userPrincipal.getId(), pageable).map(invoiceMapper::toResponseDTO);
    }

    public InvoiceResponse findInvoiceByReference(String ref, UserPrincipal userPrincipal) {
        Invoice invoice;
        if (userPrincipal.isSuperAdmin()) {
            invoice = invoiceRepository.findByReference(ref).orElseThrow(()
                    -> new InvoiceNotFoundException(ref));
        } else {
            invoice = invoiceRepository.findByReferenceAndUserId(ref, userPrincipal.getId()).orElseThrow(()
                    -> new InvoiceNotFoundException(ref));
        }
        return invoiceMapper.toResponseDTO(invoice);
    }


    public List<InvoiceResponse> getDueInvoice(UserPrincipal userPrincipal) {

        if (userPrincipal.isSuperAdmin()) {
            return toResponse(invoiceRepository.getDueInvoice(List.of(Status.OVERDUE, Status.PENDING)));
        } else {
            return toResponse(invoiceRepository.getDueInvoiceById(userPrincipal.getId(), List.of(Status.OVERDUE, Status.PENDING)));
        }
    }

    public List<InvoiceResponse> findInvoicesWithMinMax(UserPrincipal userPrincipal, BigDecimal min, BigDecimal max) {

        if (userPrincipal.isSuperAdmin()) {
            return toResponse(invoiceRepository.findInvoicesWithMinMax(min, max));
        }
        return toResponse(invoiceRepository.findInvoicesWithMinMaxById (userPrincipal.getId(), min, max));
    }


    @Transactional
    public InvoiceResponse saveInvoice(UserPrincipal userPrincipal, InvoiceRequest dto) {

        UUID ownerId = accessHelper.resolveOwnerUserId(userPrincipal, dto.ownerUserId());


        if (invoiceRepository.existsByReferenceAndUserId(dto.reference(), ownerId)) {
            throw new InvoiceAlreadyExistsException(dto.reference());
        }

        if (dto.dueDate().isBefore(dto.issueDate())) {
            throw new IllegalArgumentException("Due date must be on or after issue date.");
        }

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundByIdException(ownerId));

        Client client = clientRepository.findByIdAndUserId(dto.clientId(), ownerId)
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
    public InvoiceResponse updateInvoice(String reference, UserPrincipal userPrincipal, InvoicePatchRequest dto) {

        Invoice invoice = getInvoiceForPrincipal(reference, userPrincipal);

        invoiceMapper.updatePatching(dto, invoice);

        if (dto.clientId() != null) {
            Client client = clientRepository.findByIdAndUserId(dto.clientId(), invoice.getUser().getId()).orElseThrow(()
                    -> new ClientNotFoundException(dto.clientId()));

            invoice.setClient(client);
        }

        if (invoice.getDueDate().isBefore(invoice.getIssueDate())) {
            throw new IllegalArgumentException("Due date must be on or after issue date.");
        }


        Invoice savedInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponseDTO(savedInvoice);
    }

    @Transactional
    public void deleteInvoice(UserPrincipal userPrincipal, String reference) {

        Invoice invoice = getInvoiceForPrincipal(reference, userPrincipal);

        invoiceRepository.delete(invoice);

    }
    // Helper
    public List<InvoiceResponse> toResponse(List<Invoice> invoices) {
        return invoices.stream().map(invoiceMapper::toResponseDTO).toList();
    }

    public Invoice getInvoiceForPrincipal(String reference, UserPrincipal userPrincipal) {
        if (userPrincipal.isSuperAdmin()) {
            return invoiceRepository.findByReference(reference)
                    .orElseThrow(() -> new InvoiceNotFoundException(reference));
        } else {
            return invoiceRepository.findByReferenceAndUserId(reference, userPrincipal.getId())
                    .orElseThrow(() -> new InvoiceNotFoundException(reference));
        }
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

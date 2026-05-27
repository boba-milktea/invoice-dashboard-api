package edu.hyf.invoice.invoice;

import edu.hyf.invoice.client.Client;
import edu.hyf.invoice.client.ClientRepository;
import edu.hyf.invoice.common.exception.InvoiceAlreadyExistsException;
import edu.hyf.invoice.common.utils.AccessHelper;
import edu.hyf.invoice.invoice.dto.InvoicePatchRequest;
import edu.hyf.invoice.invoice.dto.InvoiceRequest;
import edu.hyf.invoice.invoice.dto.InvoiceResponse;
import edu.hyf.invoice.security.UserPrincipal;
import edu.hyf.invoice.user.User;
import edu.hyf.invoice.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccessHelper accessHelper;

    @Mock
    private UserPrincipal userPrincipal;

    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void saveInvoice_shouldThrowIllegalArgumentException_whenDueDateIsBeforeIssueDate() {
        // arrange
        UUID ownerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        LocalDate issueDate = LocalDate.of(2026, 5, 26);
        LocalDate dueDate = LocalDate.of(2026, 5, 25);

        InvoiceRequest request = new InvoiceRequest(
                "INV-001",
                issueDate,
                dueDate,
                Status.PENDING,
                clientId,
                ownerId
        );
        when(accessHelper.resolveOwnerUserId(userPrincipal, request.ownerUserId()))
                .thenReturn(ownerId);

        when(invoiceRepository.existsByReferenceAndUserId("INV-001", ownerId))
                .thenReturn(false);

        // act + assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> invoiceService.saveInvoice(userPrincipal, request)
        );

        assertEquals(
                "Due date must be on or after issue date.",
                 exception.getMessage()
        );

        verify(userRepository, never()).findById(any());
        verify(clientRepository, never()).findByIdAndUserId(any(), any());
        verify(invoiceMapper, never()).toEntity(any());
        verify(invoiceRepository, never()).save(any());

    }

    @Test
    void saveInvoice_shouldThrowException_whenReferenceAlreadyExists() {
        // arrange
        UUID ownerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = LocalDate.now().plusDays(30);

        String existingReference = "INV-001";

        InvoiceRequest request = new InvoiceRequest(
                existingReference,
                issueDate,
                dueDate,
                Status.PENDING,
                clientId,
                ownerId
        );

        when(accessHelper.resolveOwnerUserId(userPrincipal, request.ownerUserId()))
                .thenReturn(ownerId);

        when(invoiceRepository.existsByReferenceAndUserId(existingReference, ownerId)).thenReturn(true);


        InvoiceAlreadyExistsException exception = assertThrows(
                InvoiceAlreadyExistsException.class,
                () -> invoiceService.saveInvoice(userPrincipal, request)
        );

        assertEquals(
                "Invoice with this reference: " + existingReference + " already exists.",
                exception.getMessage()
        );

        // verify
        verify(userRepository, never()).findById(any());
        verify(clientRepository, never()).findByIdAndUserId(any(), any());
        verify(invoiceRepository, never()).save(any());

    }

    @Test
    void saveInvoice_shouldSaveInvoice_whenRequestIsValid() {
        // arrange
        UUID ownerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        InvoiceRequest request = new InvoiceRequest(
                "INV-001",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                Status.PENDING,
                clientId,
                ownerId
        );

        User user = new User();
        user.setId(ownerId);

        Client client = new Client();
        client.setUser(user);
        client.setId(clientId);

        Invoice invoice = new Invoice();
        invoice.setReference("INV-001");

        Invoice savedInvoice = new Invoice();
        savedInvoice.setReference("INV-001");
        savedInvoice.setUser(user);
        savedInvoice.setClient(client);

        InvoiceResponse response = mock(InvoiceResponse.class);

        when(accessHelper.resolveOwnerUserId(userPrincipal, request.ownerUserId()))
                .thenReturn(ownerId);

        when(invoiceRepository.existsByReferenceAndUserId("INV-001", ownerId))
                .thenReturn(false);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));

        when(clientRepository.findByIdAndUserId(clientId, ownerId)).thenReturn(Optional.of(client));

        when(invoiceRepository.save(invoice)).thenReturn(savedInvoice);

        when(invoiceMapper.toEntity(request)).thenReturn(invoice);

        when(invoiceMapper.toResponseDTO(savedInvoice)).thenReturn(response);

        // act
        InvoiceResponse result = invoiceService.saveInvoice(userPrincipal, request);

        // assert
        assertNotNull(result);

        // verify
        verify(invoiceRepository).save(invoice);
        verify(invoiceMapper).toResponseDTO(savedInvoice);
    }

    @Test
    void findAllInvoices_shouldReturnAllInvoices_whenUserIsSuperAdmin() {
        // arrange
        Pageable pageable = PageRequest.of(0, 10);

        Invoice invoice = new Invoice();
        InvoiceResponse response = mock(InvoiceResponse.class);

        Page<Invoice> invoicePage = new PageImpl<>(List.of(invoice));

        when(userPrincipal.isSuperAdmin()).thenReturn(true);

        when(invoiceRepository.findAll(pageable)).thenReturn(invoicePage);

        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        //act
        Page<InvoiceResponse> result = invoiceService.findAllInvoices(userPrincipal, pageable);

        // assert
        assertEquals(1, result.getTotalElements());

        assertEquals(response, result.getContent().get(0));

        verify(invoiceRepository).findAll(pageable);

        verify(invoiceRepository, never()).findByUserId(any(), any());
    }

    @Test
    void findAllInvoices_shouldReturnAllInvoices_whenUserIsNotSuperAdmin() {
        // arrange
        UUID ownerId = UUID.randomUUID();

        Pageable pageable = PageRequest.of(0, 10);

        Invoice invoice = new Invoice();

        InvoiceResponse response = mock(InvoiceResponse.class);

        Page<Invoice> invoicePage = new PageImpl<>(List.of(invoice));

        when(userPrincipal.isSuperAdmin()).thenReturn(false);

        when(userPrincipal.getId()).thenReturn(ownerId);

        when(invoiceRepository.findByUserId(ownerId, pageable)).thenReturn(invoicePage);

        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        // act
        Page<InvoiceResponse> result = invoiceService.findAllInvoices(userPrincipal, pageable);

        // assert
        assertEquals(1, result.getTotalElements());

        assertEquals(response, result.getContent().get(0));

        verify(invoiceRepository).findByUserId(ownerId, pageable);

        verify(invoiceRepository, never()).findAll(pageable);
    }

    @Test
    void findInvoiceByReference_shouldFindReference_whenUserIsSuperAdmin() {

        String reference = "INV-001";

        Invoice invoice = new Invoice();
        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(invoiceRepository.findByReference(reference)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        // act
        InvoiceResponse result = invoiceService.findInvoiceByReference(reference, userPrincipal);

        // assert
        assertEquals(response, result);

        verify(invoiceRepository).findByReference(reference);
        verify(invoiceRepository, never()).findByReferenceAndUserId(anyString(), any());
    }

    @Test
    void findInvoiceByReference_shouldFindReference_whenUserIsNotSuperAdmin() {

        String reference = "INV-001";
        UUID ownerId = UUID.randomUUID();

        Invoice invoice = new Invoice();
        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(false);
        when(userPrincipal.getId()).thenReturn(ownerId);
        when(invoiceRepository.findByReferenceAndUserId(reference, ownerId)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        // act
        InvoiceResponse result = invoiceService.findInvoiceByReference(reference, userPrincipal);

        // assert
        assertEquals(response, result);

        verify(invoiceRepository).findByReferenceAndUserId(reference, ownerId);
        verify(invoiceRepository, never()).findByReference(reference);
    }

    @Test
    void getDueInvoice_shouldReturnAllDueInvoices_whenUserIsSuperAdmin() {
        Invoice invoice = new Invoice();

        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(invoiceRepository.getDueInvoice(List.of(Status.OVERDUE, Status.PENDING))).thenReturn(List.of(invoice));
        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        List<InvoiceResponse> result = invoiceService.getDueInvoice(userPrincipal);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(invoiceRepository).getDueInvoice(List.of(Status.OVERDUE, Status.PENDING));
        verify(invoiceRepository, never()).getDueInvoiceById(any(), anyList());
    }

    @Test
    void getDueInvoice_shouldReturnAllDueInvoices_whenUserIsNotSuperAdmin() {
        Invoice invoice = new Invoice();
        UUID ownerId = UUID.randomUUID();

        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(false);
        when(userPrincipal.getId()).thenReturn(ownerId);
        when(invoiceRepository.getDueInvoiceById( ownerId, List.of(Status.OVERDUE, Status.PENDING))).thenReturn(List.of(invoice));
        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        List<InvoiceResponse> result = invoiceService.getDueInvoice(userPrincipal);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(invoiceRepository).getDueInvoiceById(ownerId, List.of(Status.OVERDUE, Status.PENDING));
        verify(invoiceRepository, never()).getDueInvoice(anyList());
    }

    @Test
    void findInvoicesWithMinMax_shouldReturnAllMatchingInvoices_whenUserIsSuperAdmin() {
        BigDecimal min = new BigDecimal("100.00");
        BigDecimal max = new BigDecimal("1000.00");

        Invoice invoice = new Invoice();
        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(true);
        when(invoiceRepository.findInvoicesWithMinMax(min, max)).thenReturn(List.of(invoice));
        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        List<InvoiceResponse> result = invoiceService.findInvoicesWithMinMax(userPrincipal, min, max);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(invoiceRepository).findInvoicesWithMinMax(min, max);
        verify(invoiceRepository, never()).findInvoicesWithMinMaxById(any(), any(), any());
    }

    @Test
    void findInvoicesWithMinMax_shouldReturnAllMatchingInvoices_whenUserIsNotSuperAdmin() {
        BigDecimal min = new BigDecimal("100.00");
        BigDecimal max = new BigDecimal("1000.00");
        UUID ownerId = UUID.randomUUID();

        Invoice invoice = new Invoice();
        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(false);
        when(userPrincipal.getId()).thenReturn(ownerId);
        when(invoiceRepository.findInvoicesWithMinMaxById(ownerId, min, max)).thenReturn(List.of(invoice));
        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        List<InvoiceResponse> result = invoiceService.findInvoicesWithMinMax(userPrincipal, min, max);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(invoiceRepository).findInvoicesWithMinMaxById(ownerId, min, max);
        verify(invoiceRepository, never()).findInvoicesWithMinMax(any(), any());
    }

    @Test
    void updateInvoice_shouldUpdateAndSaveInvoice_withRequestIsValid() {
        String reference = "INV-002";
        UUID ownerId = UUID.randomUUID();

        User user = new User();
        user.setId(ownerId);

        Invoice invoice = new Invoice();
        invoice.setReference(reference);
        invoice.setUser(user);
        invoice.setIssueDate(LocalDate.of(2026,  5, 26));
        invoice.setDueDate(LocalDate.of(2026,  6, 25));
        invoice.setStatus(Status.PENDING);

        InvoicePatchRequest request = new InvoicePatchRequest(
                null,
                null,
                null,
                null
        );

        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(false);
        when(userPrincipal.getId()).thenReturn(ownerId);
        when(invoiceRepository.findByReferenceAndUserId(reference, ownerId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        InvoiceResponse result = invoiceService.updateInvoice(reference, userPrincipal, request);

        assertEquals(response, result);

        verify(invoiceMapper).updatePatching(request, invoice);
        verify(invoiceRepository).save(invoice);
        verify(invoiceMapper).toResponseDTO(invoice);
    }

    @Test
    void updateInvoice_shouldChangeClient_withClientIdProvided() {
        String reference = "INV-002";
        UUID ownerId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        User user = new User();
        user.setId(ownerId);

        Client newClient = new Client();
        newClient.setId(clientId);

        Invoice invoice = new Invoice();
        invoice.setReference(reference);
        invoice.setUser(user);
        invoice.setIssueDate(LocalDate.of(2026,  5, 26));
        invoice.setDueDate(LocalDate.of(2026,  6, 25));
        invoice.setStatus(Status.PENDING);

        InvoicePatchRequest request = new InvoicePatchRequest(
                null,
                null,
                null,
                clientId
        );

        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(false);
        when(userPrincipal.getId()).thenReturn(ownerId);
        when(clientRepository.findByIdAndUserId(clientId, ownerId)).thenReturn(Optional.of(newClient));
        when(invoiceRepository.findByReferenceAndUserId(reference, ownerId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);
        when(invoiceMapper.toResponseDTO(invoice)).thenReturn(response);

        InvoiceResponse result = invoiceService.updateInvoice(reference, userPrincipal, request);

        assertEquals(response, result);
        assertEquals(newClient, invoice.getClient());


        verify(clientRepository).findByIdAndUserId(clientId, ownerId);
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void updateInvoice_shouldThrowException_withDueDateIsBeforeIssueDate() {
        String reference = "INV-002";
        UUID ownerId = UUID.randomUUID();

        User user = new User();
        user.setId(ownerId);

        Invoice invoice = new Invoice();
        invoice.setReference(reference);
        invoice.setUser(user);
        invoice.setIssueDate(LocalDate.of(2026,  5, 26));
        invoice.setDueDate(LocalDate.of(2026,  5, 25));
        invoice.setStatus(Status.PENDING);

        InvoicePatchRequest request = new InvoicePatchRequest(
                null,
                null,
                null,
                null
        );

        InvoiceResponse response = mock(InvoiceResponse.class);

        when(userPrincipal.isSuperAdmin()).thenReturn(false);
        when(userPrincipal.getId()).thenReturn(ownerId);
        when(invoiceRepository.findByReferenceAndUserId(reference, ownerId)).thenReturn(Optional.of(invoice));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> invoiceService.updateInvoice(reference, userPrincipal, request)
        );

       assertEquals("Due date must be on or after issue date.", exception.getMessage());

       verify(invoiceRepository, never()).save(any());
    }

    @Test
    void deleteInvoice_shouldDeleteInvoice_whenInvoiceExists() {
        String ref = "INV-003";
        UUID ownerId = UUID.randomUUID();

        Invoice invoice = new Invoice();

        when(userPrincipal.isSuperAdmin()).thenReturn(false);
        when(userPrincipal.getId()).thenReturn(ownerId);
        when(invoiceRepository.findByReferenceAndUserId(ref, ownerId)).thenReturn(Optional.of(invoice));

        invoiceService.deleteInvoice(userPrincipal, ref);

        verify(invoiceRepository).delete(invoice);
    }

    @Test
    void recalculateInvoiceTotals_shouldCalculateSubtotalTaxAndTotal() {
        Invoice invoice = new Invoice();

        InvoiceItem item1 = new InvoiceItem();
        item1.setUnitPrice(new BigDecimal("100.00"));
        item1.setQuantity(2);

        InvoiceItem item2 = new InvoiceItem();
        item2.setUnitPrice(new BigDecimal("50.00"));
        item2.setQuantity(1);

        invoice.setInvoiceItems(List.of(item1, item2));

        invoiceService.recalculateInvoiceTotals(invoice);

        assertEquals(new BigDecimal("250.00"), invoice.getSubtotal());
        assertEquals(new BigDecimal("52.50"), invoice.getTaxAmount());
        assertEquals(new BigDecimal("302.50"), invoice.getTotalAmount());
    }

}

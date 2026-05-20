package edu.hyf.invoice.invoice;

import edu.hyf.invoice.invoice.dto.InvoiceItemPatchRequest;
import edu.hyf.invoice.invoice.dto.InvoiceItemRequest;
import edu.hyf.invoice.invoice.dto.InvoicePatchRequest;
import edu.hyf.invoice.invoice.dto.InvoiceRequest;
import edu.hyf.invoice.invoice.dto.InvoiceResponse;
import edu.hyf.invoice.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Validated


// TODO solve Serializing PageImpl, use Spring Data's PagedModel

public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceItemService invoiceItemService;

    @GetMapping
    public ResponseEntity<@NonNull Page<@NonNull InvoiceResponse>> getMyInvoices(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {
        return ResponseEntity.ok(invoiceService.findMyInvoices(userPrincipal.getId(), pageable));
    }

    @GetMapping("/due")
    public ResponseEntity<List<InvoiceResponse>> getDueInvoice(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(invoiceService.getDueInvoice(userPrincipal.getId()));
    }

    @GetMapping("/search/by-amount")
    public ResponseEntity<List<InvoiceResponse>> getInvoiceMinMax(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(invoiceService.findInvoicesWithMinMax(userPrincipal.getId(), min, max));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<@NonNull InvoiceResponse> getInvoice(
            @PathVariable String reference,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(invoiceService.findInvoiceByReference(reference, userPrincipal.getId()));
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse created = invoiceService.saveInvoice(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{reference}")
    public ResponseEntity<InvoiceResponse> patchInvoice(
            @PathVariable String reference,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody InvoicePatchRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(reference, userPrincipal.getId(), request));
    }

    @DeleteMapping("/{reference}")
    public ResponseEntity<Void> deleteInvoice(
            @PathVariable String reference,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        invoiceService.deleteInvoice(userPrincipal.getId(), reference);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reference}/items")
    public ResponseEntity<InvoiceResponse> addItem(
            @PathVariable String reference,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody InvoiceItemRequest request) {
        InvoiceResponse updated = invoiceItemService.addItem(reference, userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }

    @PatchMapping("/{reference}/items/{itemId}")
    public ResponseEntity<InvoiceResponse> patchItem(
            @PathVariable String reference,
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody InvoiceItemPatchRequest request) {
        return ResponseEntity.ok(invoiceItemService.updateItem(reference, itemId, userPrincipal.getId(), request));
    }

    @DeleteMapping("/{reference}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable String reference,
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        invoiceItemService.deleteItem(reference, itemId, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }

}

package edu.hyf.invoice.client;

import edu.hyf.invoice.client.dto.ClientPatchRequest;
import edu.hyf.invoice.client.dto.ClientRequest;
import edu.hyf.invoice.client.dto.ClientResponse;
import edu.hyf.invoice.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Validated

public class ClientController {

    private final ClientService clientService;

   @GetMapping
   @Operation(summary = "Get all clients")
   public ResponseEntity<@NonNull Page<@NonNull ClientResponse>> getAllClients(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                      @ParameterObject
                                                                      @PageableDefault(
                                                                              size = 5,
                                                                              sort = "createdAt",
                                                                              direction = Sort.Direction.DESC
                                                                      )Pageable pageable) {
       return ResponseEntity.ok(clientService.findAllClients(userPrincipal.getId(),pageable));
   }

   @GetMapping("/search")
   @Operation(summary = "Get clients by an username")
    public ResponseEntity<@NonNull List<ClientResponse>> getClientsByName(@RequestParam String name, @AuthenticationPrincipal UserPrincipal userPrincipal ) {
       return ResponseEntity.ok(clientService.findClientsByName(name, userPrincipal.getId()));
   }

   @GetMapping("/{id}")
   @Operation(summary = "Get a client by UUID")
    public ResponseEntity<@NonNull ClientResponse> getClientById(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
       return ResponseEntity.ok(clientService.findClientById(id, userPrincipal.getId()));
   }

   @PostMapping
   @Operation(summary = "Add a client")
    public ResponseEntity<@NonNull ClientResponse> createClient(@Valid @RequestBody ClientRequest dto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
       return ResponseEntity.status(HttpStatus.CREATED).body(clientService.saveClient(dto, userPrincipal.getId()));
   }

   @PatchMapping("/{id}")
   @Operation(summary = "Update information in a client")
   public ResponseEntity<@NonNull ClientResponse> updateClient(@PathVariable UUID id, @Valid @RequestBody ClientPatchRequest dto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(clientService.updateClientById(id, dto, userPrincipal.getId()));
   }

   @DeleteMapping("/{id}")
   @Operation(summary = "Delete client by id")
    public ResponseEntity<@NonNull Void> deleteClient(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
       clientService.deleteClient(id, userPrincipal.getId());
       return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}

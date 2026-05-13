package edu.hyf.invoice.client;

import edu.hyf.invoice.client.dto.ClientPatchRequest;
import edu.hyf.invoice.client.dto.ClientRequest;
import edu.hyf.invoice.client.dto.ClientResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
   @Operation(summary = "Get all the clients")
   public ResponseEntity<@NonNull List<ClientResponse>> getAllClients() {
       return ResponseEntity.ok(clientService.findAllClients());
   }

   @GetMapping("/search")
   @Operation(summary = "Get clients by an username")
    public ResponseEntity<@NonNull List<ClientResponse>> getClientsByUsername(@RequestParam String name ) {
       return ResponseEntity.ok(clientService.findClientsByUsername(name));
   }

   @GetMapping("/{id}")
   @Operation(summary = "Get a client by UUID")
    public ResponseEntity<@NonNull ClientResponse> getClientById(@PathVariable UUID id) {
       return ResponseEntity.ok(clientService.findClientById(id));
   }

   @PostMapping
   @Operation(summary = "Add a client")
    public ResponseEntity<@NonNull ClientResponse> createClient(@Valid @RequestBody ClientRequest dto) {
       return ResponseEntity.status(HttpStatus.CREATED).body(clientService.saveClient(dto));
   }

   @PatchMapping("/{id}")
   @Operation(summary = "Update information in a client")
   public ResponseEntity<@NonNull ClientResponse> updateClient(@PathVariable UUID id, @Valid @RequestBody ClientPatchRequest dto) {
        return ResponseEntity.ok(clientService.updateClientById(id, dto));
   }

   @DeleteMapping("/{id}")
   @Operation(summary = "Delete client by id")
    public ResponseEntity<@NonNull Void> deleteClient(@PathVariable UUID id) {
       clientService.deleteClient(id);
       return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}

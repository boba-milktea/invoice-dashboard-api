package edu.hyf.invoice.client.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter

public class ClientRequest {

    @NotBlank(message = "Name is required.")
    private final String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    private final String email;

    @NotBlank(message = "Address is required.")
    @Size(min = 5, max = 255)
    @Pattern(regexp = "^[a-zA-Z0-9\\s,.-]+$", message = "Address contains invalid characters.")
    private String address;

    private UUID ownerUserId;
}

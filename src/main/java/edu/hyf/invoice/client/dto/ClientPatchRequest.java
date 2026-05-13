package edu.hyf.invoice.client.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClientPatchRequest {

    private String name;
    @Email(message = "Email format is invalid.")
    private String email;
    @Size(min = 5, max = 255)
    @Pattern(regexp = "^[a-zA-Z0-9\\s,.-]+$", message = "Address contains invalid characters.")
    private String address;

}

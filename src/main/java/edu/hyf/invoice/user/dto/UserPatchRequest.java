package edu.hyf.invoice.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserPatchRequest {
    private String name;
    @Email(message = "Email format is invalid.")
    private String email;
}

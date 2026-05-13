package edu.hyf.invoice.auth.dto;

import edu.hyf.invoice.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class RegisterRequestDTO {

    @NotBlank(message = "Name is required.")
    public String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is invalid.")
    public String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    public String password;

}



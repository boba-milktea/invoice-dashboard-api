package edu.hyf.invoice.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

public record UserPatchRequest(
        String name,
        @Email(message = "Email format is invalid.")
        String email) {}

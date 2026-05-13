package edu.hyf.invoice.user.dto;

import edu.hyf.invoice.user.Role;
import jakarta.validation.constraints.NotNull;

public record UserRolePatchRequest(
        @NotNull(message = "Role is required.")
        Role role) {

}

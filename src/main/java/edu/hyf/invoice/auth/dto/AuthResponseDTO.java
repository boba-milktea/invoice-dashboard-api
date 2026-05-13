package edu.hyf.invoice.auth.dto;

import edu.hyf.invoice.user.Role;
import lombok.Data;

@Data

public class AuthResponseDTO {
    private String token;
    // private String refreshToken;
    private String name;
    private Role role;
}

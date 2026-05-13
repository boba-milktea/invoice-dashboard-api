package edu.hyf.invoice.user.dto;

import edu.hyf.invoice.user.Role;

import java.util.UUID;

public record UserResponse (UUID id, String name, String email, Role role){
}

package edu.hyf.invoice.client.dto;

import java.util.UUID;

public record ClientResponse (UUID id, String name, String email, String address, UUID userId){
}

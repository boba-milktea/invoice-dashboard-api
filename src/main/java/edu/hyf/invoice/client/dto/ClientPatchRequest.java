package edu.hyf.invoice.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClientPatchRequest {

    private String name;
    private String email;
    private String address;

}

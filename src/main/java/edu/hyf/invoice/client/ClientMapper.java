package edu.hyf.invoice.client;

import edu.hyf.invoice.client.dto.ClientPatchRequest;
import edu.hyf.invoice.client.dto.ClientRequest;
import edu.hyf.invoice.client.dto.ClientResponse;
import org.mapstruct.*;
import tools.jackson.databind.exc.IgnoredPropertyException;

import java.lang.annotation.Target;


@Mapper(componentModel = "spring")

public interface ClientMapper {

    @Mapping(source="user.id", target = "userId")
    ClientResponse toResponseDTO(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Client toEntity(ClientRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE )
    void updatePatching(ClientPatchRequest dto, @MappingTarget Client client);

}

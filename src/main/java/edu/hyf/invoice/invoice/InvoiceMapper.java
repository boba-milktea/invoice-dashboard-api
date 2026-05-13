package edu.hyf.invoice.invoice;

import edu.hyf.invoice.invoice.dto.InvoiceRequestDTO;
import edu.hyf.invoice.invoice.dto.InvoiceResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

public interface InvoiceMapper {
    @Mapping(source="client.id", target="clientId")
    @Mapping(source="user.id", target="userId")
    InvoiceResponseDTO toResponseDTO (Invoice invoice);

    @Mapping(target="id", ignore = true)
    @Mapping(target="invoiceItems", ignore = true)
    @Mapping(target="user", ignore = true)
    @Mapping(target="client", ignore = true)
    @Mapping(target="createdAt", ignore = true)
    @Mapping(target="updatedAt", ignore = true)
    Invoice toEntity(InvoiceRequestDTO dto);
}

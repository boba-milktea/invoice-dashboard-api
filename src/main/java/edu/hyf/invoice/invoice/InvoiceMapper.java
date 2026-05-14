package edu.hyf.invoice.invoice;

import edu.hyf.invoice.invoice.dto.InvoicePatchRequest;
import edu.hyf.invoice.invoice.dto.InvoiceRequest;
import edu.hyf.invoice.invoice.dto.InvoiceResponse;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = InvoiceItemMapper.class)

public interface InvoiceMapper {
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "invoiceItems", target = "items")
    InvoiceResponse toResponseDTO(Invoice invoice);

    @Mapping(target="id", ignore = true)
    @Mapping(target="invoiceItems", ignore = true)
    @Mapping(target="user", ignore = true)
    @Mapping(target="client", ignore = true)
    @Mapping(target="createdAt", ignore = true)
    @Mapping(target="updatedAt", ignore = true)
    Invoice toEntity(InvoiceRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceItems", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePatching(InvoicePatchRequest dto, @MappingTarget Invoice invoice);
}

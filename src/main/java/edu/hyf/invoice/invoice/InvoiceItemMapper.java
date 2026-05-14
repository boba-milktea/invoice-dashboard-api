package edu.hyf.invoice.invoice;

import edu.hyf.invoice.invoice.dto.InvoiceItemPatchRequest;
import edu.hyf.invoice.invoice.dto.InvoiceItemRequest;
import edu.hyf.invoice.invoice.dto.InvoiceItemResponse;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

public interface InvoiceItemMapper {
    InvoiceItemResponse toResponseDTO(InvoiceItem invoiceItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    InvoiceItem toEntity(InvoiceItemRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePatching(InvoiceItemPatchRequest dto, @MappingTarget InvoiceItem item);
}


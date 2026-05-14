package edu.hyf.invoice.invoice;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<@NonNull InvoiceItem, @NonNull Long> {

}

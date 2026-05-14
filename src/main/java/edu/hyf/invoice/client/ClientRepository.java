package edu.hyf.invoice.client;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface ClientRepository extends JpaRepository<@NonNull Client, @NonNull UUID> {

    @Query("""
            select c.name, c.id from Client c
            join c.user u
            where upper(u.name) like upper(concat ('%', :name, '%'))
            """)

    List<Client> findByUsername(@Param("name") String name);
    Optional<Client> findClientById(UUID id);

    Optional<Client> findByIdAndUser_Id(UUID id, UUID userId);

    boolean existsByEmail(String email);
}

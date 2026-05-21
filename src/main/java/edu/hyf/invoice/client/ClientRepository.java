package edu.hyf.invoice.client;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface ClientRepository extends JpaRepository<@NonNull Client, @NonNull UUID> {
    Page<@NonNull Client> findByUserId(UUID uuid, Pageable pageable);

    @Query("""
            select c from Client c
            join c.user u
            where c.user.id = :userId
            and upper(c.name) like upper(concat ('%', :name, '%'))
            """)


    List<Client> findByNameAndUserId(@Param("name") String name, @Param("userId") UUID userId);

    Optional<Client> findByIdAndUserId(UUID id, UUID userid);


    boolean existsByEmail(String email);
}

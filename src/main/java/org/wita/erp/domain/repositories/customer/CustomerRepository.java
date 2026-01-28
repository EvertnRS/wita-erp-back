package org.wita.erp.domain.repositories.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.customer.Customer;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query("SELECT c FROM Customer c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(c.cpf) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "(LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Customer> findBySearchTerm(String searchTerm, Pageable pageable);

    Customer findByEmail(String name);

    Customer findByCpf(String cpf);
}

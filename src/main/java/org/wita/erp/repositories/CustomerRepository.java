package org.wita.erp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.Customer.Customer;
import org.wita.erp.domain.Product.Product;

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

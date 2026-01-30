package org.wita.erp.domain.repositories.purchase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.purchase.Payable;

import java.util.UUID;

public interface PayableRepository extends JpaRepository<Payable, UUID> {
    @Query("SELECT p FROM Payable p WHERE " +
            "(LOWER(p.purchase.supplier.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Payable> findBySearchTerm(String searchTerm, Pageable pageable);
}

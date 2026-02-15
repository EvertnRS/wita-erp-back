package org.wita.erp.domain.repositories.supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.wita.erp.domain.entities.supplier.Supplier;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    @Query("SELECT s FROM Supplier s WHERE " +
            "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(CAST(s.id AS STRING)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Supplier> findBySearchTerm(String searchTerm, Pageable pageable);

    Supplier findByCnpj(String cnpj);
}

package org.wita.erp.domain.repositories.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.audit.SoftDeleteLog;

import java.util.UUID;

public interface SoftDeleteLogRepository extends JpaRepository<SoftDeleteLog, UUID> {
    @Query("SELECT s FROM SoftDeleteLog s WHERE " +
            "(LOWER(s.entityType) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) OR " +
            "LOWER(s.entityId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<SoftDeleteLog> findBySearchTerm(String searchTerm, Pageable pageable);
}

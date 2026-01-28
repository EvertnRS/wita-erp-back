package org.wita.erp.domain.repositories.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.wita.erp.domain.entities.stock.MovementReason;

import java.util.UUID;

public interface MovementReasonRepository extends JpaRepository <MovementReason, UUID> {
    @Query("SELECT m FROM MovementReason m WHERE " +
            "(LOWER(m.reason) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<MovementReason> findBySearchTerm(String searchTerm, Pageable pageable);

    MovementReason findByReason(String reason);
}

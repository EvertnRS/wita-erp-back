package org.wita.erp.domain.repositories.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.wita.erp.domain.entities.report.dto.AccountReport;
import org.wita.erp.domain.entities.transaction.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Transaction, UUID> {
    @Query("""
    SELECT new org.wita.erp.domain.entities.report.dto.AccountReport(
        r.id,
        r.value,
        r.dueDate,
        r.paymentStatus,
        'RECEIVABLE'
    )
    FROM Receivable r
    WHERE r.dueDate <= :dueDateLimit
""")
    List<AccountReport> findAllReceivable(@Param("dueDateLimit") LocalDate dueDateLimit);

    @Query("""
    SELECT new org.wita.erp.domain.entities.report.dto.AccountReport(
        p.id,
        p.value,
        p.dueDate,
        p.paymentStatus,
        'PAYABLE'
    )
    FROM Payable p
    WHERE p.dueDate <= :dueDateLimit
""")
    List<AccountReport> findAllPayable(@Param("dueDateLimit") LocalDate dueDateLimit);

}

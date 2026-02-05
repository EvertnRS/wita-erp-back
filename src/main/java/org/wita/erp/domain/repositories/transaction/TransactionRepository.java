package org.wita.erp.domain.repositories.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wita.erp.domain.entities.transaction.Transaction;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}

package org.wita.erp.infra.config.audit;

import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditReaderProvider {
    public AuditReader get(EntityManager entityManager) {
        return AuditReaderFactory.get(entityManager);
    }
}


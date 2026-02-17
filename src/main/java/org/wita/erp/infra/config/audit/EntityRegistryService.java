package org.wita.erp.infra.config.audit;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.infra.config.audit.revision.CustomRevisionEntity;
import org.wita.erp.infra.config.audit.revision.RevisionDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntityRegistryService {
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<RevisionDTO<Object>> getRevisions(String entityName, UUID id, Integer revision, Pageable pageable) {
        EntityType entityType = EntityType.fromPath(entityName);
        Class<?> entityClass = entityType.getEntityClass();
        AuditReader reader = AuditReaderFactory.get(entityManager);

        AuditQuery query = reader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true)
                .add(AuditEntity.id().eq(id));

        if (revision != null) {
            query.add(AuditEntity.revisionNumber().eq(revision));
        } else {
            query.addOrder(AuditEntity.revisionNumber().desc());
        }

        AuditQuery countQuery = reader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true)
                .addProjection(AuditEntity.revisionNumber().count())
                .add(AuditEntity.id().eq(id));

        if (revision != null) {
            countQuery.add(AuditEntity.revisionNumber().eq(revision));
        }

        long total = (Long) countQuery.getSingleResult();

        if (total == 0) {
            return Page.empty(pageable);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = (List<Object[]>) query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        List<RevisionDTO<Object>> content = results.stream()
                .map(this::mapToDTO)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    private RevisionDTO<Object> mapToDTO(Object[] r) {

        Object entity = r[0];
        CustomRevisionEntity revEntity =
                (CustomRevisionEntity) r[1];
        RevisionType revType = (RevisionType) r[2];

        return new RevisionDTO<>(
                entity,
                revEntity.getId(),
                Instant.ofEpochMilli(revEntity.getTimestamp()),
                revType,
                revEntity.getUserId()
        );
    }

}

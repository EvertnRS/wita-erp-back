package org.wita.erp.services.audit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.AuditQueryCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.wita.erp.infra.config.audit.AuditReaderProvider;
import org.wita.erp.infra.config.audit.EntityRegistryService;
import org.wita.erp.infra.config.audit.revision.CustomRevisionEntity;
import org.wita.erp.infra.config.audit.revision.GlobalRevisionDTO;
import org.wita.erp.infra.config.audit.revision.RevisionDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class EntityRegistryServiceTest {
    @Mock
    private EntityManager entityManager;
    @Mock
    private AuditReaderProvider auditReaderProvider;
    @Mock
    private AuditReader auditReader;
    @Mock
    private AuditQuery auditQuery;
    @Mock
    private AuditQuery countAuditQuery;
    @InjectMocks
    private EntityRegistryService service;
    private Pageable pageable;
    @Mock
    private AuditQueryCreator queryCreator;
    @Mock
    private Query dataQuery;
    @Mock
    private Query countQuery;

    @BeforeEach
    void setup() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve retornar as revisões de uma entidade específica")
    void shouldReturnRevisions() {
        UUID entityId = UUID.randomUUID();

        Object entity = new Object();
        CustomRevisionEntity revisionEntity = new CustomRevisionEntity();
        revisionEntity.setId(1);
        revisionEntity.setTimestamp(System.currentTimeMillis());
        revisionEntity.setUserId(UUID.randomUUID());

        Object[] row = new Object[]{
                entity,
                revisionEntity,
                RevisionType.ADD
        };

        Mockito.when(auditReaderProvider.get(entityManager))
                .thenReturn(auditReader);

        Mockito.when(auditReader.createQuery())
                .thenReturn(queryCreator)
                .thenReturn(queryCreator);

        Mockito.when(queryCreator.forRevisionsOfEntity(
                        Mockito.any(),
                        Mockito.eq(false),
                        Mockito.eq(true)))
                .thenReturn(auditQuery)
                .thenReturn(countAuditQuery);

        Mockito.when(auditQuery.add(Mockito.any())).thenReturn(auditQuery);
        Mockito.when(auditQuery.addOrder(Mockito.any())).thenReturn(auditQuery);
        Mockito.when(auditQuery.setFirstResult(Mockito.anyInt())).thenReturn(auditQuery);
        Mockito.when(auditQuery.setMaxResults(Mockito.anyInt())).thenReturn(auditQuery);

        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);

        Mockito.when(auditQuery.getResultList()).thenReturn(mockResult);
        Mockito.when(countAuditQuery.addProjection(Mockito.any())).thenReturn(countAuditQuery);
        Mockito.when(countAuditQuery.add(Mockito.any())).thenReturn(countAuditQuery);
        Mockito.when(countAuditQuery.getSingleResult()).thenReturn(1L);

        Page<RevisionDTO<Object>> result =
                service.getRevisions("product", entityId, null, pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals(RevisionType.ADD, result.getContent().getFirst().revisionType());
        Mockito.verify(auditReaderProvider).get(entityManager);
    }


    @Test
    @DisplayName("Deve retornar as ultimas revisões de alterações de entidade em geral")
    void shouldReturnGlobalRevisions() {
        UUID userId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();

        List<Object[]> mockRows = Collections.singletonList(
                new Object[]{
                        1L,
                        System.currentTimeMillis(),
                        userId,
                        "product",
                        entityId,
                        0
                }
        );

        Mockito.when(entityManager.createNativeQuery(Mockito.contains("SELECT * FROM")))
                .thenReturn(dataQuery);
        Mockito.when(entityManager.createNativeQuery(Mockito.contains("SELECT COUNT(*)")))
                .thenReturn(countQuery);
        Mockito.when((dataQuery).setParameter(Mockito.anyString(), Mockito.any()))
                .thenReturn(dataQuery);
        Mockito.when((dataQuery).getResultList())
                .thenReturn(mockRows);
        Mockito.when((countQuery).getSingleResult())
                .thenReturn(1L);

        Page<GlobalRevisionDTO> result = service.getAllRevisions(pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("product", result.getContent().getFirst().entityName());
    }
}

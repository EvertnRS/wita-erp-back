package org.wita.erp.services.audit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.audit.SoftDeleteLog;
import org.wita.erp.domain.repositories.audit.SoftDeleteLogRepository;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;

import java.util.List;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class SoftDeleteLogServiceTest {
    @Mock
    private SoftDeleteLogRepository softDeleteLogRepository;

    @InjectMocks
    private SoftDeleteLogService softDeleteLogService;

    private Pageable pageable;
    private Page<SoftDeleteLog> fakePage;
    private SoftDeleteLogObserver fakeEvent;

    @BeforeEach
    void setup() {
        pageable = PageRequest.of(0, 10);

        SoftDeleteLog fakeLog = new SoftDeleteLog();
        fakeLog.setId(UUID.randomUUID());
        fakeLog.setEntityId("123e4567-e89b-12d3-a456-426614174000");
        fakeLog.setEntityType("CUSTOMER");
        fakeLog.setReason("Duplicated account");

        fakePage = new PageImpl<>(List.of(fakeLog));

        fakeEvent = new SoftDeleteLogObserver("123e4567-e89b-12d3-a456-426614174000", "CUSTOMER", "Duplicated account");
    }

    @Test
    @DisplayName("Deve retornar todos os logs quando o searchTerm for nulo")
    void shouldReturnAllLogsWhenSearchTermIsNull() {
        Mockito.when(softDeleteLogRepository.findAll(pageable)).thenReturn(fakePage);

        ResponseEntity<Page<SoftDeleteLog>> response = softDeleteLogService.getAllSoftDeleteLogs(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakePage, response.getBody());

        Mockito.verify(softDeleteLogRepository).findAll(pageable);
        Mockito.verify(softDeleteLogRepository, Mockito.never()).findBySearchTerm(Mockito.anyString(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar logs filtrados quando o searchTerm for informado")
    void shouldReturnFilteredLogsWhenSearchTermIsProvided() {
        Mockito.when(softDeleteLogRepository.findBySearchTerm("CUSTOMER", pageable)).thenReturn(fakePage);

        ResponseEntity<Page<SoftDeleteLog>> response = softDeleteLogService.getAllSoftDeleteLogs(pageable, "CUSTOMER");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakePage, response.getBody());

        Mockito.verify(softDeleteLogRepository).findBySearchTerm("CUSTOMER", pageable);
        Mockito.verify(softDeleteLogRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve criar e salvar um log de auditoria ao receber o evento de soft delete")
    void shouldCreateAndSaveLogOnSoftDeleteEvent() {
        softDeleteLogService.onSoftDelete(fakeEvent);

        ArgumentCaptor<SoftDeleteLog> logCaptor = ArgumentCaptor.forClass(SoftDeleteLog.class);

        Mockito.verify(softDeleteLogRepository).save(logCaptor.capture());

        SoftDeleteLog savedLog = logCaptor.getValue();

        Assertions.assertEquals(fakeEvent.entityId(), savedLog.getEntityId());
        Assertions.assertEquals(fakeEvent.entityType(), savedLog.getEntityType());
        Assertions.assertEquals(fakeEvent.reason(), savedLog.getReason());
    }
}

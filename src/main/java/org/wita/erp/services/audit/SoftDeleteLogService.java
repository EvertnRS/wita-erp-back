package org.wita.erp.services.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.audit.SoftDeleteLog;
import org.wita.erp.domain.repositories.audit.SoftDeleteLogRepository;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;

@Service
@RequiredArgsConstructor
public class SoftDeleteLogService {
private final SoftDeleteLogRepository softDeleteLogRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<SoftDeleteLog>> getAllSoftDeleteLogs(Pageable pageable, String searchTerm) {
        if (searchTerm != null) {
            return ResponseEntity.ok(softDeleteLogRepository.findBySearchTerm(searchTerm, pageable));
        } else {
            return ResponseEntity.ok(softDeleteLogRepository.findAll(pageable));
        }
    }

    @EventListener
    public void onSoftDelete(SoftDeleteLogObserver event){
        SoftDeleteLog log = new SoftDeleteLog();
        log.setEntityId(event.entityId());
        log.setEntityType(event.entityType());
        log.setReason(event.reason());
        softDeleteLogRepository.save(log);
    }
}

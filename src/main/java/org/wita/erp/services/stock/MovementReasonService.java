package org.wita.erp.services.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.dtos.CreateMovementReasonRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.MovementReasonDTO;
import org.wita.erp.domain.entities.stock.dtos.DeleteMovementReasonRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateMovementReasonRequestDTO;
import org.wita.erp.domain.entities.stock.mappers.MovementReasonMapper;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.infra.exceptions.product.CategoryException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.stock.observers.MovementReasonSoftDeleteObserver;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovementReasonService {
    private final MovementReasonRepository movementReasonRepository;
    private final MovementReasonMapper movementReasonMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<MovementReasonDTO>> getAllMovementReason(Pageable pageable, String searchTerm) {
        Page<MovementReason> movementReasonPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            movementReasonPage = movementReasonRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            movementReasonPage = movementReasonRepository.findAll(pageable);
        }

        return ResponseEntity.ok(movementReasonPage.map(movementReasonMapper::toDTO));
    }

    public ResponseEntity<MovementReasonDTO> save(CreateMovementReasonRequestDTO data) {
        if (movementReasonRepository.findByReason(data.reason()) != null) {
            throw new CategoryException("Movement Reason already exists", HttpStatus.CONFLICT);
        }

        MovementReason movementReason = new MovementReason();
        movementReason.setReason(data.reason());

        movementReasonRepository.save(movementReason);

        return ResponseEntity.ok(movementReasonMapper.toDTO(movementReason));
    }

    public ResponseEntity<MovementReasonDTO> update(UUID id, UpdateMovementReasonRequestDTO data) {
        MovementReason movementReason = movementReasonRepository.findById(id)
                .orElseThrow(() -> new MovementReasonException("MovementReason not found", HttpStatus.NOT_FOUND));

        if(data.reason() != null) {
            movementReason.setReason(data.reason());
            movementReasonRepository.save(movementReason);
        }

        return ResponseEntity.ok(movementReasonMapper.toDTO(movementReason));
    }

    public ResponseEntity<MovementReasonDTO> delete(UUID id, DeleteMovementReasonRequestDTO data) {
        MovementReason movementReason = movementReasonRepository.findById(id)
                .orElseThrow(() -> new MovementReasonException("MovementReason not found", HttpStatus.NOT_FOUND));
        movementReason.setActive(false);
        movementReasonRepository.save(movementReason);

        this.auditMovementReasonSoftDelete(id, data.reason());
        this.movementReasonCascadeDelete(id);

        return ResponseEntity.ok(movementReasonMapper.toDTO(movementReason));
    }

    @Async
    public void auditMovementReasonSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.MOVEMENT_REASON.getEntityType(), reason));
    }

    @Async
    public void movementReasonCascadeDelete(UUID id){
        publisher.publishEvent(new MovementReasonSoftDeleteObserver(id));
    }
}

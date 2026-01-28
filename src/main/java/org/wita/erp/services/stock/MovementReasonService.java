package org.wita.erp.services.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.dtos.CreateMovementReasonRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateMovementReasonRequestDTO;
import org.wita.erp.domain.repositories.stock.MovementReasonRepository;
import org.wita.erp.infra.exceptions.product.CategoryException;
import org.wita.erp.infra.exceptions.stock.MovementReasonException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovementReasonService {
    private final MovementReasonRepository movementReasonRepository;

    public ResponseEntity<Page<MovementReason>> getAllMovementReason(Pageable pageable, String searchTerm) {
        Page<MovementReason> movementReasonPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            movementReasonPage = movementReasonRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            movementReasonPage = movementReasonRepository.findAll(pageable);
        }

        return ResponseEntity.ok(movementReasonPage);
    }

    public ResponseEntity<MovementReason> save(CreateMovementReasonRequestDTO data) {
        if (movementReasonRepository.findByReason(data.reason()) != null) {
            throw new CategoryException("Movement Reason already exists", HttpStatus.CONFLICT);
        }

        MovementReason movementReason = new MovementReason();
        movementReason.setReason(data.reason());

        movementReasonRepository.save(movementReason);

        return ResponseEntity.ok(movementReason);
    }

    public ResponseEntity<MovementReason> update(UUID id, UpdateMovementReasonRequestDTO data) {
        MovementReason movementReason = movementReasonRepository.findById(id)
                .orElseThrow(() -> new MovementReasonException("MovementReason not found", HttpStatus.NOT_FOUND));

        if(data.reason() != null) {
            movementReason.setReason(data.reason());
        }

        return ResponseEntity.ok(movementReason);
    }

    public ResponseEntity<MovementReason> delete(UUID id) {
        MovementReason movementReason = movementReasonRepository.findById(id)
                .orElseThrow(() -> new MovementReasonException("MovementReason not found", HttpStatus.NOT_FOUND));
        movementReason.setActive(false);
        movementReasonRepository.save(movementReason);
        return ResponseEntity.ok(movementReason);
    }
}

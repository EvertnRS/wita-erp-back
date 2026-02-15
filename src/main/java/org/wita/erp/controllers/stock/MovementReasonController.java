package org.wita.erp.controllers.stock;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.stock.MovementReason;
import org.wita.erp.domain.entities.stock.dtos.CreateMovementReasonRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.DeleteMovementReasonRequestDTO;
import org.wita.erp.domain.entities.stock.dtos.UpdateMovementReasonRequestDTO;
import org.wita.erp.services.stock.MovementReasonService;

import java.util.UUID;

@RestController
@RequestMapping("/movement")
@RequiredArgsConstructor
public class MovementReasonController {
    private final MovementReasonService movementReasonService;

    @GetMapping
    @PreAuthorize("hasAuthority('REASON_READ')")
    public ResponseEntity<Page<MovementReason>> getAllMovementReason(@PageableDefault(size = 10, sort = "reason") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return movementReasonService.getAllMovementReason(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('REASON_CREATE')")
    public ResponseEntity<MovementReason> create(@Valid @RequestBody CreateMovementReasonRequestDTO data) {
        return movementReasonService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('REASON_UPDATE')")
    public ResponseEntity<MovementReason> update(@PathVariable UUID id, @RequestBody @Valid UpdateMovementReasonRequestDTO data) {
        return movementReasonService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REASON_DELETE')")
    public ResponseEntity<MovementReason> delete(@PathVariable UUID id, @RequestBody @Valid DeleteMovementReasonRequestDTO data) {
        return movementReasonService.delete(id, data);
    }
}

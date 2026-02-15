package org.wita.erp.controllers.transaction.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.transaction.order.dtos.DeleteReceivableRequestDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.ReceivableDTO;
import org.wita.erp.domain.entities.transaction.order.dtos.UpdateReceivableRequestDTO;
import org.wita.erp.services.transaction.order.ReceivableService;

import java.util.UUID;

@RestController
@RequestMapping("/receivable")
@RequiredArgsConstructor
public class ReceivableController {
    private final ReceivableService receivableService;

    @GetMapping
    @PreAuthorize("hasAuthority('RECEIVABLE_READ')")
    public ResponseEntity<Page<ReceivableDTO>> getAllReceivable(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return receivableService.getAllReceivable(pageable, searchTerm);
    }

    /*@PostMapping("/create")
    @PreAuthorize("hasAuthority('RECEIVABLE_CREATE')")
    public ResponseEntity<List<ReceivableDTO>> create(@Valid @RequestBody CreateReceivableRequestDTO data) {
        return receivableService.save(data);
    }*/

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('RECEIVABLE_UPDATE')")
    public ResponseEntity<ReceivableDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateReceivableRequestDTO data) {
        return receivableService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('RECEIVABLE_DELETE')")
    public ResponseEntity<ReceivableDTO> delete(@PathVariable UUID id, @RequestBody @Valid DeleteReceivableRequestDTO data) {
        return receivableService.delete(id, data);
    }
}

package org.wita.erp.controllers.transaction.purchase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.transaction.purchase.dtos.CreatePayableRequestDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.PayableDTO;
import org.wita.erp.domain.entities.transaction.purchase.dtos.UpdatePayableRequestDTO;
import org.wita.erp.services.transaction.purchase.PayableService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payable")
@RequiredArgsConstructor
public class PayableController {
    private final PayableService payableService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYABLE_READ')")
    public ResponseEntity<Page<PayableDTO>> getAllPayable(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return payableService.getAllPayable(pageable, searchTerm);
    }

    /*@PostMapping("/create")
    @PreAuthorize("hasAuthority('PAYABLE_CREATE')")
    public ResponseEntity<List<PayableDTO>> create(@Valid @RequestBody CreatePayableRequestDTO data) {
        return payableService.save(data);
    }*/

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYABLE_UPDATE')")
    public ResponseEntity<PayableDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdatePayableRequestDTO data) {
        return payableService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYABLE_DELETE')")
    public ResponseEntity<PayableDTO> delete(@PathVariable UUID id) {
        return payableService.delete(id);
    }
}

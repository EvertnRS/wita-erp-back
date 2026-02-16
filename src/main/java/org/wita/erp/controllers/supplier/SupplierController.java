package org.wita.erp.controllers.supplier;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.supplier.docs.SupplierDocs;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.supplier.dtos.CreateSupplierRequestDTO;
import org.wita.erp.domain.entities.supplier.dtos.SupplierDTO;
import org.wita.erp.domain.entities.supplier.dtos.UpdateSupplierRequestDTO;
import org.wita.erp.services.supplier.SupplierService;

import java.util.UUID;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
public class SupplierController implements SupplierDocs {
    private final SupplierService supplierService;

    @GetMapping
    @PreAuthorize("hasAuthority('SUPPLIER_READ')")
    public ResponseEntity<Page<SupplierDTO>> getAllSuppliers(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return supplierService.getAllSuppliers(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SUPPLIER_CREATE')")
    public ResponseEntity<SupplierDTO> create(@Valid @RequestBody CreateSupplierRequestDTO data) {
        return supplierService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER_UPDATE')")
    public ResponseEntity<SupplierDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateSupplierRequestDTO data) {
        return supplierService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPPLIER_DELETE')")
    public ResponseEntity<SupplierDTO> delete(@PathVariable UUID id) {
        return supplierService.delete(id);
    }
}

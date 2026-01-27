package org.wita.erp.services;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.supplier.Supplier;
import org.wita.erp.domain.supplier.dtos.CreateSupplierRequestDTO;
import org.wita.erp.domain.supplier.dtos.UpdateSupplierRequestDTO;
import org.wita.erp.domain.supplier.mappers.SupplierMapper;
import org.wita.erp.infra.exceptions.supplier.SupplierException;
import org.wita.erp.repositories.SupplierRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public ResponseEntity<Page<Supplier>> getAllSuppliers(Pageable pageable, String searchTerm) {
        Page<Supplier> supplierPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            supplierPage = supplierRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            supplierPage = supplierRepository.findAll(pageable);
        }

        return ResponseEntity.ok(supplierPage);
    }

    public ResponseEntity<Supplier> save(CreateSupplierRequestDTO data) {
        if (supplierRepository.findByCnpj(data.cnpj()) != null) {
            throw new SupplierException("Supplier already exists", HttpStatus.CONFLICT);
        }

        Supplier supplier = new Supplier();
        supplier.setName(data.name());
        supplier.setEmail(data.email());
        supplier.setAddress(data.address());
        supplier.setCnpj(data.cnpj());

        supplierRepository.save(supplier);

        return ResponseEntity.ok(supplier);
    }

    public ResponseEntity<Supplier> update(UUID id, UpdateSupplierRequestDTO data) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierException("Supplier not found", HttpStatus.NOT_FOUND));

        supplierMapper.updateSupplierFromSupplier(data, supplier);
        supplierRepository.save(supplier);

        return ResponseEntity.ok(supplier);
    }

    public ResponseEntity<Supplier> delete(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierException("Supplier not found", HttpStatus.NOT_FOUND));
        supplier.setActive(false);
        supplierRepository.save(supplier);
        return ResponseEntity.ok(supplier);
    }
}

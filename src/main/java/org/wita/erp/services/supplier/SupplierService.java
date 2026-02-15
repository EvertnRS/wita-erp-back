package org.wita.erp.services.supplier;

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
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.supplier.dtos.CreateSupplierRequestDTO;
import org.wita.erp.domain.entities.supplier.dtos.DeleteSupplierRequestDTO;
import org.wita.erp.domain.entities.supplier.dtos.UpdateSupplierRequestDTO;
import org.wita.erp.domain.entities.supplier.mappers.SupplierMapper;
import org.wita.erp.domain.repositories.supplier.SupplierRepository;
import org.wita.erp.infra.exceptions.supplier.SupplierException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.supplier.observers.SupplierSoftDeleteObserver;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
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

        supplierMapper.updateSupplierFromDTO(data, supplier);
        supplierRepository.save(supplier);

        return ResponseEntity.ok(supplier);
    }

    public ResponseEntity<Supplier> delete(UUID id, DeleteSupplierRequestDTO data) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierException("Supplier not found", HttpStatus.NOT_FOUND));
        supplier.setActive(false);
        supplierRepository.save(supplier);

        this.auditSupplierSoftDelete(id, data.reason());
        this.supplierCascadeDelete(supplier.getId());

        return ResponseEntity.ok(supplier);
    }

    @Async
    public void auditSupplierSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.SUPPLIER.getEntityType(), reason));
    }

    @Async
    public void supplierCascadeDelete(UUID id){
        publisher.publishEvent(new SupplierSoftDeleteObserver(id));
    }

}

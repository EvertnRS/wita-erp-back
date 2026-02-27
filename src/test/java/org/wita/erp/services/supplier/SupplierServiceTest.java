package org.wita.erp.services.supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.supplier.Supplier;
import org.wita.erp.domain.entities.supplier.dtos.CreateSupplierRequestDTO;
import org.wita.erp.domain.entities.supplier.dtos.DeleteSupplierRequestDTO;
import org.wita.erp.domain.entities.supplier.dtos.SupplierDTO;
import org.wita.erp.domain.entities.supplier.dtos.UpdateSupplierRequestDTO;
import org.wita.erp.domain.entities.supplier.mappers.SupplierMapper;
import org.wita.erp.domain.repositories.supplier.SupplierRepository;
import org.wita.erp.infra.exceptions.supplier.SupplierException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.supplier.observers.SupplierSoftDeleteObserver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {
    @Mock
    private SupplierRepository supplierRepository;
    @Mock
    private SupplierMapper supplierMapper;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private SupplierService supplierService;

    private UUID supplierId;
    private Supplier fakeSupplier;
    private Supplier existingSupplier;
    private SupplierDTO fakeSupplierDTO;
    private Pageable pageable;
    private Page<Supplier> fakePage;
    private CreateSupplierRequestDTO fakeCreateDTO;
    private UpdateSupplierRequestDTO fakeUpdateDTO;
    private DeleteSupplierRequestDTO fakeDeleteDTO;

    @BeforeEach
    void setup() {
        supplierId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        fakeSupplier = new Supplier();
        fakeSupplier.setId(supplierId);
        fakeSupplier.setName("Acme Corp");
        fakeSupplier.setEmail("contact@acme.com");
        fakeSupplier.setAddress("Industrial St. 100");
        fakeSupplier.setCnpj("12345678000199");
        fakeSupplier.setActive(true);

        existingSupplier = new Supplier();
        existingSupplier.setId(UUID.randomUUID());
        existingSupplier.setCnpj("99999999000199");

        fakeSupplierDTO = new SupplierDTO(supplierId, "Acme Corp", "contact@acme.com", "Industrial St. 100", "12345678000199", true);

        fakePage = new PageImpl<>(List.of(fakeSupplier));

        fakeCreateDTO = new CreateSupplierRequestDTO("Acme Corp", "contact@acme.com", "Industrial St. 100", "12345678000199");

        fakeUpdateDTO = new UpdateSupplierRequestDTO("Acme Corp Updated", "new@acme.com", "New Avenue 200", null);

        fakeDeleteDTO = new DeleteSupplierRequestDTO("Contract ended");
    }

    @Test
    @DisplayName("Deve retornar todos os fornecedores quando o searchTerm for nulo")
    void shouldReturnAllSuppliersWhenSearchTermIsNull() {
        Mockito.when(supplierRepository.findAll(pageable)).thenReturn(fakePage);
        Mockito.when(supplierMapper.toDTO(fakeSupplier)).thenReturn(fakeSupplierDTO);

        ResponseEntity<Page<SupplierDTO>> response = supplierService.getAllSuppliers(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(supplierRepository).findAll(pageable);
        Mockito.verify(supplierRepository, Mockito.never()).findBySearchTerm(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar fornecedores filtrados pelo searchTerm")
    void shouldReturnSuppliersFilteredBySearchTerm() {
        Mockito.when(supplierRepository.findBySearchTerm("acme", pageable)).thenReturn(fakePage);
        Mockito.when(supplierMapper.toDTO(fakeSupplier)).thenReturn(fakeSupplierDTO);

        ResponseEntity<Page<SupplierDTO>> response = supplierService.getAllSuppliers(pageable, "acme");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(supplierRepository).findBySearchTerm("acme", pageable);
        Mockito.verify(supplierRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve cadastrar um novo fornecedor com sucesso")
    void shouldRegisterNewSupplierSuccessfully() {
        Mockito.when(supplierRepository.findByCnpj(Mockito.anyString())).thenReturn(null);
        Mockito.when(supplierMapper.toDTO(Mockito.any(Supplier.class))).thenReturn(fakeSupplierDTO);

        ResponseEntity<SupplierDTO> response = supplierService.save(fakeCreateDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(fakeSupplierDTO, response.getBody());
        Mockito.verify(supplierRepository).save(Mockito.any(Supplier.class));
    }

    @Test
    @DisplayName("Deve lançar SupplierException ao tentar cadastrar um fornecedor com CNPJ já registrado")
    void shouldThrowSupplierExceptionWhenCnpjAlreadyRegistered() {
        Mockito.when(supplierRepository.findByCnpj(Mockito.anyString())).thenReturn(existingSupplier);

        SupplierException exception = Assertions.assertThrows(SupplierException.class, () -> supplierService.save(fakeCreateDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Supplier already exists", exception.getMessage());
        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any(Supplier.class));
    }

    @Test
    @DisplayName("Deve atualizar um fornecedor com sucesso")
    void shouldUpdateSupplierSuccessfully() {
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(supplierRepository.findByEmail(fakeUpdateDTO.email())).thenReturn(null);
        Mockito.when(supplierMapper.toDTO(fakeSupplier)).thenReturn(fakeSupplierDTO);

        ResponseEntity<SupplierDTO> response = supplierService.update(supplierId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakeSupplierDTO, response.getBody());
        Mockito.verify(supplierMapper).updateSupplierFromDTO(fakeUpdateDTO, fakeSupplier);
        Mockito.verify(supplierRepository).save(fakeSupplier);
    }

    @Test
    @DisplayName("Deve lançar SupplierException ao tentar atualizar CNPJ para um já registrado por outro fornecedor")
    void shouldThrowSupplierExceptionWhenUpdatingWithExistingCnpj() {
        UpdateSupplierRequestDTO conflictCnpjDTO = new UpdateSupplierRequestDTO("Acme Corp Updated", null, null, "99999999000199");

        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(supplierRepository.findByCnpj("99999999000199")).thenReturn(existingSupplier);

        SupplierException exception = Assertions.assertThrows(SupplierException.class, () -> supplierService.update(supplierId, conflictCnpjDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Another supplier with the same CNPJ already exists", exception.getMessage());
        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any(Supplier.class));
    }

    @Test
    @DisplayName("Deve lançar SupplierException ao tentar atualizar e-mail para um já registrado por outro fornecedor")
    void shouldThrowSupplierExceptionWhenUpdatingWithExistingEmail() {
        UpdateSupplierRequestDTO conflictEmailDTO = new UpdateSupplierRequestDTO("Acme Corp Updated", "existing@acme.com", null, null);

        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(supplierRepository.findByEmail("existing@acme.com")).thenReturn(existingSupplier);

        SupplierException exception = Assertions.assertThrows(SupplierException.class, () -> supplierService.update(supplierId, conflictEmailDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Another supplier with the same email already exists", exception.getMessage());
        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any(Supplier.class));
    }

    @Test
    @DisplayName("Deve lançar SupplierException ao tentar atualizar fornecedor inexistente")
    void shouldThrowSupplierExceptionWhenUpdatingNonExistentSupplier() {
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        SupplierException exception = Assertions.assertThrows(SupplierException.class, () -> supplierService.update(supplierId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Supplier not found", exception.getMessage());
        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any(Supplier.class));
    }

    @Test
    @DisplayName("Deve deletar (soft delete) um fornecedor com sucesso e disparar eventos")
    void shouldDeleteSupplierSuccessfully() {
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(fakeSupplier));
        Mockito.when(supplierMapper.toDTO(fakeSupplier)).thenReturn(fakeSupplierDTO);

        ResponseEntity<SupplierDTO> response = supplierService.delete(supplierId, fakeDeleteDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakeSupplierDTO, response.getBody());
        Assertions.assertFalse(fakeSupplier.getActive());

        Mockito.verify(supplierRepository).save(fakeSupplier);
        Mockito.verify(publisher).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher).publishEvent(Mockito.any(SupplierSoftDeleteObserver.class));
    }

    @Test
    @DisplayName("Deve lançar SupplierException ao tentar deletar um fornecedor inexistente")
    void shouldThrowSupplierExceptionWhenDeletingNonExistentSupplier() {
        Mockito.when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        SupplierException exception = Assertions.assertThrows(SupplierException.class, () -> supplierService.delete(supplierId, fakeDeleteDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Supplier not found", exception.getMessage());

        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any(Supplier.class));
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }

    @Test
    @DisplayName("Deve publicar evento de log de auditoria ao realizar soft delete")
    void shouldPublishAuditLogEventSuccessfully() {
        supplierService.auditSupplierSoftDelete(supplierId, "Contract ended");
        Mockito.verify(publisher).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
    }

    @Test
    @DisplayName("Deve publicar evento de exclusão em cascata do fornecedor")
    void shouldPublishCascadeDeleteEventSuccessfully() {
        supplierService.supplierCascadeDelete(supplierId);
        Mockito.verify(publisher).publishEvent(Mockito.any(SupplierSoftDeleteObserver.class));
    }
}

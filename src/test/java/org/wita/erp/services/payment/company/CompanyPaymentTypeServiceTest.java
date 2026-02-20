package org.wita.erp.services.payment.company;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.company.dtos.CompanyPaymentTypeDTO;
import org.wita.erp.domain.entities.payment.company.dtos.CreateCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.dtos.DeleteCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.dtos.UpdateCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.mappers.CompanyPaymentTypeMapper;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.repositories.payment.company.CompanyPaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.payment.PaymentTypeService;
import org.wita.erp.services.payment.company.observers.CompanyPaymentTypeSoftDeleteObserver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CompanyPaymentTypeServiceTest {
    @Mock
    private CompanyPaymentTypeMapper companyPaymentTypeMapper;
    @Mock
    private PaymentTypeService paymentTypeService;
    @Mock
    private CompanyPaymentTypeRepository companyPaymentTypeRepository;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CompanyPaymentTypeService companyPaymentTypeService;

    private UUID companyPaymentTypeId;
    private CompanyPaymentType fakeCompanyPaymentType;
    private Pageable pageable;
    private Page<CompanyPaymentType> fakePage;
    private CompanyPaymentTypeDTO fakeCompanyPaymentTypeDTO;
    private CreateCompanyPaymentTypeRequestDTO fakeCreateCompanyPaymentTypeDTO;
    private UpdateCompanyPaymentTypeRequestDTO fakeUpdateCompanyPaymentTypeDTO;

    @BeforeEach
    void setup() {
        companyPaymentTypeId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        fakeCompanyPaymentType = new CompanyPaymentType("12345678900", "1234", "0000", "1234", "Brand", 10);
        fakeCompanyPaymentType.setId(companyPaymentTypeId);

        fakePage = new PageImpl<>(List.of(fakeCompanyPaymentType));

        fakeCompanyPaymentTypeDTO = new CompanyPaymentTypeDTO(companyPaymentTypeId, true, false, "12345678900", "1234", "Brand", 10, true);
        fakeCreateCompanyPaymentTypeDTO = new CreateCompanyPaymentTypeRequestDTO(true, false, "1234", "0000", "1234", "1234", "Brand", 10);
        fakeUpdateCompanyPaymentTypeDTO = new UpdateCompanyPaymentTypeRequestDTO(true, false, "1234", "0000", "1234", "1234", "Brand", 10);

    }

    @Test
    @DisplayName("Deve retornar todos os tipos de pagamento da empresa quando o searchTerm for nulo")
    void shouldReturnAllProductsWhenSearchTermIsNull() {
        Mockito.when(companyPaymentTypeRepository.findAll(pageable)).thenReturn(fakePage);
        Mockito.when(companyPaymentTypeMapper.toDTO(fakeCompanyPaymentType)).thenReturn(fakeCompanyPaymentTypeDTO);

        ResponseEntity<Page<CompanyPaymentTypeDTO>> response = companyPaymentTypeService.getAllCompanyPaymentTypes(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(companyPaymentTypeRepository).findAll(pageable);
        Mockito.verify(companyPaymentTypeRepository, Mockito.never()).findBySearchTerm(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar tipos de pagamento da empresa filtrados pelo searchTerm")
    void shouldReturnProductsFilteredBySearchTerm() {
        Mockito.when(companyPaymentTypeRepository.findBySearchTerm(String.valueOf(companyPaymentTypeId), pageable)).thenReturn(fakePage);
        Mockito.when(companyPaymentTypeMapper.toDTO(fakeCompanyPaymentType)).thenReturn(fakeCompanyPaymentTypeDTO);

        ResponseEntity<Page<CompanyPaymentTypeDTO>> response = companyPaymentTypeService.getAllCompanyPaymentTypes(pageable, String.valueOf(companyPaymentTypeId));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(companyPaymentTypeRepository).findBySearchTerm(String.valueOf(companyPaymentTypeId), pageable);
        Mockito.verify(companyPaymentTypeRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve salvar pagamento de empresa com dados de cartão quando não for imediato e permitir parcelas")
    void shouldSaveCompanyPaymentWithCardDetails() {
        Mockito.when(paymentTypeService.save(Mockito.any(CompanyPaymentType.class), Mockito.any(CreatePaymentTypeRequestDTO.class)))
                .thenAnswer(invocation -> ResponseEntity.ok(invocation.getArgument(0)));

        Mockito.when(companyPaymentTypeMapper.toDTO(Mockito.any(CompanyPaymentType.class)))
                .thenReturn(fakeCompanyPaymentTypeDTO);

        ResponseEntity<CompanyPaymentTypeDTO> response = companyPaymentTypeService.save(fakeCreateCompanyPaymentTypeDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(fakeCompanyPaymentTypeDTO, response.getBody());
        Mockito.verify(paymentTypeService).save(Mockito.any(CompanyPaymentType.class), Mockito.any(CreatePaymentTypeRequestDTO.class));
    }

    @Test
    @DisplayName("Não deve setar dados de cartão quando o pagamento for imediato")
    void shouldNotSetCardDetailsWhenPaymentIsImmediate() {
        Mockito.when(paymentTypeService.save(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> ResponseEntity.ok(invocation.getArgument(0)));

        companyPaymentTypeService.save(fakeCreateCompanyPaymentTypeDTO);

        ArgumentCaptor<CompanyPaymentType> captor = ArgumentCaptor.forClass(CompanyPaymentType.class);
        Mockito.verify(paymentTypeService).save(captor.capture(), Mockito.any());

        CompanyPaymentType captured = captor.getValue();
        Assertions.assertNull(captured.getBrand());
        Assertions.assertNull(captured.getClosingDay());
        Assertions.assertNull(captured.getLastFourDigits());
    }

    @Test
    @DisplayName("Deve atualizar pagamento de empresa com sucesso")
    void shouldUpdateCompanyPaymentTypeSuccessfully() {
        CompanyPaymentType updatedEntity = new CompanyPaymentType();
        updatedEntity.setId(companyPaymentTypeId);
        updatedEntity.setAccountNumber("99999");

        Mockito.when(companyPaymentTypeRepository.findById(companyPaymentTypeId))
                .thenReturn(Optional.of(fakeCompanyPaymentType));
        Mockito.when(paymentTypeService.update(Mockito.eq(fakeCompanyPaymentType), Mockito.any(UpdatePaymentTypeRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(updatedEntity));
        Mockito.when(companyPaymentTypeMapper.toDTO(updatedEntity))
                .thenReturn(fakeCompanyPaymentTypeDTO);

        ResponseEntity<CompanyPaymentTypeDTO> response = companyPaymentTypeService.update(companyPaymentTypeId, fakeUpdateCompanyPaymentTypeDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakeCompanyPaymentTypeDTO, response.getBody());
        Mockito.verify(companyPaymentTypeMapper).updateCompanyPaymentTypeFromDTO(fakeUpdateCompanyPaymentTypeDTO, fakeCompanyPaymentType);
        Mockito.verify(paymentTypeService).update(Mockito.eq(fakeCompanyPaymentType), Mockito.any(UpdatePaymentTypeRequestDTO.class));
        Mockito.verify(companyPaymentTypeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o pagamento de empresa para atualizar não existe")
    void shouldThrowExceptionWhenUpdateCompanyPaymentNotFound() {
        UpdateCompanyPaymentTypeRequestDTO data = new UpdateCompanyPaymentTypeRequestDTO(
                true, true, "001", "1", "1", null, null, null
        );

        Mockito.when(companyPaymentTypeRepository.findById(companyPaymentTypeId)).thenReturn(Optional.empty());

        PaymentTypeException exception = Assertions.assertThrows(PaymentTypeException.class,
                () -> companyPaymentTypeService.update(companyPaymentTypeId, data));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(paymentTypeService, Mockito.never()).update(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve realizar soft delete de pagamento de empresa e disparar eventos")
    void shouldDeleteCompanyPaymentTypeSuccessfully() {
        Mockito.when(companyPaymentTypeRepository.findById(companyPaymentTypeId)).thenReturn(Optional.of(fakeCompanyPaymentType));
        Mockito.when(paymentTypeService.delete(companyPaymentTypeId)).thenReturn(ResponseEntity.ok(fakeCompanyPaymentType));
        Mockito.when(companyPaymentTypeMapper.toDTO(fakeCompanyPaymentType)).thenReturn(fakeCompanyPaymentTypeDTO);

        ResponseEntity<CompanyPaymentTypeDTO> response = companyPaymentTypeService.delete(companyPaymentTypeId, new DeleteCompanyPaymentTypeRequestDTO("Reason for deletion"));

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

        Mockito.verify(paymentTypeService).delete(companyPaymentTypeId);

        Mockito.verify(publisher).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher).publishEvent(Mockito.any(CompanyPaymentTypeSoftDeleteObserver.class));
        Mockito.verify(companyPaymentTypeMapper).toDTO(fakeCompanyPaymentType);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar um pagamento de empresa que não existe")
    void shouldThrowExceptionWhenDeletingNonExistentCompanyPayment() {
        DeleteCompanyPaymentTypeRequestDTO deleteData = new DeleteCompanyPaymentTypeRequestDTO("Reason");
        Mockito.when(companyPaymentTypeRepository.findById(companyPaymentTypeId)).thenReturn(Optional.empty());

        PaymentTypeException exception = Assertions.assertThrows(PaymentTypeException.class,
                () -> companyPaymentTypeService.delete(companyPaymentTypeId, deleteData));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(paymentTypeService, Mockito.never()).delete(Mockito.any());
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }
}
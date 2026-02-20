package org.wita.erp.services.payment.customer;

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
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.customer.dto.CreateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.CustomerPaymentTypeDTO;
import org.wita.erp.domain.entities.payment.customer.dto.DeleteCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.UpdateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.mappers.CustomerPaymentTypeMapper;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.repositories.customer.CustomerRepository;
import org.wita.erp.domain.repositories.payment.customer.CustomerPaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.customer.observers.CustomerSoftDeleteObserver;
import org.wita.erp.services.payment.PaymentTypeService;
import org.wita.erp.services.payment.customer.observers.CustomerPaymentTypeSoftDeleteObserver;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CustomerPaymentTypeServiceTest {
    @Mock
    private CustomerPaymentTypeMapper customerPaymentTypeMapper;
    @Mock
    private PaymentTypeService paymentTypeService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerPaymentTypeRepository customerPaymentTypeRepository;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CustomerPaymentTypeService customerPaymentTypeService;

    private UUID customerPaymentTypeId, customerId;
    private CustomerPaymentType fakeCustomerPaymentType;
    private Pageable pageable;
    private Page<CustomerPaymentType> fakePage;
    private CustomerPaymentTypeDTO fakeCustomerPaymentTypeDTO;
    private CreateCustomerPaymentTypeRequestDTO fakeCreateCustomerPaymentTypeDTO;
    private UpdateCustomerPaymentTypeRequestDTO fakeUpdateCustomerPaymentTypeDTO;

    @BeforeEach
    void setup() {
        customerPaymentTypeId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);


        Customer fakeCustomer = new Customer();
        fakeCustomer.setId(customerId);

        fakeCustomerPaymentType = new CustomerPaymentType();
        fakeCustomerPaymentType.setId(customerPaymentTypeId);
        fakeCustomerPaymentType.setCustomer(fakeCustomer);

        fakePage = new PageImpl<>(List.of(fakeCustomerPaymentType));

        fakeCustomerPaymentTypeDTO = new CustomerPaymentTypeDTO(customerPaymentTypeId, true, false, true, null, true);
        fakeCreateCustomerPaymentTypeDTO = new CreateCustomerPaymentTypeRequestDTO(true, false, true, customerId);
        fakeUpdateCustomerPaymentTypeDTO = new UpdateCustomerPaymentTypeRequestDTO(true, false, true);

    }

    @Test
    @DisplayName("Deve retornar todos os tipos de pagamento do cliente quando o searchTerm for nulo")
    void shouldReturnAllProductsWhenSearchTermIsNull() {
        Mockito.when(customerPaymentTypeRepository.findAll(pageable)).thenReturn(fakePage);
        Mockito.when(customerPaymentTypeMapper.toDTO(fakeCustomerPaymentType)).thenReturn(fakeCustomerPaymentTypeDTO);

        ResponseEntity<Page<CustomerPaymentTypeDTO>> response = customerPaymentTypeService.getAllCustomerPaymentTypes(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(customerPaymentTypeRepository).findAll(pageable);
        Mockito.verify(customerPaymentTypeRepository, Mockito.never()).findBySearchTerm(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar tipos de pagamento do cliente filtrados pelo searchTerm")
    void shouldReturnProductsFilteredBySearchTerm() {
        Mockito.when(customerPaymentTypeRepository.findBySearchTerm("John Doe", pageable)).thenReturn(fakePage);
        Mockito.when(customerPaymentTypeMapper.toDTO(fakeCustomerPaymentType)).thenReturn(fakeCustomerPaymentTypeDTO);

        ResponseEntity<Page<CustomerPaymentTypeDTO>> response = customerPaymentTypeService.getAllCustomerPaymentTypes(pageable, "John Doe");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(customerPaymentTypeRepository).findBySearchTerm("John Doe", pageable);
        Mockito.verify(customerPaymentTypeRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve salvar um tipo de pagamento de cliente com sucesso")
    void shouldSaveCustomerPaymentTypeSuccessfully() {
        Customer fakeCustomer = new Customer();
        fakeCustomer.setId(customerId);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(fakeCustomer));
        Mockito.when(paymentTypeService.save(Mockito.any(CustomerPaymentType.class), Mockito.any(CreatePaymentTypeRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(fakeCustomerPaymentType));

        Mockito.when(customerPaymentTypeMapper.toDTO(fakeCustomerPaymentType)).thenReturn(fakeCustomerPaymentTypeDTO);

        ResponseEntity<CustomerPaymentTypeDTO> response = customerPaymentTypeService.save(fakeCreateCustomerPaymentTypeDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(fakeCustomerPaymentTypeDTO, response.getBody());

        Mockito.verify(paymentTypeService).save(Mockito.any(CustomerPaymentType.class), Mockito.any(CreatePaymentTypeRequestDTO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente não for encontrado")
    void shouldThrowExceptionWhenCustomerNotFound() {
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        PaymentTypeException exception = Assertions.assertThrows(PaymentTypeException.class,
                () -> customerPaymentTypeService.save(fakeCreateCustomerPaymentTypeDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Customer not found", exception.getMessage());

        Mockito.verify(paymentTypeService, Mockito.never()).save(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve atualizar um tipo de pagamento de cliente com sucesso")
    void shouldUpdateCustomerPaymentTypeSuccessfully() {
        CustomerPaymentType updatedEntity = new CustomerPaymentType();
        updatedEntity.setId(customerPaymentTypeId);
        updatedEntity.setSupportsRefunds(true);

        Mockito.when(customerPaymentTypeRepository.findById(customerPaymentTypeId))
                .thenReturn(Optional.of(fakeCustomerPaymentType));
        Mockito.when(paymentTypeService.update(Mockito.eq(fakeCustomerPaymentType), Mockito.any(UpdatePaymentTypeRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(updatedEntity));
        Mockito.when(customerPaymentTypeMapper.toDTO(updatedEntity))
                .thenReturn(fakeCustomerPaymentTypeDTO);

        ResponseEntity<CustomerPaymentTypeDTO> response = customerPaymentTypeService.update(customerPaymentTypeId, fakeUpdateCustomerPaymentTypeDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakeCustomerPaymentTypeDTO, response.getBody());
        Mockito.verify(customerPaymentTypeMapper).updateCustomerPaymentTypeFromDTO(fakeUpdateCustomerPaymentTypeDTO, fakeCustomerPaymentType);
        Mockito.verify(paymentTypeService).update(Mockito.eq(fakeCustomerPaymentType), Mockito.any(UpdatePaymentTypeRequestDTO.class));
        Mockito.verify(customerPaymentTypeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar tipo de pagamento inexistente")
    void shouldThrowExceptionWhenUpdateNotFound() {
        Mockito.when(customerPaymentTypeRepository.findById(customerPaymentTypeId)).thenReturn(Optional.empty());

        PaymentTypeException exception = Assertions.assertThrows(PaymentTypeException.class,
                () -> customerPaymentTypeService.update(customerPaymentTypeId, fakeUpdateCustomerPaymentTypeDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Mockito.verify(paymentTypeService, Mockito.never()).update(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve realizar soft delete de pagamento de cliente e disparar eventos")
    void shouldDeleteCustomerPaymentTypeSuccessfully() {
        DeleteCustomerPaymentTypeRequestDTO deleteData = new DeleteCustomerPaymentTypeRequestDTO("Cancelamento");

        CustomerPaymentTypeDTO expectedDTO = new CustomerPaymentTypeDTO(customerPaymentTypeId, true, true, false, null, false);

        Mockito.when(customerPaymentTypeRepository.findById(customerPaymentTypeId)).thenReturn(Optional.of(fakeCustomerPaymentType));
        Mockito.when(paymentTypeService.delete(customerPaymentTypeId)).thenReturn(ResponseEntity.ok(fakeCustomerPaymentType));
        Mockito.when(customerPaymentTypeMapper.toDTO(fakeCustomerPaymentType)).thenReturn(expectedDTO);

        ResponseEntity<CustomerPaymentTypeDTO> response = customerPaymentTypeService.delete(customerPaymentTypeId, deleteData);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(publisher).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher).publishEvent(Mockito.any(CustomerPaymentTypeSoftDeleteObserver.class));
        Mockito.verify(paymentTypeService).delete(customerPaymentTypeId);
    }

    @Test
    @DisplayName("Deve processar deleção em cascata quando um cliente for deletado")
    void shouldHandleCustomerSoftDeleteEvent() {
        UUID paymentId1 = UUID.randomUUID();
        UUID paymentId2 = UUID.randomUUID();

        Mockito.when(customerPaymentTypeRepository.cascadeDeleteFromCustomer(customerId))
                .thenReturn(List.of(paymentId1, paymentId2));

        customerPaymentTypeService.onCustomerSoftDelete(new CustomerSoftDeleteObserver(customerId));

        Mockito.verify(publisher, Mockito.times(2)).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher, Mockito.times(2)).publishEvent(Mockito.any(CustomerPaymentTypeSoftDeleteObserver.class));
    }

    @Test
    @DisplayName("Deve publicar evento de log de auditoria de pagamento de cliente")
    void shouldPublishAuditLog() {
        customerPaymentTypeService.auditCustomerPaymentTypeSoftDelete(customerPaymentTypeId, "Teste");

        ArgumentCaptor<SoftDeleteLogObserver> captor = ArgumentCaptor.forClass(SoftDeleteLogObserver.class);
        Mockito.verify(publisher).publishEvent(captor.capture());

        Assertions.assertEquals("payment_type", captor.getValue().entityType());
        Assertions.assertEquals(customerPaymentTypeId.toString(), captor.getValue().entityId());
    }
}
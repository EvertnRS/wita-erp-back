package org.wita.erp.services.customer;

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
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.customer.dtos.CreateCustomerRequestDTO;
import org.wita.erp.domain.entities.customer.dtos.CustomerDTO;
import org.wita.erp.domain.entities.customer.dtos.DeleteCustomerRequestDTO;
import org.wita.erp.domain.entities.customer.dtos.UpdateCustomerRequestDTO;
import org.wita.erp.domain.entities.customer.mappers.CustomerMapper;
import org.wita.erp.domain.repositories.customer.CustomerRepository;
import org.wita.erp.infra.exceptions.customer.CustomerException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.customer.observers.CustomerSoftDeleteObserver;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks
    private CustomerService customerService;

    private UUID customerId;
    private Customer fakeCustomer;
    private Customer existingCustomer;
    private CustomerDTO fakeCustomerDTO;
    private Pageable pageable;
    private Page<Customer> fakePage;
    private CreateCustomerRequestDTO fakeCreateDTO;
    private UpdateCustomerRequestDTO fakeUpdateDTO;
    private DeleteCustomerRequestDTO fakeDeleteDTO;

    @BeforeEach
    void setup() {
        customerId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        fakeCustomer = new Customer(customerId, "John Doe", "john@example.com", "Address", "12345678900", LocalDate.of(1990, 1, 1), true, null);
        existingCustomer = new Customer(UUID.randomUUID(), "Existing User", "existing@example.com", "Other Address", "99999999999", LocalDate.of(1985, 5, 5), true, null);

        fakeCustomerDTO = new CustomerDTO(customerId, "John Doe", "john@example.com", "Address", "12345678900", LocalDate.of(1990, 1, 1), true);

        fakePage = new PageImpl<>(List.of(fakeCustomer));

        fakeCreateDTO = new CreateCustomerRequestDTO("John Doe", "john@example.com", "Address", "12345678900", LocalDate.of(1990, 1, 1));

        fakeUpdateDTO = new UpdateCustomerRequestDTO("John Updated", "john.updated@example.com", "New Address", "12345678900", LocalDate.of(1990, 1, 1));

        fakeDeleteDTO = new DeleteCustomerRequestDTO("Reason for deletion");
    }

    @Test
    @DisplayName("Deve retornar todos os clientes quando o searchTerm for nulo")
    void shouldReturnAllCustomersWhenSearchTermIsNull() {
        Mockito.when(customerRepository.findAll(pageable)).thenReturn(fakePage);
        Mockito.when(customerMapper.toDTO(fakeCustomer)).thenReturn(fakeCustomerDTO);

        ResponseEntity<Page<CustomerDTO>> response = customerService.getAllCustomers(pageable, null);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(customerRepository).findAll(pageable);
        Mockito.verify(customerRepository, Mockito.never()).findBySearchTerm(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("Deve retornar clientes filtrados pelo searchTerm")
    void shouldReturnCustomersFilteredBySearchTerm() {
        Mockito.when(customerRepository.findBySearchTerm("john", pageable)).thenReturn(fakePage);
        Mockito.when(customerMapper.toDTO(fakeCustomer)).thenReturn(fakeCustomerDTO);

        ResponseEntity<Page<CustomerDTO>> response = customerService.getAllCustomers(pageable, "john");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().getTotalElements());
        Mockito.verify(customerRepository).findBySearchTerm("john", pageable);
        Mockito.verify(customerRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    @DisplayName("Deve cadastrar um novo cliente com sucesso")
    void shouldRegisterNewCustomerSuccessfully() {
        Mockito.when(customerRepository.findByEmail(Mockito.anyString())).thenReturn(null);
        Mockito.when(customerRepository.findByCpf(Mockito.anyString())).thenReturn(null);
        Mockito.when(customerMapper.toDTO(Mockito.any(Customer.class))).thenReturn(fakeCustomerDTO);

        ResponseEntity<CustomerDTO> response = customerService.save(fakeCreateDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(fakeCustomerDTO, response.getBody());
        Mockito.verify(customerRepository).save(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar CustomerException ao tentar cadastrar um cliente com email já registrado")
    void shouldThrowCustomerExceptionWhenEmailAlreadyRegistered() {
        Mockito.when(customerRepository.findByEmail(Mockito.anyString())).thenReturn(existingCustomer);

        CustomerException exception = Assertions.assertThrows(CustomerException.class, () -> customerService.save(fakeCreateDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Customer already exists", exception.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar CustomerException ao tentar cadastrar um cliente com CPF já registrado")
    void shouldThrowCustomerExceptionWhenCpfAlreadyRegistered() {
        Mockito.when(customerRepository.findByEmail(Mockito.anyString())).thenReturn(null);
        Mockito.when(customerRepository.findByCpf(Mockito.anyString())).thenReturn(existingCustomer);

        CustomerException exception = Assertions.assertThrows(CustomerException.class, () -> customerService.save(fakeCreateDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Customer already exists", exception.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void shouldUpdateCustomerSuccessfully() {
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(fakeCustomer));
        Mockito.when(customerRepository.findByEmail(fakeUpdateDTO.email())).thenReturn(null);
        Mockito.when(customerRepository.findByCpf(fakeUpdateDTO.cpf())).thenReturn(null);
        Mockito.when(customerMapper.toDTO(fakeCustomer)).thenReturn(fakeCustomerDTO);

        ResponseEntity<CustomerDTO> response = customerService.update(customerId, fakeUpdateDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakeCustomerDTO, response.getBody());
        Mockito.verify(customerMapper).updateCustomerFromDTO(fakeUpdateDTO, fakeCustomer);
        Mockito.verify(customerRepository).save(fakeCustomer);
    }

    @Test
    @DisplayName("Deve lançar CustomerException ao tentar atualizar cliente inexistente")
    void shouldThrowCustomerExceptionWhenUpdatingNonExistentCustomer() {
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomerException exception = Assertions.assertThrows(CustomerException.class, () -> customerService.update(customerId, fakeUpdateDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Customer not found", exception.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar CustomerException ao tentar atualizar email para um já registrado")
    void shouldThrowCustomerExceptionWhenUpdatingWithExistingEmail() {
        UpdateCustomerRequestDTO conflictEmailDTO = new UpdateCustomerRequestDTO("John Updated", "existing@example.com", null, null, null);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(fakeCustomer));
        Mockito.when(customerRepository.findByEmail("existing@example.com")).thenReturn(existingCustomer);

        CustomerException exception = Assertions.assertThrows(CustomerException.class, () -> customerService.update(customerId, conflictEmailDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Email already registered", exception.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve lançar CustomerException ao tentar atualizar CPF para um já registrado")
    void shouldThrowCustomerExceptionWhenUpdatingWithExistingCpf() {
        UpdateCustomerRequestDTO conflictCpfDTO = new UpdateCustomerRequestDTO("John Updated", null, null, "99999999999", null);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(fakeCustomer));
        Mockito.when(customerRepository.findByCpf("99999999999")).thenReturn(existingCustomer);

        CustomerException exception = Assertions.assertThrows(CustomerException.class, () -> customerService.update(customerId, conflictCpfDTO));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        Assertions.assertEquals("Cpf already registered", exception.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
    }

    @Test
    @DisplayName("Deve deletar (soft delete) um cliente com sucesso e disparar eventos")
    void shouldDeleteCustomerSuccessfully() {
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(fakeCustomer));
        Mockito.when(customerMapper.toDTO(fakeCustomer)).thenReturn(fakeCustomerDTO);

        ResponseEntity<CustomerDTO> response = customerService.delete(customerId, fakeDeleteDTO);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakeCustomerDTO, response.getBody());
        Assertions.assertFalse(fakeCustomer.getActive());

        Mockito.verify(customerRepository).save(fakeCustomer);
        Mockito.verify(publisher).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
        Mockito.verify(publisher).publishEvent(Mockito.any(CustomerSoftDeleteObserver.class));
    }

    @Test
    @DisplayName("Deve lançar CustomerException ao tentar deletar um cliente inexistente")
    void shouldThrowCustomerExceptionWhenDeletingNonExistentCustomer() {
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomerException exception = Assertions.assertThrows(CustomerException.class, () -> customerService.delete(customerId, fakeDeleteDTO));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Customer not found", exception.getMessage());

        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any(Customer.class));
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }

    @Test
    @DisplayName("Deve publicar evento de log de auditoria ao realizar soft delete")
    void shouldPublishAuditLogEventSuccessfully() {
        customerService.auditCustomerSoftDelete(customerId, "Reason for deletion");
        Mockito.verify(publisher).publishEvent(Mockito.any(SoftDeleteLogObserver.class));
    }

    @Test
    @DisplayName("Deve publicar evento de exclusão em cascata do cliente")
    void shouldPublishCascadeDeleteEventSuccessfully() {
        customerService.customerCascadeDelete(customerId);
        Mockito.verify(publisher).publishEvent(Mockito.any(CustomerSoftDeleteObserver.class));
    }
}

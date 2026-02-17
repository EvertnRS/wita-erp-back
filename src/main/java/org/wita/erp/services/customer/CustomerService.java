package org.wita.erp.services.customer;

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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(Pageable pageable, String searchTerm) {
        Page<Customer> customerPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            customerPage = customerRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        return ResponseEntity.ok(customerPage.map(customerMapper::toDTO));
    }

    public ResponseEntity<CustomerDTO> save(CreateCustomerRequestDTO data) {
        if (customerRepository.findByEmail(data.name()) != null || customerRepository.findByCpf(data.cpf()) != null) {
            throw new CustomerException("Customer already exists", HttpStatus.CONFLICT);
        }

        Customer customer = new Customer();
        customer.setName(data.name());
        customer.setCpf(data.cpf());
        customer.setEmail(data.email());
        customer.setAddress(data.address());
        customer.setBirthDate(data.birthDate());

        customerRepository.save(customer);

        return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toDTO(customer));
    }

    public ResponseEntity<CustomerDTO> update(UUID id, UpdateCustomerRequestDTO data) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerException("Customer not found", HttpStatus.NOT_FOUND));

        if (data.email() != null && customerRepository.findByEmail(data.email()) != null) {
            throw new CustomerException("Email already registered", HttpStatus.CONFLICT);
        }
        if (data.cpf() != null && customerRepository.findByCpf(data.cpf()) != null) {
            throw new CustomerException("Cpf already registered", HttpStatus.CONFLICT);
        }

        customerMapper.updateCustomerFromDTO(data, customer);
        customerRepository.save(customer);

        return ResponseEntity.ok(customerMapper.toDTO(customer));
    }

    public ResponseEntity<CustomerDTO> delete(UUID id, DeleteCustomerRequestDTO data) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerException("Customer not found", HttpStatus.NOT_FOUND));
        customer.setActive(false);
        customerRepository.save(customer);

        this.auditCustomerSoftDelete(id, data.reason());
        this.customerCascadeDelete(id);

        return ResponseEntity.ok(customerMapper.toDTO(customer));
    }

    @Async
    public void auditCustomerSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.CUSTOMER.getEntityType(), reason));
    }

    @Async
    public void customerCascadeDelete(UUID id){
        publisher.publishEvent(new CustomerSoftDeleteObserver(id));
    }
}

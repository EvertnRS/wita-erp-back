package org.wita.erp.services.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.customer.dtos.CreateCustomerRequestDTO;
import org.wita.erp.domain.entities.customer.dtos.UpdateCustomerRequestDTO;
import org.wita.erp.domain.entities.customer.mappers.CustomerMapper;
import org.wita.erp.infra.exceptions.customer.CustomerException;
import org.wita.erp.domain.repositories.customer.CustomerRepository;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public ResponseEntity<Page<Customer>> getAllCustomers(Pageable pageable, String searchTerm) {
        Page<Customer> customerPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            customerPage = customerRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            customerPage = customerRepository.findAll(pageable);
        }

        return ResponseEntity.ok(customerPage);
    }

    public ResponseEntity<Customer> save(CreateCustomerRequestDTO data) {
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

        return ResponseEntity.ok(customer);
    }

    public ResponseEntity<Customer> update(UUID id, UpdateCustomerRequestDTO data) {
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

        return ResponseEntity.ok(customer);
    }

    public ResponseEntity<Customer> delete(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerException("Customer not found", HttpStatus.NOT_FOUND));
        customer.setActive(false);
        customerRepository.save(customer);
        return ResponseEntity.ok(customer);
    }
}

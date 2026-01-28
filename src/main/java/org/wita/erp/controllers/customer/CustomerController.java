package org.wita.erp.controllers.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.customer.dtos.CreateCustomerRequestDTO;
import org.wita.erp.domain.entities.customer.dtos.UpdateCustomerRequestDTO;
import org.wita.erp.services.customer.CustomerService;


import java.util.UUID;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_READ')")
    public ResponseEntity<Page<Customer>> getAllUsers(@PageableDefault(size = 10, sort = "name") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return customerService.getAllCustomers(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER_CREATE')")
    public ResponseEntity<Customer> create(@Valid @RequestBody CreateCustomerRequestDTO data) {
        return customerService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
    public ResponseEntity<Customer> update(@PathVariable UUID id, @RequestBody @Valid UpdateCustomerRequestDTO data) {
        return customerService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_DELETE')")
    public ResponseEntity<Customer> delete(@PathVariable UUID id) {
        return customerService.delete(id);
    }
}

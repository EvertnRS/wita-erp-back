package org.wita.erp.controllers.paymentType.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.controllers.paymentType.customer.docs.CustomerPaymentTypeDocs;
import org.wita.erp.domain.entities.paymentType.customer.dto.CreateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.paymentType.customer.dto.CustomerPaymentTypeDTO;
import org.wita.erp.domain.entities.paymentType.customer.dto.DeleteCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.paymentType.customer.dto.UpdateCustomerPaymentTypeRequestDTO;
import org.wita.erp.services.paymentType.customer.CustomerPaymentTypeService;

import java.util.UUID;

@RestController
@RequestMapping("/customer/payment")
@RequiredArgsConstructor
public class CustomerPaymentTypeController implements CustomerPaymentTypeDocs {
    private final CustomerPaymentTypeService customerPaymentService;

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_READ')")
    public ResponseEntity<Page<CustomerPaymentTypeDTO>> getAllCustomerPaymentTypes(@PageableDefault(size = 10, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String searchTerm) {
        return customerPaymentService.getAllCustomerPaymentTypes(pageable, searchTerm);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_CREATE')")
    public ResponseEntity<CustomerPaymentTypeDTO> create(@Valid @RequestBody CreateCustomerPaymentTypeRequestDTO data) {
        return customerPaymentService.save(data);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_UPDATE')")
    public ResponseEntity<CustomerPaymentTypeDTO> update(@PathVariable UUID id, @RequestBody @Valid UpdateCustomerPaymentTypeRequestDTO data) {
        return customerPaymentService.update(id, data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_PAYMENT_DELETE')")
    public ResponseEntity<CustomerPaymentTypeDTO> delete(@PathVariable UUID id, @RequestBody @Valid DeleteCustomerPaymentTypeRequestDTO data) {
        return customerPaymentService.delete(id, data);
    }
}

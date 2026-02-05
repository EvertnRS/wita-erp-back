package org.wita.erp.services.payment.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.customer.dto.CreateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.mappers.CustomerPaymentTypeMapper;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.UpdateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;
import org.wita.erp.domain.repositories.payment.customer.CustomerPaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.services.payment.PaymentTypeService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerPaymentTypeService {
    private final CustomerPaymentTypeMapper customerPaymentTypeMapper;
    private final PaymentTypeService paymentTypeService;
    private final CustomerPaymentTypeRepository customerPaymentTypeRepository;

    public ResponseEntity<Page<CustomerPaymentType>> getAllCustomerPaymentTypes(Pageable pageable, String searchTerm) {
        Page<CustomerPaymentType> customerPaymentTypePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            customerPaymentTypePage = customerPaymentTypeRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            customerPaymentTypePage = customerPaymentTypeRepository.findAll(pageable);
        }

        return ResponseEntity.ok(customerPaymentTypePage);
    }

    public ResponseEntity<PaymentType> save(CreateCustomerPaymentTypeRequestDTO data) {
        CustomerPaymentType customerPaymentType = new CustomerPaymentType();
        customerPaymentType.setSupportsRefunds(data.supportsRefunds());

        ResponseEntity<PaymentType> response = paymentTypeService.save(customerPaymentType, new CreatePaymentTypeRequestDTO(
                data.paymentMethod(),
                data.isImmediate(),
                data.allowsInstallments(),
                data.maxInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    public ResponseEntity<PaymentType> update(UUID id, UpdateCustomerPaymentTypeRequestDTO data) {
        CustomerPaymentType customerPaymentType = customerPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        customerPaymentTypeMapper.updateCustomerPaymentTypeFromDTO(data, customerPaymentType);

        ResponseEntity<PaymentType> response = paymentTypeService.update(customerPaymentType, new UpdatePaymentTypeRequestDTO(
                data.paymentMethod(),
                data.isImmediate(),
                data.allowsInstallments(),
                data.maxInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    public ResponseEntity<PaymentType> delete(UUID id) {
        CustomerPaymentType customerPaymentType = customerPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        ResponseEntity<PaymentType> response = paymentTypeService.delete(customerPaymentType.getId());

        return ResponseEntity.ok(response.getBody());
    }
}

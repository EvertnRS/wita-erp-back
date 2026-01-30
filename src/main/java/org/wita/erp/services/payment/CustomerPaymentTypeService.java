package org.wita.erp.services.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.CreateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerPaymentTypeService {
    private final PaymentTypeService paymentTypeService;
    private final PaymentTypeRepository paymentTypeRepository;

    public ResponseEntity<Page<PaymentType>> getAllCustomerPaymentTypes(Pageable pageable, String searchTerm) {
        Page<PaymentType> customerPaymentTypePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            customerPaymentTypePage = paymentTypeRepository.findCustomerPaymentTypesBySearchTerm(searchTerm, pageable);
        } else {
            customerPaymentTypePage = paymentTypeRepository.findAllCustomerPaymentTypes(pageable);
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
        CustomerPaymentType customerPaymentType = paymentTypeRepository.findCustomerPaymentTypeById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        data.applyTo(customerPaymentType);

        ResponseEntity<PaymentType> response = paymentTypeService.update(customerPaymentType, new UpdatePaymentTypeRequestDTO(
                data.paymentMethod(),
                data.isImmediate(),
                data.allowsInstallments(),
                data.maxInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    public ResponseEntity<PaymentType> delete(UUID id) {
        CustomerPaymentType customerPaymentType = paymentTypeRepository.findCustomerPaymentTypeById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        ResponseEntity<PaymentType> response = paymentTypeService.delete(customerPaymentType.getId());

        return ResponseEntity.ok(response.getBody());
    }
}

package org.wita.erp.services.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.*;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyPaymentTypeService {
    private final PaymentTypeService paymentTypeService;
    private final PaymentTypeRepository paymentTypeRepository;

    public ResponseEntity<Page<PaymentType>> getAllCompanyPaymentTypes(Pageable pageable, String searchTerm) {
        Page<PaymentType> companyPaymentTypePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            companyPaymentTypePage = paymentTypeRepository.findCompanyPaymentTypesBySearchTerm(searchTerm, pageable);
        } else {
            companyPaymentTypePage = paymentTypeRepository.findAllCompanyPaymentTypes(pageable);
        }

        return ResponseEntity.ok(companyPaymentTypePage);
    }

    public ResponseEntity<PaymentType> save(CreateCompanyPaymentTypeRequestDTO data) {
        CompanyPaymentType companyPaymentType = new CompanyPaymentType();
        companyPaymentType.setBankCode(data.bankCode());
        companyPaymentType.setAgencyNumber(data.agencyNumber());
        companyPaymentType.setAccountNumber(data.accountNumber());
        companyPaymentType.setLastFourDigits(data.lastFourDigits());
        companyPaymentType.setBrand(data.brand());
        companyPaymentType.setClosingDay(data.closingDay());

        ResponseEntity<PaymentType> response = paymentTypeService.save(companyPaymentType, new CreatePaymentTypeRequestDTO(
                data.paymentMethod(),
                data.isImmediate(),
                data.allowsInstallments(),
                data.maxInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    public ResponseEntity<PaymentType> update(UUID id, UpdateCompanyPaymentTypeRequestDTO data) {
        CompanyPaymentType companyPaymentType = paymentTypeRepository.findCompanyPaymentTypeById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        data.applyTo(companyPaymentType);

        ResponseEntity<PaymentType> response = paymentTypeService.update(companyPaymentType, new UpdatePaymentTypeRequestDTO(
                data.paymentMethod(),
                data.isImmediate(),
                data.allowsInstallments(),
                data.maxInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    public ResponseEntity<PaymentType> delete(UUID id) {
        CompanyPaymentType companyPaymentType = paymentTypeRepository.findCompanyPaymentTypeById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        ResponseEntity<PaymentType> response = paymentTypeService.delete(companyPaymentType.getId());

        return ResponseEntity.ok(response.getBody());
    }
}

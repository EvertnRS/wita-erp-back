package org.wita.erp.services.payment.company;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.company.dtos.CreateCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.dtos.UpdateCompanyPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.company.mappers.CompanyPaymentTypeMapper;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.repositories.payment.company.CompanyPaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.services.payment.PaymentTypeService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyPaymentTypeService {
    private final CompanyPaymentTypeMapper companyPaymentTypeMapper;
    private final PaymentTypeService paymentTypeService;
    private final CompanyPaymentTypeRepository companyPaymentTypeRepository;

    public ResponseEntity<Page<CompanyPaymentType>> getAllCompanyPaymentTypes(Pageable pageable) {
        Page<CompanyPaymentType> companyPaymentTypePage = companyPaymentTypeRepository.findAll(pageable);

        return ResponseEntity.ok(companyPaymentTypePage);
    }

    public ResponseEntity<PaymentType> save(CreateCompanyPaymentTypeRequestDTO data) {
        CompanyPaymentType companyPaymentType = getCompanyPaymentType(data);

        ResponseEntity<PaymentType> response = paymentTypeService.save(companyPaymentType, new CreatePaymentTypeRequestDTO(

                data.isImmediate(),
                data.allowsInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    private static CompanyPaymentType getCompanyPaymentType(CreateCompanyPaymentTypeRequestDTO data) {
        CompanyPaymentType companyPaymentType = new CompanyPaymentType();
        companyPaymentType.setBankCode(data.bankCode());
        companyPaymentType.setAgencyNumber(data.agencyNumber());
        companyPaymentType.setAccountNumber(data.accountNumber());

        if(data.allowsInstallments() && !data.isImmediate()){
            companyPaymentType.setLastFourDigits(data.lastFourDigits());
            companyPaymentType.setBrand(data.brand());
            companyPaymentType.setClosingDay(data.closingDay());
        }
        return companyPaymentType;
    }

    public ResponseEntity<PaymentType> update(UUID id, UpdateCompanyPaymentTypeRequestDTO data) {
        CompanyPaymentType companyPaymentType = companyPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        companyPaymentTypeMapper.updateCompanyPaymentTypeFromDTO(data, companyPaymentType);

        ResponseEntity<PaymentType> response = paymentTypeService.update(companyPaymentType, new UpdatePaymentTypeRequestDTO(

                data.isImmediate(),
                data.allowsInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    public ResponseEntity<PaymentType> delete(UUID id) {
        CompanyPaymentType companyPaymentType = companyPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        ResponseEntity<PaymentType> response = paymentTypeService.delete(companyPaymentType.getId());

        return ResponseEntity.ok(response.getBody());
    }
}

package org.wita.erp.services.payment.company;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.company.CompanyPaymentType;
import org.wita.erp.domain.entities.payment.company.dtos.CompanyPaymentTypeDTO;
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

    @Transactional(readOnly = true)
    public ResponseEntity<Page<CompanyPaymentTypeDTO>> getAllCompanyPaymentTypes(Pageable pageable) {
        Page<CompanyPaymentType> companyPaymentTypePage = companyPaymentTypeRepository.findAll(pageable);

        return ResponseEntity.ok(companyPaymentTypePage.map(companyPaymentTypeMapper::toDTO));
    }

    public ResponseEntity<CompanyPaymentTypeDTO> save(CreateCompanyPaymentTypeRequestDTO data) {
        CompanyPaymentType companyPaymentType = getCompanyPaymentType(data);

        ResponseEntity<PaymentType> response = paymentTypeService.save(companyPaymentType, new CreatePaymentTypeRequestDTO(

                data.isImmediate(),
                data.allowsInstallments()
        ));

        CompanyPaymentType savedEntity = (CompanyPaymentType) response.getBody();

        return ResponseEntity.status(HttpStatus.CREATED).body(companyPaymentTypeMapper.toDTO(savedEntity));
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

    public ResponseEntity<CompanyPaymentTypeDTO> update(UUID id, UpdateCompanyPaymentTypeRequestDTO data) {
        CompanyPaymentType companyPaymentType = companyPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        companyPaymentTypeMapper.updateCompanyPaymentTypeFromDTO(data, companyPaymentType);

        ResponseEntity<PaymentType> response = paymentTypeService.update(companyPaymentType, new UpdatePaymentTypeRequestDTO(

                data.isImmediate(),
                data.allowsInstallments()
        ));

        CompanyPaymentType updated = (CompanyPaymentType) response.getBody();

        return ResponseEntity.ok(companyPaymentTypeMapper.toDTO(updated));
    }

    public ResponseEntity<CompanyPaymentTypeDTO> delete(UUID id) {
        CompanyPaymentType companyPaymentType = companyPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        ResponseEntity<PaymentType> response = paymentTypeService.delete(companyPaymentType.getId());

        CompanyPaymentType deleted = (CompanyPaymentType) response.getBody();

        return ResponseEntity.ok(companyPaymentTypeMapper.toDTO(deleted));
    }
}

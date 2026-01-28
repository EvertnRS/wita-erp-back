package org.wita.erp.services.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.mappers.PaymentTypeMapper;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTypeService {
    private final PaymentTypeRepository paymentTypeRepository;
    private final PaymentTypeMapper paymentTypeMapper;

    public ResponseEntity<Page<PaymentType>> getAllPaymentTypes(Pageable pageable, String searchTerm) {
        Page<PaymentType> paymentTypePage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            paymentTypePage = paymentTypeRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            paymentTypePage = paymentTypeRepository.findAll(pageable);
        }

        return ResponseEntity.ok(paymentTypePage);
    }

    public ResponseEntity<PaymentType> save(CreatePaymentTypeRequestDTO data) {
        PaymentType paymentType = new PaymentType();
        paymentType.setName(data.name());
        paymentType.setIsImmediate(data.isImmediate());
        paymentType.setAllowsInstallments(data.allowsInstallments());
        paymentType.setMaxInstallments(data.maxInstallments());
        paymentTypeRepository.save(paymentType);

        return ResponseEntity.ok(paymentType);
    }

    public ResponseEntity<PaymentType> update(UUID id, UpdatePaymentTypeRequestDTO data) {
        PaymentType paymentType = paymentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment type not found"));

        paymentTypeMapper.updatePaymentTypeFromDTO(data, paymentType);
        paymentTypeRepository.save(paymentType);

        return ResponseEntity.ok(paymentType);
    }

    public ResponseEntity<PaymentType> delete(UUID id) {
        PaymentType paymentType = paymentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment type not found"));

        paymentType.setActive(false);
        paymentTypeRepository.save(paymentType);

        return ResponseEntity.ok(paymentType);
    }
}

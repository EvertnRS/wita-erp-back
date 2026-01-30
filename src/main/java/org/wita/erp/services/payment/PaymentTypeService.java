package org.wita.erp.services.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.PaymentMethod;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.mappers.PaymentTypeMapper;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTypeService {
    private final PaymentTypeRepository paymentTypeRepository;
    private final PaymentTypeMapper paymentTypeMapper;

    public ResponseEntity<PaymentType> save(PaymentType paymentType, CreatePaymentTypeRequestDTO data) {
        if (data.isImmediate()) {
            paymentType.setIsImmediate(true);
            paymentType.setAllowsInstallments(false);
        } else {
            paymentType.setIsImmediate(false);
            paymentType.setAllowsInstallments(data.allowsInstallments());
        }

        if (!data.allowsInstallments()){
            paymentType.setMaxInstallments(1);
        } else{
            paymentType.setMaxInstallments(data.maxInstallments());
        }

        paymentType.setPaymentMethod(data.paymentMethod());
        paymentTypeRepository.save(paymentType);

        return ResponseEntity.ok(paymentType);
    }

    public ResponseEntity<PaymentType> update(PaymentType paymentType, UpdatePaymentTypeRequestDTO data) {

        paymentTypeMapper.updatePaymentTypeFromDTO(data, paymentType);
        paymentTypeRepository.save(paymentType);

        return ResponseEntity.ok(paymentType);
    }

    public ResponseEntity<PaymentType> delete(UUID id) {
        PaymentType paymentType = paymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        paymentType.setActive(false);
        paymentTypeRepository.save(paymentType);

        return ResponseEntity.ok(paymentType);
    }
}

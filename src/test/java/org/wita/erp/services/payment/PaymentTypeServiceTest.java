package org.wita.erp.services.payment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.mappers.PaymentTypeMapper;
import org.wita.erp.domain.repositories.payment.PaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class PaymentTypeServiceTest {
    @Mock
    private PaymentTypeRepository paymentTypeRepository;
    @Mock
    private PaymentTypeMapper paymentTypeMapper;

    @InjectMocks
    private PaymentTypeService paymentTypeService;

    @Test
    @DisplayName("Deve configurar pagamento imediato corretamente (sem parcelas)")
    void shouldSaveImmediatePaymentType() {
        PaymentType fakePaymentType = new PaymentType();
        CreatePaymentTypeRequestDTO fakeCreatePaymentTypeDTO = new CreatePaymentTypeRequestDTO(true, false);

        ResponseEntity<PaymentType> response = paymentTypeService.save(fakePaymentType, fakeCreatePaymentTypeDTO);

        Assertions.assertTrue(fakePaymentType.getIsImmediate());
        Assertions.assertFalse(fakePaymentType.getAllowsInstallments());
        Mockito.verify(paymentTypeRepository).save(fakePaymentType);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve configurar pagamento não imediato permitindo parcelas conforme DTO")
    void shouldSaveNonImmediatePaymentTypeRespectingDto() {
        PaymentType fakePaymentType = new PaymentType();
        CreatePaymentTypeRequestDTO fakeCreatePaymentTypeDTO = new CreatePaymentTypeRequestDTO(false, true);

        ResponseEntity<PaymentType> response = paymentTypeService.save(fakePaymentType, fakeCreatePaymentTypeDTO);

        Assertions.assertFalse(fakePaymentType.getIsImmediate());
        Assertions.assertTrue(fakePaymentType.getAllowsInstallments());
        Mockito.verify(paymentTypeRepository).save(fakePaymentType);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve atualizar o tipo de pagamento com sucesso usando o mapper")
    void shouldUpdatePaymentTypeSuccessfully() {
        PaymentType fakePaymentType = new PaymentType();
        UpdatePaymentTypeRequestDTO fakeUpdatePaymentTypeDTO = new UpdatePaymentTypeRequestDTO(false, true);

        ResponseEntity<PaymentType> response = paymentTypeService.update(fakePaymentType, fakeUpdatePaymentTypeDTO);

        Mockito.verify(paymentTypeMapper).updatePaymentTypeFromDTO(fakeUpdatePaymentTypeDTO, fakePaymentType);
        Mockito.verify(paymentTypeRepository).save(fakePaymentType);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(fakePaymentType, response.getBody());
    }

    @Test
    @DisplayName("Deve realizar soft delete do tipo de pagamento com sucesso")
    void shouldDeletePaymentTypeSuccessfully() {
        UUID id = UUID.randomUUID();
        PaymentType paymentType = new PaymentType();
        paymentType.setId(id);
        paymentType.setActive(true);

        Mockito.when(paymentTypeRepository.findById(id)).thenReturn(Optional.of(paymentType));

        ResponseEntity<PaymentType> response = paymentTypeService.delete(id);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertFalse(paymentType.getActive());
        Mockito.verify(paymentTypeRepository).save(paymentType);
        Assertions.assertEquals(paymentType, response.getBody());
    }

    @Test
    @DisplayName("Deve lançar PaymentTypeException quando tentar deletar um tipo de pagamento inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentPaymentType() {
        UUID id = UUID.randomUUID();
        Mockito.when(paymentTypeRepository.findById(id)).thenReturn(Optional.empty());
        PaymentTypeException exception = Assertions.assertThrows(PaymentTypeException.class,
                () -> paymentTypeService.delete(id));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        Assertions.assertEquals("Payment Type not found", exception.getMessage());
        Mockito.verify(paymentTypeRepository, Mockito.never()).save(Mockito.any());
    }
}
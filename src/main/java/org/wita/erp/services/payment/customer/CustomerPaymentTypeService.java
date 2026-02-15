package org.wita.erp.services.payment.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.audit.EntityType;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.payment.PaymentType;
import org.wita.erp.domain.entities.payment.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.payment.customer.dto.CreateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.DeleteCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.dto.UpdateCustomerPaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.customer.mappers.CustomerPaymentTypeMapper;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentTypeRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.UpdatePaymentTypeRequestDTO;
import org.wita.erp.domain.repositories.customer.CustomerRepository;
import org.wita.erp.domain.repositories.payment.customer.CustomerPaymentTypeRepository;
import org.wita.erp.infra.exceptions.payment.PaymentTypeException;
import org.wita.erp.services.audit.observer.SoftDeleteLogObserver;
import org.wita.erp.services.customer.observers.CustomerSoftDeleteObserver;
import org.wita.erp.services.payment.PaymentTypeService;
import org.wita.erp.services.payment.customer.observers.CustomerPaymentTypeSoftDeleteObserver;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerPaymentTypeService {
    private final CustomerPaymentTypeMapper customerPaymentTypeMapper;
    private final PaymentTypeService paymentTypeService;
    private final CustomerRepository customerRepository;
    private final CustomerPaymentTypeRepository customerPaymentTypeRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
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
        Customer customer = customerRepository.findById(data.customer())
                .orElseThrow(() -> new PaymentTypeException("Customer not found", HttpStatus.NOT_FOUND));

        CustomerPaymentType customerPaymentType = new CustomerPaymentType();
        customerPaymentType.setSupportsRefunds(data.supportsRefunds());
        customerPaymentType.setCustomer(customer);

        ResponseEntity<PaymentType> response = paymentTypeService.save(customerPaymentType, new CreatePaymentTypeRequestDTO(
                data.isImmediate(),
                data.allowsInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    public ResponseEntity<PaymentType> update(UUID id, UpdateCustomerPaymentTypeRequestDTO data) {
        CustomerPaymentType customerPaymentType = customerPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        customerPaymentTypeMapper.updateCustomerPaymentTypeFromDTO(data, customerPaymentType);

        ResponseEntity<PaymentType> response = paymentTypeService.update(customerPaymentType, new UpdatePaymentTypeRequestDTO(
                data.isImmediate(),
                data.allowsInstallments()
        ));

        return ResponseEntity.ok(response.getBody());
    }

    public ResponseEntity<PaymentType> delete(UUID id, DeleteCustomerPaymentTypeRequestDTO data) {
        CustomerPaymentType customerPaymentType = customerPaymentTypeRepository.findById(id)
                .orElseThrow(() -> new PaymentTypeException("Payment Type not found", HttpStatus.NOT_FOUND));

        ResponseEntity<PaymentType> response = paymentTypeService.delete(customerPaymentType.getId());

        this.auditCustomerPaymentTypeSoftDelete(customerPaymentType.getId(), data.reason());
        this.customerPaymentTypeCascadeDelete(id);

        return ResponseEntity.ok(response.getBody());
    }

    @EventListener
    public void onCustomerSoftDelete(CustomerSoftDeleteObserver event) {
        List<UUID> customerPaymentTypeIds = customerPaymentTypeRepository.cascadeDeleteFromCustomer(event.customer());
        if(!customerPaymentTypeIds.isEmpty()){
            for (UUID customerPaymentTypeId : customerPaymentTypeIds) {
                this.auditCustomerPaymentTypeSoftDelete(customerPaymentTypeId, "Cascade delete from customer " + event.customer());
                this.customerPaymentTypeCascadeDelete(customerPaymentTypeId);
            }
        }
    }

    @Async
    public void auditCustomerPaymentTypeSoftDelete(UUID id, String reason){
        publisher.publishEvent(new SoftDeleteLogObserver(id.toString(), EntityType.PAYMENT_TYPE.getEntityType(), reason));
    }

    @Async
    public void customerPaymentTypeCascadeDelete(UUID id){
        publisher.publishEvent(new CustomerPaymentTypeSoftDeleteObserver(id));
    }
}

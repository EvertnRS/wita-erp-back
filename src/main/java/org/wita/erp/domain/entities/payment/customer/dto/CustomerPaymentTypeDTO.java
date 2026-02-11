package org.wita.erp.domain.entities.payment.customer.dto;

import org.wita.erp.domain.entities.customer.Customer;

import java.util.UUID;

public record CustomerPaymentTypeDTO(UUID id, Boolean supportsRefunds, Customer customer) {
}

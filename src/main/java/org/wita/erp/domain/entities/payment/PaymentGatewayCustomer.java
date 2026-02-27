package org.wita.erp.domain.entities.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.wita.erp.domain.entities.customer.Customer;

import java.util.UUID;

@Entity
@Table(name = "payment_gateway_customer")
@Getter
@Setter
@Audited
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayCustomer {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentGateway gateway;

    @Column(name = "gateway_customer_id", nullable = false, length = 100)
    private String gatewayCustomerId;
}

package org.wita.erp.services.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.payment.PaymentGateway;
import org.wita.erp.domain.entities.payment.PaymentGatewayCustomer;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.repositories.customer.CustomerRepository;
import org.wita.erp.domain.repositories.payment.PaymentGatewayCustomerRepository;
import org.wita.erp.infra.exceptions.customer.CustomerException;
import org.wita.erp.services.customer.observers.CustomerCreateObserver;

import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final CustomerRepository customerRepository;
    private final PaymentGatewayCustomerRepository paymentGatewayCustomerRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public Session createCheckoutSession(
            Receivable receivable,
            String stripeCustomerId,
            UUID paymentId
    ) throws StripeException {

        long amount = receivable.getValue()
                .movePointRight(2)
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setCustomer(stripeCustomerId)
                        .setSuccessUrl(frontendUrl + "/success")
                        .setCancelUrl(frontendUrl + "/cancel")
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("brl")
                                                        .setUnitAmount(amount)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Pedido #" + receivable.getOrder().getTransactionCode())
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .setPaymentIntentData(
                                SessionCreateParams.PaymentIntentData.builder()
                                        .putMetadata("paymentId", paymentId.toString())
                                        .build()
                        )
                        .build();

        return Session.create(params);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCustomerCreated(CustomerCreateObserver event) {
        Customer customer = customerRepository.findById(event.customer())
                .orElseThrow(() -> new CustomerException("Customer not found", HttpStatus.NOT_FOUND));

        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setName(customer.getName())
                    .setEmail(customer.getEmail())
                    .build();
            com.stripe.model.Customer stripeCustomer = com.stripe.model.Customer.create(params);

            PaymentGatewayCustomer paymentGatewayCustomer = new PaymentGatewayCustomer();
            paymentGatewayCustomer.setCustomer(customer);
            paymentGatewayCustomer.setGateway(PaymentGateway.STRIPE);
            paymentGatewayCustomer.setGatewayCustomerId(stripeCustomer.getId());

            paymentGatewayCustomerRepository.save(paymentGatewayCustomer);
        } catch (com.stripe.exception.StripeException e ) {
            System.err.println("Falha ao criar Stripe customer para o ID: " + customer.getId());
            System.err.println("Motivo: " + e.getMessage());
        }
    }
}

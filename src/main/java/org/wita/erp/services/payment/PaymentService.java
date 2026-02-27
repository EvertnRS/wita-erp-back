package org.wita.erp.services.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.payment.Payment;
import org.wita.erp.domain.entities.payment.PaymentGateway;
import org.wita.erp.domain.entities.payment.PaymentGatewayCustomer;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentCheckoutDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentDTO;
import org.wita.erp.domain.entities.payment.mappers.PaymentMapper;
import org.wita.erp.domain.entities.transaction.PaymentStatus;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.repositories.payment.PaymentRepository;
import org.wita.erp.domain.repositories.transaction.order.ReceivableRepository;
import org.wita.erp.infra.exceptions.payment.PaymentException;
import org.wita.erp.infra.exceptions.receivable.ReceivableException;
import org.wita.erp.services.payment.observers.PaymentConfirmObserver;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ReceivableRepository receivableRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    private final ApplicationEventPublisher publisher;

    public ResponseEntity<Page<PaymentDTO>> getAllPayments(Pageable pageable, String searchTerm) {
        Page<Payment> paymentPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            paymentPage = paymentRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            paymentPage = paymentRepository.findAll(pageable);
        }

        return ResponseEntity.ok(paymentPage.map(paymentMapper::toDTO));
    }

    @Transactional
    public ResponseEntity<PaymentDTO> save(UUID receivableId, CreatePaymentRequestDTO data) {
        Receivable receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() -> new ReceivableException("Receivable not found", HttpStatus.NOT_FOUND));

        Payment payment = new Payment();
        payment.setReceivable(receivable);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(receivable.getValue());
        payment.setCurrency("BRL");
        payment.setGateway(data.gateway());
        payment.setAttempts(0);

        payment.setGatewayPaymentId("PENDING_STRIPE_" + receivableId);

        return ResponseEntity.ok(paymentMapper.toDTO(paymentRepository.save(payment)));
    }

    @Transactional
    public ResponseEntity<PaymentCheckoutDTO> checkout(UUID paymentId) throws StripeException {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found", HttpStatus.NOT_FOUND));

        String stripeCustomerId = payment.getReceivable().getOrder().getCustomerPaymentType().getCustomer().getPaymentGatewayCustomers().stream()
                .filter(pgc -> pgc.getGateway() == PaymentGateway.STRIPE)
                .map(PaymentGatewayCustomer::getGatewayCustomerId)
                .findFirst()
                .orElseThrow(() -> new PaymentException("Customer not registered in Stripe", HttpStatus.BAD_REQUEST));

        Session session = stripeService.createCheckoutSession(payment.getReceivable(), stripeCustomerId, payment.getId());

        payment.setGatewaySessionId(session.getId());
        payment.setAttempts(payment.getAttempts() + 1);

        paymentRepository.save(payment);

        return ResponseEntity.ok(new PaymentCheckoutDTO(paymentId, session.getUrl()));
    }

    @Transactional
    public void confirmPaymentFromWebhook(String sessionId, String paymentIntentId) {
        Payment payment = paymentRepository.findByGatewaySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para a sessão: " + sessionId));

        payment.setStatus(PaymentStatus.PAID);
        payment.setGatewayPaymentId(paymentIntentId);
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        publisher.publishEvent(new PaymentConfirmObserver(payment.getId()));
    }
}

package org.wita.erp.services.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.payment.Payment;
import org.wita.erp.domain.entities.payment.PaymentGateway;
import org.wita.erp.domain.entities.payment.PaymentGatewayCustomer;
import org.wita.erp.domain.entities.payment.dtos.CreatePaymentRequestDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentCheckoutDTO;
import org.wita.erp.domain.entities.payment.dtos.PaymentDTO;
import org.wita.erp.domain.entities.payment.mappers.PaymentMapper;
import org.wita.erp.domain.entities.paymentType.customer.CustomerPaymentType;
import org.wita.erp.domain.entities.transaction.PaymentStatus;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.repositories.payment.PaymentRepository;
import org.wita.erp.domain.repositories.transaction.order.ReceivableRepository;
import org.wita.erp.infra.exceptions.receivable.ReceivableException;
import org.wita.erp.services.payment.observers.PaymentConfirmObserver;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private ReceivableRepository receivableRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private ApplicationEventPublisher publisher;

    private UUID paymentId;
    private UUID receivableId;
    private Payment fakePayment;
    private Receivable receivable;
    private Pageable pageable;
    private Page<Payment> fakePage;
    private PaymentDTO fakePaymentDTO;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        receivableId = UUID.randomUUID();

        Customer customer = new Customer();
        PaymentGatewayCustomer pgc = new PaymentGatewayCustomer();
        pgc.setGateway(PaymentGateway.STRIPE);
        pgc.setGatewayCustomerId("cus_12345");
        customer.setPaymentGatewayCustomers(List.of(pgc));

        CustomerPaymentType cpt = new CustomerPaymentType();
        cpt.setCustomer(customer);

        Order order = new Order();
        order.setCustomerPaymentType(cpt);
        pageable = PageRequest.of(0, 10);

        receivable = new Receivable();
        receivable.setId(receivableId);
        receivable.setValue(new BigDecimal("150.00"));
        receivable.setOrder(order);

        fakePayment = new Payment();
        fakePayment.setId(paymentId);
        fakePayment.setReceivable(receivable);
        fakePayment.setAttempts(0);
        fakePayment.setStatus(PaymentStatus.PENDING);

        fakePage = new PageImpl<>(List.of(fakePayment));

        fakePaymentDTO = new PaymentDTO(paymentId, receivableId, PaymentStatus.PENDING, null, "BRL", PaymentGateway.STRIPE, 1, null, null);
    }

    @Test
    @DisplayName("Deve retornar todos os pagamentos quando o searchTerm for nulo")
    void shouldReturnAllPaymentsWhenSearchTermIsNull() {
        when(paymentRepository.findAll(pageable)).thenReturn(fakePage);
        when(paymentMapper.toDTO(any())).thenReturn(fakePaymentDTO);

        ResponseEntity<Page<PaymentDTO>> response = paymentService.getAllPayments(pageable, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentRepository, times(1)).findAll(pageable);
        verify(paymentRepository, never()).findBySearchTerm(any(), any());
    }

    @Test
    @DisplayName("Deve retornar todos os pagamentos paginados com filtro de busca")
    void getAllPayments_WithSearchTerm() {
        when(paymentRepository.findBySearchTerm("John Doe", pageable)).thenReturn(fakePage);
        when(paymentMapper.toDTO(any())).thenReturn(fakePaymentDTO);

        ResponseEntity<Page<PaymentDTO>> response = paymentService.getAllPayments(pageable, "John Doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(paymentRepository, times(1)).findBySearchTerm("John Doe", pageable);
        verify(paymentRepository, never()).findAll(pageable);
    }

    @Test
    @DisplayName("Deve salvar um novo pagamento com sucesso")
    void save_Success() {
        CreatePaymentRequestDTO request = new CreatePaymentRequestDTO(PaymentGateway.STRIPE);
        when(receivableRepository.findById(receivableId)).thenReturn(Optional.of(receivable));
        when(paymentRepository.save(any(Payment.class))).thenReturn(fakePayment);
        when(paymentMapper.toDTO(any())).thenReturn(fakePaymentDTO);

        ResponseEntity<PaymentDTO> response = paymentService.save(receivableId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verifica se salvou com os dados corretos
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(PaymentStatus.PENDING, savedPayment.getStatus());
        assertEquals("BRL", savedPayment.getCurrency());
        assertEquals(PaymentGateway.STRIPE, savedPayment.getGateway());
    }

    @Test
    @DisplayName("Deve lançar ReceivableException quando tentar salvar pagamento de parcela inexistente")
    void save_ReceivableNotFound() {
        CreatePaymentRequestDTO request = new CreatePaymentRequestDTO(PaymentGateway.STRIPE);
        when(receivableRepository.findById(receivableId)).thenReturn(Optional.empty());

        ReceivableException exception = assertThrows(ReceivableException.class, () -> paymentService.save(receivableId, request));
        assertEquals("Receivable not found", exception.getMessage());
    }

    @Test
    @DisplayName("Deve gerar URL de checkout do Stripe com sucesso")
    void checkout_Success() throws StripeException {
        Session stripeSessionMock = mock(Session.class);
        when(stripeSessionMock.getId()).thenReturn("cs_test_123");
        when(stripeSessionMock.getUrl()).thenReturn("https://checkout.stripe.com/...");

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(fakePayment));
        when(stripeService.createCheckoutSession(any(), eq("cus_12345"), eq(paymentId))).thenReturn(stripeSessionMock);

        ResponseEntity<PaymentCheckoutDTO> response = paymentService.checkout(paymentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("https://checkout.stripe.com/...", response.getBody().checkoutUrl());

        assertEquals("cs_test_123", fakePayment.getGatewaySessionId());
        assertEquals(1, fakePayment.getAttempts());
        verify(paymentRepository, times(1)).save(fakePayment);
    }

    @Test
    @DisplayName("Deve confirmar pagamento vindo do Webhook com sucesso e disparar evento")
    void confirmPaymentFromWebhook_Success() {
        String sessionId = "cs_test_123";
        String paymentIntentId = "pi_test_123";
        fakePayment.setGatewaySessionId(sessionId);

        when(paymentRepository.findByGatewaySessionId(sessionId)).thenReturn(Optional.of(fakePayment));

        paymentService.confirmPaymentFromWebhook(sessionId, paymentIntentId);

        assertEquals(PaymentStatus.PAID, fakePayment.getStatus());
        assertEquals(paymentIntentId, fakePayment.getGatewayPaymentId());
        assertNotNull(fakePayment.getPaidAt());

        verify(paymentRepository, times(1)).save(fakePayment);
        verify(publisher, times(1)).publishEvent(any(PaymentConfirmObserver.class));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando webhook tentar confirmar sessão inexistente")
    void confirmPaymentFromWebhook_SessionNotFound() {
        when(paymentRepository.findByGatewaySessionId("cs_invalid")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                paymentService.confirmPaymentFromWebhook("cs_invalid", "pi_123"));

        assertTrue(exception.getMessage().contains("Pagamento não encontrado"));
        verify(publisher, never()).publishEvent(any());
    }
}
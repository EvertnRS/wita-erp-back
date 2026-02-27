package org.wita.erp.services.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.wita.erp.domain.entities.customer.Customer;
import org.wita.erp.domain.entities.payment.PaymentGateway;
import org.wita.erp.domain.entities.payment.PaymentGatewayCustomer;
import org.wita.erp.domain.entities.transaction.order.Order;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.repositories.customer.CustomerRepository;
import org.wita.erp.domain.repositories.payment.PaymentGatewayCustomerRepository;
import org.wita.erp.infra.exceptions.customer.CustomerException;
import org.wita.erp.services.customer.observers.CustomerCreateObserver;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    @InjectMocks
    private StripeService stripeService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentGatewayCustomerRepository paymentGatewayCustomerRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stripeService, "frontendUrl", "http://localhost:3000");
    }

    @Test
    @DisplayName("Deve criar a sessão de checkout com os valores calculados corretamente")
    void shouldCreateCheckoutSessionSuccess() throws StripeException {
        UUID paymentId = UUID.randomUUID();
        String stripeCustomerId = "cus_test123";

        Order order = new Order();
        order.setTransactionCode("TRX-999");

        Receivable receivable = new Receivable();
        receivable.setValue(new BigDecimal("150.50"));
        receivable.setOrder(order);

        Session mockSession = mock(Session.class);

        try (MockedStatic<Session> mockedSessionStatic = mockStatic(Session.class)) {
            mockedSessionStatic.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            Session result = stripeService.createCheckoutSession(receivable, stripeCustomerId, paymentId);

            assertNotNull(result);

            ArgumentCaptor<SessionCreateParams> paramsCaptor = ArgumentCaptor.forClass(SessionCreateParams.class);
            mockedSessionStatic.verify(() -> Session.create(paramsCaptor.capture()), times(1));

            SessionCreateParams capturedParams = paramsCaptor.getValue();

            Long unitAmount = capturedParams.getLineItems().get(0).getPriceData().getUnitAmount();

            assertEquals(15050L, unitAmount);
            assertEquals("cus_test123", capturedParams.getCustomer());
        }
    }

    @Test
    @DisplayName("Deve criar Customer no Stripe e salvar o GatewayCustomer")
    void shouldOnCustomerCreatedSuccess() {
        UUID customerId = UUID.randomUUID();
        CustomerCreateObserver event = new CustomerCreateObserver(customerId);

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("João Teste");
        customer.setEmail("joao@teste.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        com.stripe.model.Customer stripeMockCustomer = mock(com.stripe.model.Customer.class);
        when(stripeMockCustomer.getId()).thenReturn("cus_stripe_abc");

        try (MockedStatic<com.stripe.model.Customer> mockedStripeCustomer = mockStatic(com.stripe.model.Customer.class)) {
            mockedStripeCustomer.when(() -> com.stripe.model.Customer.create(any(CustomerCreateParams.class)))
                    .thenReturn(stripeMockCustomer);

            stripeService.onCustomerCreated(event);

            ArgumentCaptor<PaymentGatewayCustomer> pgcCaptor = ArgumentCaptor.forClass(PaymentGatewayCustomer.class);
            verify(paymentGatewayCustomerRepository, times(1)).save(pgcCaptor.capture());

            PaymentGatewayCustomer savedPgc = pgcCaptor.getValue();
            assertEquals(customer, savedPgc.getCustomer());
            assertEquals(PaymentGateway.STRIPE, savedPgc.getGateway());
            assertEquals("cus_stripe_abc", savedPgc.getGatewayCustomerId());
        }
    }

    @Test
    @DisplayName("Deve lançar CustomerException se o cliente não for encontrado")
    void shouldThrowCustomerExceptionOnCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        CustomerCreateObserver event = new CustomerCreateObserver(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> stripeService.onCustomerCreated(event));
        assertEquals("Customer not found", exception.getMessage());

        verify(paymentGatewayCustomerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve capturar StripeException")
    void shouldThrowStripeException() {
        UUID customerId = UUID.randomUUID();
        CustomerCreateObserver event = new CustomerCreateObserver(customerId);

        Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        StripeException stripeExceptionMock = mock(StripeException.class);

        try (MockedStatic<com.stripe.model.Customer> mockedStripeCustomer = mockStatic(com.stripe.model.Customer.class)) {
            mockedStripeCustomer.when(() -> com.stripe.model.Customer.create(any(CustomerCreateParams.class)))
                    .thenThrow(stripeExceptionMock);

            assertDoesNotThrow(() -> stripeService.onCustomerCreated(event));

            verify(paymentGatewayCustomerRepository, never()).save(any());
        }
    }
}
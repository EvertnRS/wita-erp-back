package org.wita.erp.infra.schedules.handler.receivable;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.transaction.PaymentStatus;
import org.wita.erp.domain.entities.transaction.order.Receivable;
import org.wita.erp.domain.repositories.transaction.order.ReceivableRepository;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.infra.schedules.handler.ScheduledTaskHandler;
import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReceivableDueSoonHandler implements ScheduledTaskHandler {
    private final ReceivableRepository repository;
    private final EmailProvider emailProvider;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public ScheduledTaskTypes  getType() {
        return ScheduledTaskTypes.RECEIVABLE_DUE_SOON;
    }

    @Override
    @Transactional
    public void handle(String referenceId) throws MessagingException {

        UUID id = UUID.fromString(referenceId);

        Receivable receivable = repository.findById(id)
                .orElse(null);

        if (receivable == null) return;

        if (receivable.getPaymentStatus() != PaymentStatus.PENDING)
            return;

        String template = emailProvider.buildOverdueTransactionTemplate(
                "Pagamento Atrasado",
                "O pagamento da sua transação está perto do vencimento. Por favor, regularize o quanto antes para evitar problemas futuros.",
                receivable.getOrder().getSeller().getName(),
                receivable.getOrder().getCustomerPaymentType().getCustomer().getName(),
                String.format("%.2f", receivable.getValue()),
                receivable.getDueDate().toString(),
                "Regularizar Pagamento",
                frontendUrl);

        emailProvider.sendEmail(receivable.getOrder().getCustomerPaymentType().getCustomer().getEmail(), "Pagamento Vencendo", template);

    }
}


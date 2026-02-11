package org.wita.erp.infra.schedules.handler.payable;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.status.PaymentStatus;
import org.wita.erp.domain.entities.transaction.purchase.Payable;
import org.wita.erp.domain.repositories.transaction.purchase.PayableRepository;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.infra.schedules.handler.ScheduledTaskHandler;
import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PayableOverdueHandler implements ScheduledTaskHandler {
    private final PayableRepository payableRepository;
    private final EmailProvider emailProvider;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public ScheduledTaskTypes getType() {
        return ScheduledTaskTypes.PAYABLE_OVERDUE;
    }

    @Override
    @Transactional
    public void handle(String referenceId) throws MessagingException {

        UUID id = UUID.fromString(referenceId);

        Payable payable = payableRepository.findById(id)
                .orElse(null);

        if (payable == null) return;

        if (payable.getPaymentStatus() != PaymentStatus.PENDING)
            return;

        payable.setPaymentStatus(PaymentStatus.OVERDUE);

        String template = emailProvider.buildOverdueTransactionTemplate(
                "Pagamento Atrasado",
                "O pagamento de uma transação está atrasado. Por favor, regularize o quanto antes para evitar problemas futuros.",
                payable.getPurchase().getSupplier().getName(),
                payable.getPurchase().getBuyer().getName(),
                String.format("%.2f", payable.getValue()),
                payable.getDueDate().toString(),
                "Regularizar Pagamento",
                frontendUrl);

        emailProvider.sendEmail(payable.getPurchase().getBuyer().getEmail(), "Pagamento atrasado", template);
    }
}

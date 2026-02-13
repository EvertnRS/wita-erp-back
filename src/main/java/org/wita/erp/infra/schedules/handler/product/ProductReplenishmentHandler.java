package org.wita.erp.infra.schedules.handler.product;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.product.Product;
import org.wita.erp.domain.repositories.product.ProductRepository;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.infra.schedules.handler.ScheduledTaskHandler;
import org.wita.erp.infra.schedules.handler.ScheduledTaskTypes;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductReplenishmentHandler implements ScheduledTaskHandler {
    private final ProductRepository productRepository;
    private final EmailProvider emailProvider;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.flyway.placeholders.admin_email}")
    private String adminEmail;

    @Override
    public ScheduledTaskTypes getType() {
        return ScheduledTaskTypes.PRODUCT_REPLENISHMENT;
    }

    @Override
    @Transactional
    public void handle(String referenceId) throws MessagingException {

        UUID id = UUID.fromString(referenceId);

        Product product = productRepository.findById(id)
                .orElse(null);

        if (product == null) return;

        if (product.getQuantityInStock() > product.getMinQuantity())
            return;

        String template = emailProvider.buildProductReplenishmentTemplate(
                "Produto atingiu quantidade baixa",
                "O produto " + product.getName() + " atingiu a quantidade mínima em estoque. Por favor, regularize o pagamento para evitar atrasos na reposição.",
                product.getName(),
                product.getQuantityInStock().toString(),
                product.getCategory().getName(),
                product.getSupplier().getName(),
                "Acessar painel",
                frontendUrl);

        emailProvider.sendEmail(adminEmail, "Produto em quantidade baixa", template);
    }
}

package org.wita.erp.infra.providers.email;

import jakarta.mail.MessagingException;

public interface EmailProvider {
    void sendEmail(String to, String subject, String html) throws MessagingException;

    String buildRecoveryPasswordTemplate(String title, String message, String agentName, String deviceClass,
                                         String userName, String dateTime, String buttonText, String buttonUrl);

    String buildOverdueTransactionTemplate(String title, String message, String agentName, String deviceClass,
                                           String userName, String dateTime, String buttonText, String buttonUrl);

    String buildProductReplenishmentTemplate(String title, String message, String productName, String quantity,
                                             String categoryName, String supplierName, String buttonText, String buttonUrl);
}

package org.wita.erp.infra.providers.email;

import jakarta.mail.MessagingException;

public interface EmailProvider {
    void sendEmail(String to, String subject, String html) throws MessagingException;
    String buildTemplate(String title, String message, String agentName, String deviceClass,
                         String userName, String dateTime, String buttonText, String buttonLink);
}

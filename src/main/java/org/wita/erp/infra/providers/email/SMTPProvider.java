package org.wita.erp.infra.providers.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SMTPProvider implements EmailProvider {
    private final JavaMailSender mailSender;
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    @Value("${spring.mail.username}")
    private String email;

    public void sendEmail(String to, String subject, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(email);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);
    }

    public void sendEmail(String to, String subject, String html, byte[] qrcode) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(email);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        helper.addInline(
                "qrcode",
                new ByteArrayResource(qrcode),
                "image/png"
        );

        mailSender.send(message);
    }

    public void sendEmail(String to, String subject, String html, String attachmentFilename, String contentType, byte[] fileBytes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(email);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        helper.addAttachment(
                attachmentFilename,
                new ByteArrayResource(fileBytes),
                contentType
        );

        mailSender.send(message);
    }


    public String buildRecoveryPasswordTemplate(String title, String message, String agentName, String deviceClass,
                                String userName, String dateTime, String buttonText, String buttonUrl) {
        return processTemplate(
                "/templates/recoveryPasswordTemplate.html",
                Map.of(
                        "TITLE", title,
                        "MESSAGE", message,
                        "AGENT_NAME", agentName,
                        "DEVICE_CLASS", deviceClass,
                        "USER_NAME", userName,
                        "DATETIME", dateTime,
                        "BUTTON_TEXT", buttonText,
                        "BUTTON_URL", buttonUrl
                )
        );
    }

    public String buildOverdueTransactionTemplate(String title, String message, String sellerName, String buyerName,
                                                String value, String dateTime, String buttonText, String buttonUrl) {
        return processTemplate(
                "/templates/overdueTransactionTemplate.html",
                Map.of(
                        "TITLE", title,
                        "MESSAGE", message,
                        "SELLER_NAME", sellerName,
                        "BUYER_NAME", buyerName,
                        "VALUE", value,
                        "DATETIME", dateTime,
                        "BUTTON_TEXT", buttonText,
                        "BUTTON_URL", buttonUrl
                )
        );
    }

    public String buildProductReplenishmentTemplate(String title, String message, String productName, String quantity,
                                                  String categoryName, String supplierName, String buttonText, String buttonUrl) {
        return processTemplate(
                "/templates/productReplenishmentTemplate.html",
                Map.of(
                        "TITLE", title,
                        "MESSAGE", message,
                        "PRODUCT_NAME", productName,
                        "QUANTITY", quantity,
                        "CATEGORY_NAME", categoryName,
                        "SUPPLIER_NAME", supplierName,
                        "BUTTON_TEXT", buttonText,
                        "BUTTON_URL", buttonUrl
                )
        );
    }

    public String buildEnable2FATemplate(String title, String message, String agentName, String deviceClass,
                           String userName, String dateTime){
        return processTemplate(
                "/templates/enable2FATemplate.html",
                Map.of(
                        "TITLE", title,
                        "MESSAGE", message,
                        "AGENT_NAME", agentName,
                        "DEVICE_CLASS", deviceClass,
                        "USER_NAME", userName,
                        "DATETIME", dateTime
                )
        );
    }

    public String buildReportExport(String title, String message, String agentName, String deviceClass,
                                         String userName, String dateTime){
        return processTemplate(
                "/templates/reportExport.html",
                Map.of(
                        "TITLE", title,
                        "MESSAGE", message,
                        "AGENT_NAME", agentName,
                        "DEVICE_CLASS", deviceClass,
                        "USER_NAME", userName,
                        "DATETIME", dateTime
                )
        );
    }

    public String buildVerifyEmailTemplate(String title, String message, String agentName, String date, String buttonText, String buttonUrl) {
        return processTemplate(
                "/templates/verifyEmailTemplate.html",
                Map.of(
                        "TITLE", title,
                        "MESSAGE", message,
                        "AGENT_NAME", agentName,
                        "DATE", date,
                        "BUTTON_TEXT", buttonText,
                        "BUTTON_URL", buttonUrl
                )
        );
    }

    private String loadTemplate(String path) {
        return templateCache.computeIfAbsent(path, p -> {
            try (InputStream is = getClass().getResourceAsStream(p)) {
                if (is == null) {
                    throw new IllegalArgumentException("Template not found: " + p);
                }
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Load template error", e);
            }
        });
    }

    private String processTemplate(String path, Map<String, String> variables) {
        String template = loadTemplate(path);

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return template;
    }

}

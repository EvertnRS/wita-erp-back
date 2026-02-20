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

@Service
@RequiredArgsConstructor
public class SMTPProvider implements EmailProvider {
    private final JavaMailSender mailSender;

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
        String template = loadTemplate("/templates/recoveryPasswordTemplate.html");
        template = template.replace("{{TITLE}}", title);
        template = template.replace("{{MESSAGE}}", message);
        template = template.replace("{{AGENT_NAME}}", agentName);
        template = template.replace("{{DEVICE_CLASS}}", deviceClass);
        template = template.replace("{{USER_NAME}}", userName);
        template = template.replace("{{DATETIME}}", dateTime);
        template = template.replace("{{BUTTON_TEXT}}", buttonText);
        template = template.replace("{{BUTTON_URL}}", buttonUrl);

        return template;
    }

    public String buildOverdueTransactionTemplate(String title, String message, String sellerName, String buyerName,
                                                String value, String dateTime, String buttonText, String buttonUrl) {
        String template = loadTemplate("/templates/overduePaymentTemplate.html");
        template = template.replace("{{TITLE}}", title);
        template = template.replace("{{MESSAGE}}", message);
        template = template.replace("{{SELLER_NAME}}", sellerName);
        template = template.replace("{{BUYER_NAME}}", buyerName);
        template = template.replace("{{VALUE}}", value);
        template = template.replace("{{DATETIME}}", dateTime);
        template = template.replace("{{BUTTON_TEXT}}", buttonText);
        template = template.replace("{{BUTTON_URL}}", buttonUrl);

        return template;
    }

    public String buildProductReplenishmentTemplate(String title, String message, String productName, String quantity,
                                                  String categoryName, String supplierName, String buttonText, String buttonUrl) {
        String template = loadTemplate("/templates/productReplenishmentTemplate.html");
        template = template.replace("{{TITLE}}", title);
        template = template.replace("{{MESSAGE}}", message);
        template = template.replace("{{PRODUCT_NAME}}", productName);
        template = template.replace("{{QUANTITY}}", quantity);
        template = template.replace("{{CATEGORY_NAME}}", categoryName);
        template = template.replace("{{SUPPLIER_NAME}}", supplierName);
        template = template.replace("{{BUTTON_TEXT}}", buttonText);
        template = template.replace("{{BUTTON_URL}}", buttonUrl);

        return template;
    }

    public String buildEnable2FATemplate(String title, String message, String agentName, String deviceClass,
                           String userName, String dateTime){
        String template = loadTemplate("/templates/enable2FATemplate.html");

        template = template.replace("{{TITLE}}", title);
        template = template.replace("{{MESSAGE}}", message);
        template = template.replace("{{AGENT_NAME}}", agentName);
        template = template.replace("{{DEVICE_CLASS}}", deviceClass);
        template = template.replace("{{USER_NAME}}", userName);
        template = template.replace("{{DATETIME}}", dateTime);

        return template;
    }

    public String buildReportExport(String title, String message, String agentName, String deviceClass,
                                         String userName, String dateTime){
        String template = loadTemplate("/templates/reportExport.html");

        template = template.replace("{{TITLE}}", title);
        template = template.replace("{{MESSAGE}}", message);
        template = template.replace("{{AGENT_NAME}}", agentName);
        template = template.replace("{{DEVICE_CLASS}}", deviceClass);
        template = template.replace("{{USER_NAME}}", userName);
        template = template.replace("{{DATETIME}}", dateTime);

        return template;
    }

    private String loadTemplate(String path) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            assert is != null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Load template error", e);
        }
    }

}

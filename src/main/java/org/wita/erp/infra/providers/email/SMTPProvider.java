package org.wita.erp.infra.providers.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Getter
    private final String baseTemplate = loadTemplate();

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

    public String buildTemplate(String title, String message, String agentName, String deviceClass,
                                String userName, String dateTime, String buttonText, String buttonUrl) {
        String template = baseTemplate;
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


    private String loadTemplate() {
        try (InputStream is = getClass().getResourceAsStream("/templates/template.html")) {
            assert is != null;
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Load template error", e);
        }
    }

}

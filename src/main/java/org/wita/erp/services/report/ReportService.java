package org.wita.erp.services.report;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.report.dto.AccountReport;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.report.ReportRepository;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.infra.providers.report.ReportProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportProvider reportProvider;
    private final EmailProvider emailProvider;

    @Transactional(readOnly = true)
    public byte[] getExcelReport(LocalDate dueDateLimit, String userAgent) throws MessagingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        var receivable = reportRepository.findAllReceivable(dueDateLimit);
        var payable = reportRepository.findAllPayable(dueDateLimit);

        List<AccountReport> unified = new ArrayList<>();
        unified.addAll(receivable);
        unified.addAll(payable);

        unified.sort(Comparator.comparing(AccountReport::dueDate));
        byte[] report = reportProvider.exportExcel(unified);

        String browser = getBrowserInfo(userAgent);
        String device = getDeviceInfo(userAgent);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String template = emailProvider.buildReportExport(
                "Relatório de Contas",
                "Segue o relatório de contas a pagar e receber.",
                browser,
                device,
                user.getName(),
                LocalDateTime.now().format(formatter));

        emailProvider.sendEmail(
                user.getEmail(),
                "Relatório de Contas",
                template,
                "report.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                report
        );

        return report;
    }

    private String getBrowserInfo(String userAgent) {
        return  userAgent.contains("Chrome") ? "Chrome" :
                userAgent.contains("Firefox") ? "Firefox" :
                        userAgent.contains("Safari") && !userAgent.contains("Chrome") ? "Safari" :
                                userAgent.contains("Edg") ? "Edge" :
                                        "Desconhecido";
    }

    private String getDeviceInfo(String userAgent){
        return  userAgent.contains("Mobile") ? "Mobile" :
                userAgent.contains("Tablet") ? "Tablet" :
                        "Desktop";
    }

}

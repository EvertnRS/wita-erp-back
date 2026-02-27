package org.wita.erp.services.report;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wita.erp.domain.entities.report.ReportRange;
import org.wita.erp.domain.entities.report.ReportType;
import org.wita.erp.domain.entities.report.dto.AccountReport;
import org.wita.erp.domain.entities.report.dto.GenerateReportRequestDTO;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.report.ReportRepository;
import org.wita.erp.infra.exceptions.report.ReportException;
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
    public byte[] getSheetReport(GenerateReportRequestDTO data, String userAgent) throws MessagingException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<AccountReport> transactions = getReportTransactions(data.type(), data.range());

        transactions.sort(Comparator.comparing(AccountReport::dueDate));
        byte[] report = reportProvider.exportExcel(transactions);

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

    private List<AccountReport> getReportTransactions(ReportType type, ReportRange range) {
        LocalDate now = LocalDate.now();
        switch (type) {
            case PAYABLE -> {
                var data = reportRepository.findAllPayable(getStartDate(range, now), getEndDate(range, now));
                data.sort(Comparator.comparing(AccountReport::dueDate));
                return data;
            }

            case RECEIVABLE -> {
                var data = reportRepository.findAllReceivable(getStartDate(range, now), getEndDate(range, now));
                data.sort(Comparator.comparing(AccountReport::dueDate));
                return data;
            }

            case ALL -> {
                var receivable = reportRepository.findAllReceivable(getStartDate(range, now), getEndDate(range, now));
                var payable = reportRepository.findAllPayable(getStartDate(range, now), getEndDate(range, now));

                List<AccountReport> unified = new ArrayList<>();
                unified.addAll(receivable);
                unified.addAll(payable);

                unified.sort(Comparator.comparing(AccountReport::dueDate));
                return unified;
            }
            default -> throw new ReportException("Invalid report type", HttpStatus.BAD_REQUEST);
        }
    }

    private LocalDate getStartDate(ReportRange range, LocalDate now) {
        switch (range) {
            case YEAR -> {
                return now.withDayOfYear(1);
            }
            case SEMESTER -> {
                if (now.getMonthValue() <= 6) {
                    return now.withMonth(1).withDayOfMonth(1);
                } else {
                    return now.withMonth(7).withDayOfMonth(1);
                }
            }
            case MONTH -> {
                return now.withDayOfMonth(1);
            }
            case FORTNIGHT -> {
                if (now.getDayOfMonth() <= 15) {
                    return now.withDayOfMonth(1);
                } else {
                    return now.withDayOfMonth(16);
                }
            }
            case WEEK -> {
                return now.with(java.time.DayOfWeek.MONDAY);
            }
            case DAY -> {
                return now;
            }
            default -> throw new ReportException("Invalid report range", HttpStatus.BAD_REQUEST);
        }
    }

    private LocalDate getEndDate(ReportRange range, LocalDate now) {
        switch (range) {
            case YEAR -> {
                return now.withDayOfYear(now.lengthOfYear());
            }
            case SEMESTER -> {
                if (now.getMonthValue() <= 6) {
                    return now.withMonth(6).withDayOfMonth(30);
                } else {
                    return now.withMonth(12).withDayOfMonth(31);
                }
            }
            case MONTH -> {
                return now.withDayOfMonth(now.lengthOfMonth());
            }
            case FORTNIGHT -> {
                if (now.getDayOfMonth() <= 15) {
                    return now.withDayOfMonth(15);
                } else {
                    return now.withDayOfMonth(now.lengthOfMonth());
                }
            }
            case WEEK -> {
                return now.with(java.time.DayOfWeek.SUNDAY);
            }
            case DAY -> {
                return now;
            }
            default -> throw new ReportException("Invalid report range", HttpStatus.BAD_REQUEST);
        }
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

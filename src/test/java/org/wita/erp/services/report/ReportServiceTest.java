package org.wita.erp.services.report;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.wita.erp.domain.entities.report.ReportRange;
import org.wita.erp.domain.entities.report.ReportType;
import org.wita.erp.domain.entities.report.dto.AccountReport;
import org.wita.erp.domain.entities.report.dto.RequestGenerateReportDTO;
import org.wita.erp.domain.entities.transaction.PaymentStatus;
import org.wita.erp.domain.entities.user.User;
import org.wita.erp.domain.repositories.report.ReportRepository;
import org.wita.erp.infra.providers.email.EmailProvider;
import org.wita.erp.infra.providers.report.ReportProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportProvider reportProvider;

    @Mock
    private EmailProvider emailProvider;

    @InjectMocks
    private ReportService reportService;

    private User fakeUser;

    @BeforeEach
    void setUp() {

        fakeUser = new User();

        ReflectionTestUtils.setField(fakeUser, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(fakeUser, "name", "Test User");
        ReflectionTestUtils.setField(fakeUser, "email", "test@email.com");
        ReflectionTestUtils.setField(fakeUser, "password", "123456");
        ReflectionTestUtils.setField(fakeUser, "active", true);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(fakeUser);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldGenerateSheetReportSuccessfully() throws MessagingException {

        AccountReport receivable = new AccountReport(
                UUID.randomUUID(),
                BigDecimal.valueOf(1000),
                LocalDate.now().plusDays(1),
                PaymentStatus.PENDING,
                "RECEIVABLE"
        );

        AccountReport payable = new AccountReport(
                UUID.randomUUID(),
                BigDecimal.valueOf(500),
                LocalDate.now().plusDays(2),
                PaymentStatus.PENDING,
                "PAYABLE"
        );

        when(reportRepository.findAllReceivable(any(), any()))
                .thenReturn(List.of(receivable));

        when(reportRepository.findAllPayable(any(), any()))
                .thenReturn(List.of(payable));

        when(reportProvider.exportExcel(any()))
                .thenReturn("fake-bytes".getBytes());

        when(emailProvider.buildReportExport(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        )).thenReturn("<html>email</html>");

        doNothing().when(emailProvider).sendEmail(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any()
        );

        byte[] result = reportService.getSheetReport(new RequestGenerateReportDTO(ReportType.ALL, ReportRange.MONTH), "Chrome");

        assertNotNull(result);
        assertArrayEquals("fake-bytes".getBytes(), result);

        verify(reportRepository).findAllReceivable(any(), any());
        verify(reportRepository).findAllPayable(any(), any());
        verify(reportProvider).exportExcel(any());
        verify(emailProvider).sendEmail(
                eq(fakeUser.getEmail()),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                any()
        );
    }
}
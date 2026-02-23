package org.wita.erp.controllers.report;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wita.erp.controllers.report.docs.ReportDocs;
import org.wita.erp.domain.entities.report.dto.RequestGenerateReportDTO;
import org.wita.erp.services.report.ReportService;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController implements ReportDocs {
    private final ReportService reportService;

    @GetMapping("/sheet")
    @PreAuthorize("hasAuthority('REPORT_EXPORT')")
    public ResponseEntity<byte[]> exportSheetReport(@RequestHeader(value = "User-Agent", required = false) String userAgent, RequestGenerateReportDTO data) throws MessagingException {
        byte[] file = reportService.getSheetReport(data, userAgent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=accounts.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}

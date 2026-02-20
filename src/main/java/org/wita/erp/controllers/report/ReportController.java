package org.wita.erp.controllers.report;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wita.erp.services.report.ReportService;

import java.time.LocalDate;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/sheet")
    @PreAuthorize("hasAuthority('REPORT_EXPORT')")
    public ResponseEntity<byte[]> exportExcelReport(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateLimit, @RequestHeader(value = "User-Agent", required = false) String userAgent) throws MessagingException {
        byte[] file = reportService.getExcelReport(dueDateLimit, userAgent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=accounts.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}

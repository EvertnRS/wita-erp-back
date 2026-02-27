package org.wita.erp.controllers.report.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.wita.erp.domain.entities.report.dto.GenerateReportRequestDTO;

@Tag(name = "report's management", description = "Endpoints to generate transaction reports on ERP system")
public interface ReportDocs {

    @Operation(summary = "Generate a sheet with transaction reports", description = "Returns a sheet of transaction history and created date filter.  \nRequires REPORT_EXPORT authority.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have the required authority")
    })
    ResponseEntity<byte[]> exportSheetReport(@RequestHeader(value = "User-Agent", required = false) String userAgent,
                                             GenerateReportRequestDTO data) throws MessagingException;

}



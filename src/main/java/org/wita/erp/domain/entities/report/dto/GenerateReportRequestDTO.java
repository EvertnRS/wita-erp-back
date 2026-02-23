package org.wita.erp.domain.entities.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.report.ReportRange;
import org.wita.erp.domain.entities.report.ReportType;

public record GenerateReportRequestDTO(
        @Schema(description = "Type of the report to be generated", example = "PAYABLE")
        @NotNull ReportType type,
        @Schema(description = "Range of the report to be generated", example = "MONTH")
        @NotNull ReportRange range
        ){}

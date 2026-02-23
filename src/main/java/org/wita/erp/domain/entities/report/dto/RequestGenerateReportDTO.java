package org.wita.erp.domain.entities.report.dto;

import jakarta.validation.constraints.NotNull;
import org.wita.erp.domain.entities.report.ReportRange;
import org.wita.erp.domain.entities.report.ReportType;

public record RequestGenerateReportDTO(
        @NotNull ReportType type,
        @NotNull ReportRange range
        ){}

package org.wita.erp.infra.providers.report;

import org.wita.erp.domain.entities.report.dto.AccountReport;

import java.util.List;

public interface ReportProvider {
     byte[] exportExcel(List<AccountReport> accounts);
}

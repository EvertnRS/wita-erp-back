package org.wita.erp.infra.providers.report;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.wita.erp.domain.entities.report.dto.AccountReport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportProvider implements ReportProvider {

    public byte[] exportExcel(List<AccountReport> data) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Accounts");

            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Amount");
            header.createCell(2).setCellValue("Due Date");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Type");

            for (var item : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.id().toString());
                row.createCell(1).setCellValue(item.value().doubleValue());
                row.createCell(2).setCellValue(item.dueDate().toString());
                row.createCell(3).setCellValue(item.status().toString());
                row.createCell(4).setCellValue(item.type());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException("Error generating Excel", e);
        }
    }
}

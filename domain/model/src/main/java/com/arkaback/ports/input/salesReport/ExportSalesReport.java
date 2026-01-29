package com.arkaback.ports.input.salesReport;

import com.arkaback.entity.salesReport.ReportFormat;
import com.arkaback.entity.salesReport.SalesReport;

public interface ExportSalesReport {
    byte[] export(SalesReport report, ReportFormat format);
    String generateFileName(SalesReport report, ReportFormat format);
}

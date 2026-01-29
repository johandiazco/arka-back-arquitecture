package com.arkaback.useCase.salesReport;

import com.arkaback.entity.salesReport.ReportFormat;
import com.arkaback.entity.salesReport.SalesReport;
import com.arkaback.ports.input.salesReport.ExportSalesReport;
import lombok.AllArgsConstructor;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public class ExportSalesReportUseCase implements ExportSalesReport {

    @Override
    public byte[] export(SalesReport report, ReportFormat format) {

        if (report == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo");
        }
        if (format == null) {
            throw new IllegalArgumentException("El formato de exportación es obligatorio");
        }

        report.validate();

        throw new UnsupportedOperationException(
                "La exportación debe ser manejada por adaptadores de infraestructura"
        );
    }

    @Override
    public String generateFileName(SalesReport report, ReportFormat format) {

        if (report == null || format == null) {
            throw new IllegalArgumentException("El reporte y el formato son obligatorios");
        }

        //Formato reporte_ventas_2025_01_20_26.csv
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String startDateStr = report.getStartDate().format(formatter);
        String endDateStr = report.getEndDate().format(formatter);
        String extension = format.name().toLowerCase();

        return String.format("reporte_ventas_%s_%s.%s",
                startDateStr,
                endDateStr,
                extension);
    }
}
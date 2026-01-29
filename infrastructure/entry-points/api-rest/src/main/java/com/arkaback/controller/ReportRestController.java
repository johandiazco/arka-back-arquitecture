package com.arkaback.controller;

import com.arkaback.dto.salesReport.SalesReportResponse;
import com.arkaback.entity.salesReport.ReportFormat;
import com.arkaback.entity.salesReport.SalesReport;
import com.arkaback.mapper.SalesReportDtoMapper;
import com.arkaback.ports.input.salesReport.ExportSalesReport;
import com.arkaback.ports.input.salesReport.GenerateWeeklySalesReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportRestController {

    private final GenerateWeeklySalesReport generateWeeklySalesReport;
    private final ExportSalesReport exportSalesReport;
    private final SalesReportDtoMapper mapper;

    //reporte de ventas para un rango de fechas personalizado
    @GetMapping("/weekly-sales")
    public ResponseEntity<SalesReportResponse> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Generando reporte de ventas: {} - {}", startDate, endDate);
        SalesReport report = generateWeeklySalesReport.generate(startDate, endDate);
        log.info("Reporte generado exitosamente. Total ventas: ${}", report.getTotalSales());

        return ResponseEntity.ok(mapper.toResponse(report));
    }

    //Genera un reporte de ventas para la semana actual de Lunes-Domingo
    @GetMapping("/weekly-sales/current")
    public ResponseEntity<SalesReportResponse> generateCurrentWeekReport() {
        log.info("Generando reporte de la semana actual");
        SalesReport report = generateWeeklySalesReport.generateCurrentWeek();
        return ResponseEntity.ok(mapper.toResponse(report));
    }

    //Genera un reporte de ventas para la semana anterior
    @GetMapping("/weekly-sales/previous")
    public ResponseEntity<SalesReportResponse> generatePreviousWeekReport() {
        log.info("Generando reporte de la semana anterior");
        SalesReport report = generateWeeklySalesReport.generatePreviousWeek();
        return ResponseEntity.ok(mapper.toResponse(report));
    }

    //Exporta un reporte de ventas
    @GetMapping("/weekly-sales/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "csv") String format) {

        log.info("Exportando reporte {} - {} en formato: {}", startDate, endDate, format);

        //Genera reporte
        SalesReport report = generateWeeklySalesReport.generate(startDate, endDate);

        //Exporta según formato
        byte[] fileContent;
        String contentType;
        String fileName;

        switch (format.toLowerCase()) {
            case "csv":
                fileContent = exportToCsv(report);
                contentType = "text/csv";
                fileName = exportSalesReport.generateFileName(report, ReportFormat.CSV);
                break;

            case "pdf":
            case "html":
                //HTML que puede imprimirse como PDF
                fileContent = exportToHtml(report);
                contentType = "text/html";
                fileName = exportSalesReport.generateFileName(report, ReportFormat.PDF)
                        .replace(".pdf", ".html");
                break;

            default:
                throw new IllegalArgumentException("Formato no soportado: " + format +
                        ". Usa 'csv' o 'pdf'");
        }

        log.info("Reporte exportado exitosamente: {}", fileName);

        //Configura headers para descarga
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(fileContent.length);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    //Exporta reporte de la semana actual
    @GetMapping("/weekly-sales/export/current")
    public ResponseEntity<byte[]> exportCurrentWeekReport(
            @RequestParam(defaultValue = "csv") String format) {

        SalesReport report = generateWeeklySalesReport.generateCurrentWeek();

        byte[] fileContent;
        String contentType;
        String fileName;

        if ("csv".equalsIgnoreCase(format)) {
            fileContent = exportToCsv(report);
            contentType = "text/csv";
            fileName = exportSalesReport.generateFileName(report, ReportFormat.CSV);
        } else {
            fileContent = exportToHtml(report);
            contentType = "text/html";
            fileName = exportSalesReport.generateFileName(report, ReportFormat.PDF)
                    .replace(".pdf", ".html");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    //METODO PRIVADO DE EXPORTACION

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    //Exporta un reporte a formato CSV
    private byte[] exportToCsv(SalesReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {

            //reconozca formato Excel reconozca UTF-8
            writer.write('\ufeff');

            //Encabezado
            writer.println("REPORTE DE VENTAS SEMANALES");
            writer.println("Período," + report.getStartDate().format(DATE_FORMATTER) +
                    " - " + report.getEndDate().format(DATE_FORMATTER));
            writer.println();

            //Métricas generales
            writer.println("RESUMEN GENERAL");
            writer.println("Métrica,Valor");
            writer.println("Total Ventas,$" + report.getTotalSales());
            writer.println("Total Órdenes," + report.getTotalOrders());
            writer.println("Total Productos Vendidos," + report.getTotalProducts());
            writer.println("Valor Promedio por Orden,$" + report.getAverageOrderValue());
            writer.println("Ventas Período Anterior,$" + report.getPreviousPeriodSales());
            writer.println("Crecimiento," + report.getSalesGrowthPercentage() + "%");
            writer.println();

            //Productos más vendidos
            writer.println("PRODUCTOS MÁS VENDIDOS (TOP 10)");
            writer.println("Ranking,SKU,Nombre,Categoría,Cantidad Vendida,Ingresos Totales");

            int rank = 1;
            for (SalesReport.TopProduct product : report.getTopProducts()) {
                writer.printf("%d,%s,%s,%s,%d,$%s%n",
                        rank++,
                        escapeCSV(product.getProductSku()),
                        escapeCSV(product.getProductName()),
                        escapeCSV(product.getCategoryName()),
                        product.getQuantitySold(),
                        product.getTotalRevenue()
                );
            }
            writer.println();

            //Clientes frecuentes
            writer.println("CLIENTES MÁS FRECUENTES (TOP 10)");
            writer.println("Ranking,Nombre,Email,Total Órdenes,Total Gastado");

            rank = 1;
            for (SalesReport.TopCustomer customer : report.getTopCustomers()) {
                writer.printf("%d,%s,%s,%d,$%s%n",
                        rank++,
                        escapeCSV(customer.getCustomerName()),
                        escapeCSV(customer.getCustomerEmail()),
                        customer.getTotalOrders(),
                        customer.getTotalSpent()
                );
            }

            writer.flush();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar CSV: " + e.getMessage(), e);
        }
    }

    //Exporta un reporte a formato HTML como PDF
    private byte[] exportToHtml(SalesReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {

            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='es'>");
            writer.println("<head>");
            writer.println("<meta charset='UTF-8'>");
            writer.println("<title>Reporte de Ventas</title>");
            writer.println("<style>");
            writer.println("body{font-family:Arial,sans-serif;padding:40px;background:#f5f5f5;}");
            writer.println(".header{background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:white;padding:30px;border-radius:10px;text-align:center;}");
            writer.println("table{width:100%;border-collapse:collapse;background:white;margin:20px 0;}");
            writer.println("th,td{padding:12px;text-align:left;border-bottom:1px solid #ddd;}");
            writer.println("th{background:#667eea;color:white;}");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");

            //Encabezado
            writer.println("<div class='header'>");
            writer.println("<h1>📊 Reporte de Ventas Semanales</h1>");
            writer.println("<p>Período: " + report.getStartDate().format(DATE_FORMATTER) +
                    " - " + report.getEndDate().format(DATE_FORMATTER) + "</p>");
            writer.println("</div>");

            //Métricas
            writer.println("<h2>Resumen General</h2>");
            writer.println("<table>");
            writer.println("<tr><th>Métrica</th><th>Valor</th></tr>");
            writer.println("<tr><td>Total Ventas</td><td>$" + report.getTotalSales() + "</td></tr>");
            writer.println("<tr><td>Total Órdenes</td><td>" + report.getTotalOrders() + "</td></tr>");
            writer.println("<tr><td>Crecimiento</td><td>" + report.getSalesGrowthPercentage() + "%</td></tr>");
            writer.println("</table>");

            //Productos
            writer.println("<h2>🏆 Productos Más Vendidos</h2>");
            writer.println("<table>");
            writer.println("<tr><th>#</th><th>Producto</th><th>Cantidad</th><th>Ingresos</th></tr>");
            int rank = 1;
            for (SalesReport.TopProduct p : report.getTopProducts()) {
                writer.println("<tr><td>" + rank++ + "</td><td>" + p.getProductName() +
                        "</td><td>" + p.getQuantitySold() + "</td><td>$" + p.getTotalRevenue() + "</td></tr>");
            }
            writer.println("</table>");

            writer.println("</body></html>");
            writer.flush();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar HTML: " + e.getMessage(), e);
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}
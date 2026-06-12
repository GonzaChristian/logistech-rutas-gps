package pe.edu.utp.logistech.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.edu.utp.logistech.dto.ReporteFiltroDto;
import pe.edu.utp.logistech.dto.ReporteRutaDto;

public final class ExcelReportUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] HEADERS = {
            "ID asignacion",
            "ID ruta",
            "Origen",
            "Destino",
            "Fecha programada",
            "Fecha asignacion",
            "Estado",
            "Conductor",
            "DNI",
            "Vehiculo",
            "Placa"
    };

    private ExcelReportUtil() {
    }

    public static Workbook crearLibroReporteRutas() {
        Workbook workbook = new XSSFWorkbook();
        workbook.createSheet("Reporte de rutas");
        return workbook;
    }

    public static byte[] exportarReporteRutas(List<ReporteRutaDto> filas, ReporteFiltroDto filtro) {
        try (Workbook workbook = crearLibroReporteRutas();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.getSheetAt(0);
            construirEncabezado(workbook, sheet, filtro);
            escribirTabla(workbook, sheet, filas);
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el reporte Excel", ex);
        }
    }

    private static void construirEncabezado(Workbook workbook, Sheet sheet, ReporteFiltroDto filtro) {
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);

        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("LOGISTECH - Reporte de rutas");
        titleCell.setCellStyle(titleStyle);

        Row filterRow = sheet.createRow(1);
        filterRow.createCell(0).setCellValue("Filtros");
        filterRow.createCell(1).setCellValue(describirFiltro(filtro));
    }

    private static void escribirTabla(Workbook workbook, Sheet sheet, List<ReporteRutaDto> filas) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row headerRow = sheet.createRow(3);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 4;
        for (ReporteRutaDto fila : filas) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(fila.getIdAsignacion());
            row.createCell(1).setCellValue(fila.getIdRuta());
            row.createCell(2).setCellValue(fila.getOrigen());
            row.createCell(3).setCellValue(fila.getDestino());
            row.createCell(4).setCellValue(formatDate(fila.getFechaProgramada()));
            row.createCell(5).setCellValue(formatDate(fila.getFechaAsignacion()));
            row.createCell(6).setCellValue(fila.getEstado().name());
            row.createCell(7).setCellValue(fila.getConductor());
            row.createCell(8).setCellValue(fila.getDniConductor());
            row.createCell(9).setCellValue(fila.getVehiculo());
            row.createCell(10).setCellValue(fila.getPlaca());
        }
    }

    private static String describirFiltro(ReporteFiltroDto filtro) {
        String estado = filtro.getEstado() == null ? "Todos" : filtro.getEstado().name();
        String conductor = filtro.getIdConductor() == null || filtro.getIdConductor() <= 0
                ? "Todos"
                : filtro.getIdConductor().toString();
        String inicio = filtro.getFechaInicio() == null ? "Sin inicio" : formatDate(filtro.getFechaInicio());
        String fin = filtro.getFechaFin() == null ? "Sin fin" : formatDate(filtro.getFechaFin());
        return "Estado: " + estado + " | Conductor ID: " + conductor + " | Fechas: " + inicio + " a " + fin;
    }

    private static String formatDate(java.time.LocalDate date) {
        return date == null ? "" : DATE_FORMATTER.format(date);
    }
}

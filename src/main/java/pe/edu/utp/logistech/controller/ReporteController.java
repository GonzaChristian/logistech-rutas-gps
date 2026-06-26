package pe.edu.utp.logistech.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.utp.logistech.dto.ReporteFiltroDto;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.service.ReporteService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public String index(@ModelAttribute("filtro") ReporteFiltroDto filtro, Model model) {
        try {
            cargarModelo(model, filtro);
        } catch (IllegalArgumentException ex) {
            cargarModeloConFilas(model, filtro, List.of());
            model.addAttribute("error", ex.getMessage());
        }
        return "reportes/index";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@ModelAttribute ReporteFiltroDto filtro) {
        byte[] contenido = reporteService.exportarRutasExcel(filtro);
        String fileName = "logistech_reporte_rutas_" + LocalDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(fileName).build().toString())
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(contenido);
    }

    private void cargarModelo(Model model, ReporteFiltroDto filtro) {
        cargarModeloConFilas(model, filtro, reporteService.consultarRutas(filtro));
    }

    private void cargarModeloConFilas(Model model, ReporteFiltroDto filtro, List<?> filas) {
        model.addAttribute("filtro", filtro);
        model.addAttribute("filas", filas);
        model.addAttribute("conductores", reporteService.listarConductores());
        model.addAttribute("estados", EstadoRuta.values());
        model.addAttribute("activeMenu", "reportes");
        model.addAttribute("pageTitle", "Reporte de rutas");
    }
}

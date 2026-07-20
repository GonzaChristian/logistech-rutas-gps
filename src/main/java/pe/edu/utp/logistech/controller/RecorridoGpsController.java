package pe.edu.utp.logistech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.logistech.dto.IncidenciaFormDto;
import pe.edu.utp.logistech.dto.RecorridoGpsFormDto;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.IncidenciaService;
import pe.edu.utp.logistech.service.RecorridoGpsService;

@Controller
@RequestMapping("/recorridos")
public class RecorridoGpsController {

    private final RecorridoGpsService recorridoGpsService;
    private final IncidenciaService incidenciaService;

    public RecorridoGpsController(RecorridoGpsService recorridoGpsService, IncidenciaService incidenciaService) {
        this.recorridoGpsService = recorridoGpsService;
        this.incidenciaService = incidenciaService;
    }

    @GetMapping
    public String index(Model model) {
        if (!model.containsAttribute("gpsForm")) {
            model.addAttribute("gpsForm", new RecorridoGpsFormDto());
        }
        if (!model.containsAttribute("incidenciaForm")) {
            model.addAttribute("incidenciaForm", new IncidenciaFormDto());
        }
        cargarModelo(model);
        return "recorridos/index";
    }

    @PostMapping("/gps")
    public String registrarGps(@ModelAttribute("gpsForm") RecorridoGpsFormDto form,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        try {
            recorridoGpsService.registrar(form);
            redirectAttributes.addFlashAttribute("success", "Posicion GPS registrada correctamente");
            return "redirect:/recorridos";
        } catch (IllegalArgumentException | LogistechException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("incidenciaForm", new IncidenciaFormDto());
            cargarModelo(model);
            return "recorridos/index";
        }
    }

    @PostMapping("/incidencias")
    public String registrarIncidencia(@ModelAttribute("incidenciaForm") IncidenciaFormDto form,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        try {
            incidenciaService.registrar(form);
            redirectAttributes.addFlashAttribute("success", "Incidencia registrada correctamente");
            return "redirect:/recorridos";
        } catch (IllegalArgumentException | LogistechException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("gpsForm", new RecorridoGpsFormDto());
            cargarModelo(model);
            return "recorridos/index";
        }
    }

    private void cargarModelo(Model model) {
        model.addAttribute("asignacionesControl", recorridoGpsService.listarAsignacionesEnControl());
        model.addAttribute("registrosGps", recorridoGpsService.listarRegistrosGps());
        model.addAttribute("incidencias", incidenciaService.listarIncidencias());
        model.addAttribute("tiposIncidencia", incidenciaService.listarTiposSugeridos());
        model.addAttribute("activeMenu", "recorridos");
        model.addAttribute("pageTitle", "Monitoreo GPS");
    }
}

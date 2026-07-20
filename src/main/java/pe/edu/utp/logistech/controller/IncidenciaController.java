package pe.edu.utp.logistech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.logistech.dto.IncidenciaFormDto;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.IncidenciaService;
import pe.edu.utp.logistech.service.RecorridoGpsService;

@Controller
@RequestMapping("/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;
    private final RecorridoGpsService recorridoGpsService;

    public IncidenciaController(IncidenciaService incidenciaService, RecorridoGpsService recorridoGpsService) {
        this.incidenciaService = incidenciaService;
        this.recorridoGpsService = recorridoGpsService;
    }

    @GetMapping
    public String index(Model model) {
        if (!model.containsAttribute("incidenciaForm")) {
            model.addAttribute("incidenciaForm", new IncidenciaFormDto());
        }
        cargarModelo(model);
        return "incidencias/index";
    }

    @PostMapping
    public String registrar(@ModelAttribute("incidenciaForm") IncidenciaFormDto form,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        try {
            incidenciaService.registrar(form);
            redirectAttributes.addFlashAttribute("success", "Incidencia registrada correctamente");
            return "redirect:/incidencias";
        } catch (IllegalArgumentException | LogistechException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "incidencias/index";
        }
    }

    private void cargarModelo(Model model) {
        model.addAttribute("asignacionesControl", recorridoGpsService.listarAsignacionesEnControl());
        model.addAttribute("incidencias", incidenciaService.listarIncidencias());
        model.addAttribute("tiposIncidencia", incidenciaService.listarTiposSugeridos());
        model.addAttribute("activeMenu", "incidencias");
        model.addAttribute("pageTitle", "Gestión de incidencias");
    }
}

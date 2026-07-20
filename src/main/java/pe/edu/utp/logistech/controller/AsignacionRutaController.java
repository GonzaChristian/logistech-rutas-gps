package pe.edu.utp.logistech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.logistech.dto.AsignacionRutaFormDto;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.AsignacionRutaService;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionRutaController {

    private final AsignacionRutaService asignacionRutaService;

    public AsignacionRutaController(AsignacionRutaService asignacionRutaService) {
        this.asignacionRutaService = asignacionRutaService;
    }

    @GetMapping
    public String index(Model model) {
        if (!model.containsAttribute("asignacionForm")) {
            model.addAttribute("asignacionForm", new AsignacionRutaFormDto());
        }
        cargarModelo(model);
        return "asignaciones/index";
    }

    @PostMapping
    public String registrar(@ModelAttribute("asignacionForm") AsignacionRutaFormDto form,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        try {
            asignacionRutaService.registrar(form);
            redirectAttributes.addFlashAttribute("success", "Ruta asignada correctamente");
            return "redirect:/asignaciones";
        } catch (IllegalArgumentException | LogistechException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "asignaciones/index";
        }
    }

    @PostMapping("/{idAsignacion}/estado")
    public String cambiarEstado(@PathVariable Long idAsignacion,
                                @RequestParam EstadoRuta estado,
                                RedirectAttributes redirectAttributes) {
        try {
            asignacionRutaService.cambiarEstado(idAsignacion, estado);
            redirectAttributes.addFlashAttribute("success", "Estado de asignacion actualizado");
        } catch (IllegalArgumentException | LogistechException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/asignaciones";
    }

    private void cargarModelo(Model model) {
        model.addAttribute("asignaciones", asignacionRutaService.listarAsignaciones());
        model.addAttribute("rutasDisponibles", asignacionRutaService.listarRutasDisponibles());
        model.addAttribute("conductoresDisponibles", asignacionRutaService.listarConductoresDisponibles());
        model.addAttribute("vehiculosDisponibles", asignacionRutaService.listarVehiculosDisponibles());
        model.addAttribute("estados", asignacionRutaService.listarEstadosGestion());
        model.addAttribute("activeMenu", "asignaciones");
        model.addAttribute("pageTitle", "Asignación de rutas");
    }
}

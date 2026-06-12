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
import pe.edu.utp.logistech.dto.ConductorFormDto;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.ConductorService;

@Controller
@RequestMapping("/conductores")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @GetMapping
    public String index(Model model) {
        if (!model.containsAttribute("conductorForm")) {
            model.addAttribute("conductorForm", new ConductorFormDto());
        }
        cargarModelo(model);
        return "conductores/index";
    }

    @GetMapping("/{idConductor}/editar")
    public String editar(@PathVariable Long idConductor, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("conductorForm", conductorService.obtenerFormulario(idConductor));
            cargarModelo(model);
            return "conductores/index";
        } catch (IllegalArgumentException | LogistechException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/conductores";
        }
    }

    @PostMapping
    public String registrar(@ModelAttribute("conductorForm") ConductorFormDto form,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        try {
            conductorService.registrar(form);
            redirectAttributes.addFlashAttribute("success", "Conductor registrado correctamente");
            return "redirect:/conductores";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "conductores/index";
        }
    }

    @PostMapping("/{idConductor}")
    public String actualizar(@PathVariable Long idConductor,
                             @ModelAttribute("conductorForm") ConductorFormDto form,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            conductorService.actualizar(idConductor, form);
            redirectAttributes.addFlashAttribute("success", "Conductor actualizado correctamente");
            return "redirect:/conductores";
        } catch (IllegalArgumentException | LogistechException ex) {
            form.setIdConductor(idConductor);
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "conductores/index";
        }
    }

    @PostMapping("/{idConductor}/estado")
    public String cambiarEstado(@PathVariable Long idConductor,
                                @RequestParam EstadoGeneral estado,
                                RedirectAttributes redirectAttributes) {
        try {
            conductorService.cambiarEstado(idConductor, estado);
            redirectAttributes.addFlashAttribute("success", "Estado del conductor actualizado");
        } catch (IllegalArgumentException | LogistechException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/conductores";
    }

    private void cargarModelo(Model model) {
        model.addAttribute("conductores", conductorService.listarConductores());
        model.addAttribute("estados", EstadoGeneral.values());
        model.addAttribute("activeMenu", "conductores");
        model.addAttribute("pageTitle", "Gestion de conductores");
    }
}

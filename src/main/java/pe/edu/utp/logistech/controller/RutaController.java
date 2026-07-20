package pe.edu.utp.logistech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.logistech.dto.RutaFormDto;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.RutaService;

@Controller
@RequestMapping("/rutas")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @GetMapping
    public String index(Model model) {
        if (!model.containsAttribute("rutaForm")) {
            model.addAttribute("rutaForm", new RutaFormDto());
        }
        cargarModelo(model);
        return "rutas/index";
    }

    @GetMapping("/{idRuta}/editar")
    public String editar(@PathVariable Long idRuta, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("rutaForm", rutaService.obtenerFormulario(idRuta));
            cargarModelo(model);
            return "rutas/index";
        } catch (IllegalArgumentException | LogistechException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/rutas";
        }
    }

    @PostMapping
    public String registrar(@ModelAttribute("rutaForm") RutaFormDto form,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        try {
            rutaService.registrar(form);
            redirectAttributes.addFlashAttribute("success", "Ruta registrada correctamente");
            return "redirect:/rutas";
        } catch (IllegalArgumentException | LogistechException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "rutas/index";
        }
    }

    @PostMapping("/{idRuta}")
    public String actualizar(@PathVariable Long idRuta,
                             @ModelAttribute("rutaForm") RutaFormDto form,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            rutaService.actualizar(idRuta, form);
            redirectAttributes.addFlashAttribute("success", "Ruta actualizada correctamente");
            return "redirect:/rutas";
        } catch (IllegalArgumentException | LogistechException ex) {
            form.setIdRuta(idRuta);
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "rutas/index";
        }
    }

    private void cargarModelo(Model model) {
        model.addAttribute("rutas", rutaService.listarRutas());
        model.addAttribute("activeMenu", "rutas");
        model.addAttribute("pageTitle", "Gestión de rutas");
    }
}

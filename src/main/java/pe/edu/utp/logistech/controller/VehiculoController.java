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
import pe.edu.utp.logistech.dto.VehiculoFormDto;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.VehiculoService;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public String index(Model model) {
        if (!model.containsAttribute("vehiculoForm")) {
            model.addAttribute("vehiculoForm", new VehiculoFormDto());
        }
        cargarModelo(model);
        return "vehiculos/index";
    }

    @GetMapping("/{idVehiculo}/editar")
    public String editar(@PathVariable Long idVehiculo, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("vehiculoForm", vehiculoService.obtenerFormulario(idVehiculo));
            cargarModelo(model);
            return "vehiculos/index";
        } catch (IllegalArgumentException | LogistechException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/vehiculos";
        }
    }

    @PostMapping
    public String registrar(@ModelAttribute("vehiculoForm") VehiculoFormDto form,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        try {
            vehiculoService.registrar(form);
            redirectAttributes.addFlashAttribute("success", "Vehiculo registrado correctamente");
            return "redirect:/vehiculos";
        } catch (IllegalArgumentException | LogistechException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "vehiculos/index";
        }
    }

    @PostMapping("/{idVehiculo}")
    public String actualizar(@PathVariable Long idVehiculo,
                             @ModelAttribute("vehiculoForm") VehiculoFormDto form,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            vehiculoService.actualizar(idVehiculo, form);
            redirectAttributes.addFlashAttribute("success", "Vehiculo actualizado correctamente");
            return "redirect:/vehiculos";
        } catch (IllegalArgumentException | LogistechException ex) {
            form.setIdVehiculo(idVehiculo);
            model.addAttribute("error", ex.getMessage());
            cargarModelo(model);
            return "vehiculos/index";
        }
    }

    @PostMapping("/{idVehiculo}/estado")
    public String cambiarEstado(@PathVariable Long idVehiculo,
                                @RequestParam EstadoVehiculo estado,
                                RedirectAttributes redirectAttributes) {
        try {
            vehiculoService.cambiarEstado(idVehiculo, estado);
            redirectAttributes.addFlashAttribute("success", "Estado del vehiculo actualizado");
        } catch (IllegalArgumentException | LogistechException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/vehiculos";
    }

    private void cargarModelo(Model model) {
        model.addAttribute("vehiculos", vehiculoService.listarVehiculos());
        model.addAttribute("estados", vehiculoService.listarEstadosGestion());
        model.addAttribute("activeMenu", "vehiculos");
        model.addAttribute("pageTitle", "Gestión de vehículos");
    }
}

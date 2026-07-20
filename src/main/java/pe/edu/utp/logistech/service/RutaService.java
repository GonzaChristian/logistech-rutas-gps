package pe.edu.utp.logistech.service;

import java.util.List;
import pe.edu.utp.logistech.dto.RutaFormDto;
import pe.edu.utp.logistech.entity.Ruta;

public interface RutaService {

    List<Ruta> listarRutas();

    RutaFormDto obtenerFormulario(Long idRuta);

    Ruta registrar(RutaFormDto form);

    Ruta actualizar(Long idRuta, RutaFormDto form);

    long contarRutas();
}

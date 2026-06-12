package pe.edu.utp.logistech.service;

import java.util.List;
import pe.edu.utp.logistech.dto.IncidenciaFormDto;
import pe.edu.utp.logistech.entity.Incidencia;

public interface IncidenciaService {

    List<Incidencia> listarIncidencias();

    Incidencia registrar(IncidenciaFormDto form);

    List<String> listarTiposSugeridos();

    long contarIncidencias();
}

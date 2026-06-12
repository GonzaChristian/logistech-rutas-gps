package pe.edu.utp.logistech.service;

import java.util.List;
import pe.edu.utp.logistech.dto.ConductorFormDto;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;

public interface ConductorService {

    List<Conductor> listarConductores();

    ConductorFormDto obtenerFormulario(Long idConductor);

    Conductor registrar(ConductorFormDto form);

    Conductor actualizar(Long idConductor, ConductorFormDto form);

    void cambiarEstado(Long idConductor, EstadoGeneral estado);

    long contarConductores();
}

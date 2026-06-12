package pe.edu.utp.logistech.service;

import java.util.List;
import pe.edu.utp.logistech.dto.AsignacionRutaFormDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

public interface AsignacionRutaService {

    List<AsignacionRuta> listarAsignaciones();

    AsignacionRuta registrar(AsignacionRutaFormDto form);

    void cambiarEstado(Long idAsignacion, EstadoRuta estado);

    List<Ruta> listarRutasDisponibles();

    List<Conductor> listarConductoresDisponibles();

    List<Vehiculo> listarVehiculosDisponibles();

    List<EstadoRuta> listarEstadosGestion();

    long contarAsignaciones();
}

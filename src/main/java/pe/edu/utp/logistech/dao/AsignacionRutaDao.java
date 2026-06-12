package pe.edu.utp.logistech.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

public interface AsignacionRutaDao {

    List<AsignacionRuta> listarTodos();

    Optional<AsignacionRuta> buscarPorId(Long idAsignacion);

    AsignacionRuta guardar(AsignacionRuta asignacionRuta);

    boolean existeRutaActiva(Long idRuta, Collection<EstadoRuta> estados);

    boolean existeConductorActivo(Long idConductor, Collection<EstadoRuta> estados);

    boolean existeVehiculoActivo(Long idVehiculo, Collection<EstadoRuta> estados);

    long contar();
}

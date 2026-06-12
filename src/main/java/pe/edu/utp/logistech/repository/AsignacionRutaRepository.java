package pe.edu.utp.logistech.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

public interface AsignacionRutaRepository extends JpaRepository<AsignacionRuta, Long> {

    @EntityGraph(attributePaths = {"ruta", "conductor", "vehiculo"})
    List<AsignacionRuta> findAllByOrderByFechaAsignacionDescIdAsignacionDesc();

    boolean existsByRuta_IdRutaAndEstadoIn(Long idRuta, Collection<EstadoRuta> estados);

    boolean existsByConductor_IdConductorAndEstadoIn(Long idConductor, Collection<EstadoRuta> estados);

    boolean existsByVehiculo_IdVehiculoAndEstadoIn(Long idVehiculo, Collection<EstadoRuta> estados);
}

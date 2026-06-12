package pe.edu.utp.logistech.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.AsignacionRutaDao;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.repository.AsignacionRutaRepository;

@Repository
public class AsignacionRutaDaoImpl implements AsignacionRutaDao {

    private final AsignacionRutaRepository asignacionRutaRepository;

    public AsignacionRutaDaoImpl(AsignacionRutaRepository asignacionRutaRepository) {
        this.asignacionRutaRepository = asignacionRutaRepository;
    }

    @Override
    public List<AsignacionRuta> listarTodos() {
        return asignacionRutaRepository.findAllByOrderByFechaAsignacionDescIdAsignacionDesc();
    }

    @Override
    public Optional<AsignacionRuta> buscarPorId(Long idAsignacion) {
        return asignacionRutaRepository.findById(idAsignacion);
    }

    @Override
    public AsignacionRuta guardar(AsignacionRuta asignacionRuta) {
        return asignacionRutaRepository.save(asignacionRuta);
    }

    @Override
    public boolean existeRutaActiva(Long idRuta, Collection<EstadoRuta> estados) {
        return asignacionRutaRepository.existsByRuta_IdRutaAndEstadoIn(idRuta, estados);
    }

    @Override
    public boolean existeConductorActivo(Long idConductor, Collection<EstadoRuta> estados) {
        return asignacionRutaRepository.existsByConductor_IdConductorAndEstadoIn(idConductor, estados);
    }

    @Override
    public boolean existeVehiculoActivo(Long idVehiculo, Collection<EstadoRuta> estados) {
        return asignacionRutaRepository.existsByVehiculo_IdVehiculoAndEstadoIn(idVehiculo, estados);
    }

    @Override
    public long contar() {
        return asignacionRutaRepository.count();
    }
}

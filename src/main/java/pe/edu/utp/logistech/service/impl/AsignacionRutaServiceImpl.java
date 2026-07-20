package pe.edu.utp.logistech.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.logistech.dao.AsignacionRutaDao;
import pe.edu.utp.logistech.dao.ConductorDao;
import pe.edu.utp.logistech.dao.RutaDao;
import pe.edu.utp.logistech.dao.VehiculoDao;
import pe.edu.utp.logistech.dto.AsignacionRutaFormDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.AsignacionRutaService;

@Service
@Transactional
public class AsignacionRutaServiceImpl implements AsignacionRutaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsignacionRutaServiceImpl.class);
    private static final List<EstadoRuta> ESTADOS_ACTIVOS = ImmutableList.of(
            EstadoRuta.PROGRAMADA,
            EstadoRuta.EN_CURSO
    );
    private static final List<EstadoRuta> ESTADOS_GESTION = ImmutableList.of(
            EstadoRuta.PROGRAMADA,
            EstadoRuta.EN_CURSO,
            EstadoRuta.FINALIZADA,
            EstadoRuta.CANCELADA
    );
    private static final Map<EstadoRuta, Set<EstadoRuta>> TRANSICIONES_VALIDAS = Map.of(
            EstadoRuta.PROGRAMADA, Set.of(EstadoRuta.EN_CURSO, EstadoRuta.CANCELADA),
            EstadoRuta.EN_CURSO, Set.of(EstadoRuta.FINALIZADA, EstadoRuta.CANCELADA),
            EstadoRuta.FINALIZADA, Set.of(),
            EstadoRuta.CANCELADA, Set.of()
    );

    private final AsignacionRutaDao asignacionRutaDao;
    private final RutaDao rutaDao;
    private final ConductorDao conductorDao;
    private final VehiculoDao vehiculoDao;

    public AsignacionRutaServiceImpl(AsignacionRutaDao asignacionRutaDao,
                                     RutaDao rutaDao,
                                     ConductorDao conductorDao,
                                     VehiculoDao vehiculoDao) {
        this.asignacionRutaDao = asignacionRutaDao;
        this.rutaDao = rutaDao;
        this.conductorDao = conductorDao;
        this.vehiculoDao = vehiculoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionRuta> listarAsignaciones() {
        return asignacionRutaDao.listarTodos();
    }

    @Override
    public AsignacionRuta registrar(AsignacionRutaFormDto form) {
        Preconditions.checkArgument(form != null, "El formulario es obligatorio");

        Ruta ruta = obtenerRuta(form.getIdRuta());
        Conductor conductor = obtenerConductor(form.getIdConductor());
        Vehiculo vehiculo = obtenerVehiculo(form.getIdVehiculo());

        validarRutaDisponible(ruta);
        validarConductorDisponible(conductor);
        validarVehiculoDisponible(vehiculo);

        AsignacionRuta asignacionRuta = new AsignacionRuta();
        asignacionRuta.setRuta(ruta);
        asignacionRuta.setConductor(conductor);
        asignacionRuta.setVehiculo(vehiculo);
        asignacionRuta.setFechaAsignacion(LocalDate.now());
        asignacionRuta.setEstado(EstadoRuta.PROGRAMADA);

        ruta.setEstado(EstadoRuta.PROGRAMADA);
        vehiculo.setEstado(EstadoVehiculo.ASIGNADO);

        vehiculoDao.guardar(vehiculo);
        rutaDao.guardar(ruta);
        AsignacionRuta guardada = asignacionRutaDao.guardar(asignacionRuta);
        LOGGER.info("Ruta {} asignada a conductor {} y vehiculo {}",
                ruta.getIdRuta(), conductor.getIdConductor(), vehiculo.getIdVehiculo());
        return guardada;
    }

    @Override
    public void cambiarEstado(Long idAsignacion, EstadoRuta estado) {
        validarEstadoGestion(estado);
        AsignacionRuta asignacionRuta = obtenerAsignacion(idAsignacion);
        validarTransicion(asignacionRuta.getEstado(), estado);
        asignacionRuta.setEstado(estado);
        asignacionRuta.getRuta().setEstado(estado);

        if (estado == EstadoRuta.FINALIZADA || estado == EstadoRuta.CANCELADA) {
            asignacionRuta.getVehiculo().setEstado(EstadoVehiculo.DISPONIBLE);
        } else {
            asignacionRuta.getVehiculo().setEstado(EstadoVehiculo.ASIGNADO);
        }

        vehiculoDao.guardar(asignacionRuta.getVehiculo());
        rutaDao.guardar(asignacionRuta.getRuta());
        asignacionRutaDao.guardar(asignacionRuta);
        LOGGER.info("Estado de asignacion {} cambiado a {}", idAsignacion, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ruta> listarRutasDisponibles() {
        return rutaDao.listarTodos().stream()
                .filter(ruta -> ruta.getEstado() == EstadoRuta.PROGRAMADA)
                .filter(ruta -> !asignacionRutaDao.existeRutaActiva(ruta.getIdRuta(), ESTADOS_ACTIVOS))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conductor> listarConductoresDisponibles() {
        return conductorDao.listarTodos().stream()
                .filter(conductor -> conductor.getEstado() == EstadoGeneral.ACTIVO)
                .filter(conductor -> !asignacionRutaDao.existeConductorActivo(conductor.getIdConductor(), ESTADOS_ACTIVOS))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculosDisponibles() {
        return vehiculoDao.listarTodos().stream()
                .filter(vehiculo -> vehiculo.getEstado() == EstadoVehiculo.DISPONIBLE)
                .filter(vehiculo -> !asignacionRutaDao.existeVehiculoActivo(vehiculo.getIdVehiculo(), ESTADOS_ACTIVOS))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoRuta> listarEstadosGestion() {
        return ESTADOS_GESTION;
    }

    @Override
    @Transactional(readOnly = true)
    public long contarAsignaciones() {
        return asignacionRutaDao.contar();
    }

    private AsignacionRuta obtenerAsignacion(Long idAsignacion) {
        Preconditions.checkArgument(idAsignacion != null && idAsignacion > 0, "La asignacion es obligatoria");
        return asignacionRutaDao.buscarPorId(idAsignacion)
                .orElseThrow(() -> new LogistechException("No se encontro la asignacion solicitada"));
    }

    private Ruta obtenerRuta(Long idRuta) {
        Preconditions.checkArgument(idRuta != null && idRuta > 0, "La ruta es obligatoria");
        return rutaDao.buscarPorId(idRuta)
                .orElseThrow(() -> new LogistechException("No se encontro la ruta solicitada"));
    }

    private Conductor obtenerConductor(Long idConductor) {
        Preconditions.checkArgument(idConductor != null && idConductor > 0, "El conductor es obligatorio");
        return conductorDao.buscarPorId(idConductor)
                .orElseThrow(() -> new LogistechException("No se encontro el conductor solicitado"));
    }

    private Vehiculo obtenerVehiculo(Long idVehiculo) {
        Preconditions.checkArgument(idVehiculo != null && idVehiculo > 0, "El vehiculo es obligatorio");
        return vehiculoDao.buscarPorId(idVehiculo)
                .orElseThrow(() -> new LogistechException("No se encontro el vehiculo solicitado"));
    }

    private void validarRutaDisponible(Ruta ruta) {
        Preconditions.checkArgument(ruta.getEstado() == EstadoRuta.PROGRAMADA,
                "La ruta debe estar PROGRAMADA para ser asignada");
        Preconditions.checkArgument(!asignacionRutaDao.existeRutaActiva(ruta.getIdRuta(), ESTADOS_ACTIVOS),
                "La ruta ya tiene una asignacion activa");
    }

    private void validarConductorDisponible(Conductor conductor) {
        Preconditions.checkArgument(conductor.getEstado() == EstadoGeneral.ACTIVO,
                "El conductor debe estar ACTIVO");
        Preconditions.checkArgument(!asignacionRutaDao.existeConductorActivo(conductor.getIdConductor(), ESTADOS_ACTIVOS),
                "El conductor ya tiene una ruta activa");
    }

    private void validarVehiculoDisponible(Vehiculo vehiculo) {
        Preconditions.checkArgument(vehiculo.getEstado() == EstadoVehiculo.DISPONIBLE,
                "El vehiculo debe estar DISPONIBLE");
        Preconditions.checkArgument(!asignacionRutaDao.existeVehiculoActivo(vehiculo.getIdVehiculo(), ESTADOS_ACTIVOS),
                "El vehiculo ya tiene una ruta activa");
    }

    private void validarEstadoGestion(EstadoRuta estado) {
        Preconditions.checkArgument(estado != null, "El estado es obligatorio");
        Preconditions.checkArgument(ESTADOS_GESTION.contains(estado), "El estado de la asignacion no es valido");
    }

    private void validarTransicion(EstadoRuta estadoActual, EstadoRuta estadoNuevo) {
        Set<EstadoRuta> destinosPermitidos = TRANSICIONES_VALIDAS.getOrDefault(estadoActual, Set.of());
        Preconditions.checkArgument(destinosPermitidos.contains(estadoNuevo),
                "No se puede cambiar una asignacion de %s a %s", estadoActual, estadoNuevo);
    }
}

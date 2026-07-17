package pe.edu.utp.logistech.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.logistech.dao.ConductorDao;
import pe.edu.utp.logistech.dao.AsignacionRutaDao;
import pe.edu.utp.logistech.dto.ConductorFormDto;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.ConductorService;

@Service
@Transactional
public class ConductorServiceImpl implements ConductorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConductorServiceImpl.class);
    private static final List<EstadoRuta> ESTADOS_ACTIVOS = ImmutableList.of(
            EstadoRuta.PROGRAMADA,
            EstadoRuta.EN_CURSO
    );

    private final ConductorDao conductorDao;
    private final AsignacionRutaDao asignacionRutaDao;

    public ConductorServiceImpl(ConductorDao conductorDao, AsignacionRutaDao asignacionRutaDao) {
        this.conductorDao = conductorDao;
        this.asignacionRutaDao = asignacionRutaDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conductor> listarConductores() {
        return conductorDao.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public ConductorFormDto obtenerFormulario(Long idConductor) {
        Conductor conductor = obtenerConductor(idConductor);
        return ConductorFormDto.desdeEntidad(conductor);
    }

    @Override
    public Conductor registrar(ConductorFormDto form) {
        Conductor conductor = new Conductor();
        aplicarFormulario(conductor, form);
        Preconditions.checkArgument(!conductorDao.existeDni(conductor.getDni()), "El DNI ya esta registrado");
        Conductor guardado = conductorDao.guardar(conductor);
        LOGGER.info("Conductor registrado con DNI {}", guardado.getDni());
        return guardado;
    }

    @Override
    public Conductor actualizar(Long idConductor, ConductorFormDto form) {
        Conductor conductor = obtenerConductor(idConductor);
        validarCambioEstadoConAsignacionActiva(idConductor, form == null ? null : form.getEstado());
        aplicarFormulario(conductor, form);
        Preconditions.checkArgument(
                !conductorDao.existeDniEnOtroConductor(conductor.getDni(), idConductor),
                "El DNI ya pertenece a otro conductor"
        );
        Conductor actualizado = conductorDao.guardar(conductor);
        LOGGER.info("Conductor actualizado con id {}", actualizado.getIdConductor());
        return actualizado;
    }

    @Override
    public void cambiarEstado(Long idConductor, EstadoGeneral estado) {
        Preconditions.checkArgument(estado != null, "El estado es obligatorio");
        Conductor conductor = obtenerConductor(idConductor);
        validarCambioEstadoConAsignacionActiva(idConductor, estado);
        conductor.setEstado(estado);
        conductorDao.guardar(conductor);
        LOGGER.info("Estado de conductor {} cambiado a {}", idConductor, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarConductores() {
        return conductorDao.contar();
    }

    private Conductor obtenerConductor(Long idConductor) {
        Preconditions.checkArgument(idConductor != null && idConductor > 0, "El conductor es obligatorio");
        return conductorDao.buscarPorId(idConductor)
                .orElseThrow(() -> new LogistechException("No se encontro el conductor solicitado"));
    }

    private void aplicarFormulario(Conductor conductor, ConductorFormDto form) {
        Preconditions.checkArgument(form != null, "El formulario es obligatorio");

        String nombres = validarTexto(form.getNombres(), "Los nombres");
        String apellidos = validarTexto(form.getApellidos(), "Los apellidos");
        String dni = validarTexto(form.getDni(), "El DNI");
        String licencia = validarTexto(form.getLicencia(), "La licencia");
        String telefono = StringUtils.trimToNull(form.getTelefono());
        EstadoGeneral estado = form.getEstado() == null ? EstadoGeneral.ACTIVO : form.getEstado();

        Preconditions.checkArgument(StringUtils.length(dni) >= 8 && StringUtils.length(dni) <= 15,
                "El DNI debe tener entre 8 y 15 caracteres");
        Preconditions.checkArgument(StringUtils.length(licencia) <= 30, "La licencia no debe superar 30 caracteres");
        Preconditions.checkArgument(telefono == null || StringUtils.length(telefono) <= 20,
                "El telefono no debe superar 20 caracteres");

        conductor.setNombres(nombres);
        conductor.setApellidos(apellidos);
        conductor.setDni(dni);
        conductor.setLicencia(licencia);
        conductor.setTelefono(telefono);
        conductor.setEstado(estado);
    }

    private String validarTexto(String valor, String campo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(valor), "%s es obligatorio", campo);
        return StringUtils.trim(valor);
    }

    private void validarCambioEstadoConAsignacionActiva(Long idConductor, EstadoGeneral estadoSolicitado) {
        if (estadoSolicitado == EstadoGeneral.INACTIVO) {
            Preconditions.checkArgument(
                    !asignacionRutaDao.existeConductorActivo(idConductor, ESTADOS_ACTIVOS),
                    "No se puede desactivar un conductor con una asignacion activa"
            );
        }
    }
}

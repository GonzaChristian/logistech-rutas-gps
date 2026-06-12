package pe.edu.utp.logistech.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.logistech.dao.AsignacionRutaDao;
import pe.edu.utp.logistech.dao.IncidenciaDao;
import pe.edu.utp.logistech.dto.IncidenciaFormDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Incidencia;
import pe.edu.utp.logistech.entity.enums.EstadoIncidencia;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.IncidenciaService;

@Service
@Transactional
public class IncidenciaServiceImpl implements IncidenciaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncidenciaServiceImpl.class);
    private static final List<String> TIPOS_SUGERIDOS = ImmutableList.of(
            "RETRASO",
            "DESVIO",
            "AVERIA",
            "ACCIDENTE",
            "OTRO"
    );
    private static final List<EstadoRuta> ESTADOS_EN_CONTROL = ImmutableList.of(
            EstadoRuta.PROGRAMADA,
            EstadoRuta.EN_CURSO
    );

    private final IncidenciaDao incidenciaDao;
    private final AsignacionRutaDao asignacionRutaDao;

    public IncidenciaServiceImpl(IncidenciaDao incidenciaDao, AsignacionRutaDao asignacionRutaDao) {
        this.incidenciaDao = incidenciaDao;
        this.asignacionRutaDao = asignacionRutaDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Incidencia> listarIncidencias() {
        return incidenciaDao.listarTodos();
    }

    @Override
    public Incidencia registrar(IncidenciaFormDto form) {
        Preconditions.checkArgument(form != null, "El formulario es obligatorio");
        AsignacionRuta asignacionRuta = obtenerAsignacion(form.getIdAsignacion());
        validarAsignacionEnControl(asignacionRuta);

        String tipo = StringUtils.upperCase(validarTexto(form.getTipo(), "El tipo"));
        String descripcion = StringUtils.trimToNull(form.getDescripcion());
        Preconditions.checkArgument(StringUtils.length(tipo) <= 50, "El tipo no debe superar 50 caracteres");
        Preconditions.checkArgument(descripcion == null || StringUtils.length(descripcion) <= 250,
                "La descripcion no debe superar 250 caracteres");

        Incidencia incidencia = new Incidencia();
        incidencia.setAsignacion(asignacionRuta);
        incidencia.setTipo(tipo);
        incidencia.setDescripcion(descripcion);
        incidencia.setFechaHora(LocalDateTime.now());
        incidencia.setEstado(EstadoIncidencia.PENDIENTE);

        iniciarRecorridoSiCorresponde(asignacionRuta);
        asignacionRutaDao.guardar(asignacionRuta);
        Incidencia guardada = incidenciaDao.guardar(incidencia);
        LOGGER.warn("Incidencia {} registrada para asignacion {}",
                guardada.getTipo(), asignacionRuta.getIdAsignacion());
        return guardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarTiposSugeridos() {
        return TIPOS_SUGERIDOS;
    }

    @Override
    @Transactional(readOnly = true)
    public long contarIncidencias() {
        return incidenciaDao.contar();
    }

    private AsignacionRuta obtenerAsignacion(Long idAsignacion) {
        Preconditions.checkArgument(idAsignacion != null && idAsignacion > 0, "La asignacion es obligatoria");
        return asignacionRutaDao.buscarPorId(idAsignacion)
                .orElseThrow(() -> new LogistechException("No se encontro la asignacion solicitada"));
    }

    private void validarAsignacionEnControl(AsignacionRuta asignacionRuta) {
        Preconditions.checkArgument(ESTADOS_EN_CONTROL.contains(asignacionRuta.getEstado()),
                "Solo se pueden registrar incidencias en rutas PROGRAMADAS o EN_CURSO");
    }

    private void iniciarRecorridoSiCorresponde(AsignacionRuta asignacionRuta) {
        if (asignacionRuta.getEstado() == EstadoRuta.PROGRAMADA) {
            asignacionRuta.setEstado(EstadoRuta.EN_CURSO);
            asignacionRuta.getRuta().setEstado(EstadoRuta.EN_CURSO);
            LOGGER.info("Asignacion {} iniciada automaticamente por incidencia",
                    asignacionRuta.getIdAsignacion());
        }
    }

    private String validarTexto(String valor, String campo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(valor), "%s es obligatorio", campo);
        return StringUtils.trim(valor);
    }
}

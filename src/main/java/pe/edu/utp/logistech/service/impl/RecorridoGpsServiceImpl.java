package pe.edu.utp.logistech.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.logistech.dao.AsignacionRutaDao;
import pe.edu.utp.logistech.dao.RecorridoGpsDao;
import pe.edu.utp.logistech.dto.RecorridoGpsFormDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.RecorridoGps;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.RecorridoGpsService;

@Service
@Transactional
public class RecorridoGpsServiceImpl implements RecorridoGpsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecorridoGpsServiceImpl.class);
    private static final BigDecimal LATITUD_MINIMA = new BigDecimal("-90");
    private static final BigDecimal LATITUD_MAXIMA = new BigDecimal("90");
    private static final BigDecimal LONGITUD_MINIMA = new BigDecimal("-180");
    private static final BigDecimal LONGITUD_MAXIMA = new BigDecimal("180");
    private static final List<EstadoRuta> ESTADOS_EN_CONTROL = ImmutableList.of(
            EstadoRuta.PROGRAMADA,
            EstadoRuta.EN_CURSO
    );

    private final RecorridoGpsDao recorridoGpsDao;
    private final AsignacionRutaDao asignacionRutaDao;

    public RecorridoGpsServiceImpl(RecorridoGpsDao recorridoGpsDao, AsignacionRutaDao asignacionRutaDao) {
        this.recorridoGpsDao = recorridoGpsDao;
        this.asignacionRutaDao = asignacionRutaDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecorridoGps> listarRegistrosGps() {
        return recorridoGpsDao.listarTodos();
    }

    @Override
    public RecorridoGps registrar(RecorridoGpsFormDto form) {
        Preconditions.checkArgument(form != null, "El formulario es obligatorio");
        AsignacionRuta asignacionRuta = obtenerAsignacion(form.getIdAsignacion());
        validarAsignacionEnControl(asignacionRuta);

        RecorridoGps recorridoGps = new RecorridoGps();
        recorridoGps.setAsignacion(asignacionRuta);
        recorridoGps.setLatitud(validarCoordenada(form.getLatitud(), LATITUD_MINIMA, LATITUD_MAXIMA, "La latitud"));
        recorridoGps.setLongitud(validarCoordenada(form.getLongitud(), LONGITUD_MINIMA, LONGITUD_MAXIMA, "La longitud"));
        recorridoGps.setFechaHora(LocalDateTime.now());

        iniciarRecorridoSiCorresponde(asignacionRuta);
        asignacionRutaDao.guardar(asignacionRuta);
        RecorridoGps guardado = recorridoGpsDao.guardar(recorridoGps);
        LOGGER.info("GPS registrado para asignacion {} en latitud {}, longitud {}",
                asignacionRuta.getIdAsignacion(), guardado.getLatitud(), guardado.getLongitud());
        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionRuta> listarAsignacionesEnControl() {
        return asignacionRutaDao.listarTodos().stream()
                .filter(asignacion -> ESTADOS_EN_CONTROL.contains(asignacion.getEstado()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarRegistrosGps() {
        return recorridoGpsDao.contar();
    }

    private AsignacionRuta obtenerAsignacion(Long idAsignacion) {
        Preconditions.checkArgument(idAsignacion != null && idAsignacion > 0, "La asignacion es obligatoria");
        return asignacionRutaDao.buscarPorId(idAsignacion)
                .orElseThrow(() -> new LogistechException("No se encontro la asignacion solicitada"));
    }

    private void validarAsignacionEnControl(AsignacionRuta asignacionRuta) {
        Preconditions.checkArgument(ESTADOS_EN_CONTROL.contains(asignacionRuta.getEstado()),
                "Solo se puede registrar GPS en rutas PROGRAMADAS o EN_CURSO");
    }

    private void iniciarRecorridoSiCorresponde(AsignacionRuta asignacionRuta) {
        if (asignacionRuta.getEstado() == EstadoRuta.PROGRAMADA) {
            asignacionRuta.setEstado(EstadoRuta.EN_CURSO);
            asignacionRuta.getRuta().setEstado(EstadoRuta.EN_CURSO);
            LOGGER.info("Asignacion {} iniciada automaticamente por registro GPS",
                    asignacionRuta.getIdAsignacion());
        }
    }

    private BigDecimal validarCoordenada(BigDecimal valor, BigDecimal minimo, BigDecimal maximo, String campo) {
        Preconditions.checkArgument(valor != null, "%s es obligatoria", campo);
        Preconditions.checkArgument(valor.compareTo(minimo) >= 0 && valor.compareTo(maximo) <= 0,
                "%s debe estar entre %s y %s", campo, minimo, maximo);
        return valor.setScale(8, RoundingMode.HALF_UP);
    }
}

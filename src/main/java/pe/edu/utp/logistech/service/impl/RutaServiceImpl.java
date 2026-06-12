package pe.edu.utp.logistech.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.logistech.dao.RutaDao;
import pe.edu.utp.logistech.dto.RutaFormDto;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.RutaService;

@Service
@Transactional
public class RutaServiceImpl implements RutaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RutaServiceImpl.class);
    private static final List<EstadoRuta> ESTADOS_GESTION = ImmutableList.of(
            EstadoRuta.PROGRAMADA,
            EstadoRuta.EN_CURSO,
            EstadoRuta.FINALIZADA,
            EstadoRuta.CANCELADA
    );

    private final RutaDao rutaDao;

    public RutaServiceImpl(RutaDao rutaDao) {
        this.rutaDao = rutaDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ruta> listarRutas() {
        return rutaDao.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public RutaFormDto obtenerFormulario(Long idRuta) {
        Ruta ruta = obtenerRuta(idRuta);
        return RutaFormDto.desdeEntidad(ruta);
    }

    @Override
    public Ruta registrar(RutaFormDto form) {
        Ruta ruta = new Ruta();
        aplicarFormulario(ruta, form);
        Ruta guardada = rutaDao.guardar(ruta);
        LOGGER.info("Ruta registrada de {} a {}", guardada.getOrigen(), guardada.getDestino());
        return guardada;
    }

    @Override
    public Ruta actualizar(Long idRuta, RutaFormDto form) {
        Ruta ruta = obtenerRuta(idRuta);
        aplicarFormulario(ruta, form);
        Ruta actualizada = rutaDao.guardar(ruta);
        LOGGER.info("Ruta actualizada con id {}", actualizada.getIdRuta());
        return actualizada;
    }

    @Override
    public void cambiarEstado(Long idRuta, EstadoRuta estado) {
        validarEstadoGestion(estado);
        Ruta ruta = obtenerRuta(idRuta);
        ruta.setEstado(estado);
        rutaDao.guardar(ruta);
        LOGGER.info("Estado de ruta {} cambiado a {}", idRuta, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoRuta> listarEstadosGestion() {
        return ESTADOS_GESTION;
    }

    @Override
    @Transactional(readOnly = true)
    public long contarRutas() {
        return rutaDao.contar();
    }

    private Ruta obtenerRuta(Long idRuta) {
        Preconditions.checkArgument(idRuta != null && idRuta > 0, "La ruta es obligatoria");
        return rutaDao.buscarPorId(idRuta)
                .orElseThrow(() -> new LogistechException("No se encontro la ruta solicitada"));
    }

    private void aplicarFormulario(Ruta ruta, RutaFormDto form) {
        Preconditions.checkArgument(form != null, "El formulario es obligatorio");

        String origen = validarTexto(form.getOrigen(), "El origen");
        String destino = validarTexto(form.getDestino(), "El destino");
        LocalDate fechaProgramada = form.getFechaProgramada();
        EstadoRuta estado = form.getEstado() == null ? EstadoRuta.PROGRAMADA : form.getEstado();
        validarEstadoGestion(estado);

        Preconditions.checkArgument(StringUtils.length(origen) <= 150, "El origen no debe superar 150 caracteres");
        Preconditions.checkArgument(StringUtils.length(destino) <= 150, "El destino no debe superar 150 caracteres");
        Preconditions.checkArgument(!StringUtils.equalsIgnoreCase(origen, destino),
                "El origen y destino no pueden ser iguales");
        Preconditions.checkArgument(fechaProgramada != null, "La fecha programada es obligatoria");

        ruta.setOrigen(origen);
        ruta.setDestino(destino);
        ruta.setFechaProgramada(fechaProgramada);
        ruta.setEstado(estado);
    }

    private void validarEstadoGestion(EstadoRuta estado) {
        Preconditions.checkArgument(estado != null, "El estado es obligatorio");
        Preconditions.checkArgument(ESTADOS_GESTION.contains(estado), "El estado de la ruta no es valido");
    }

    private String validarTexto(String valor, String campo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(valor), "%s es obligatorio", campo);
        return StringUtils.trim(valor);
    }
}

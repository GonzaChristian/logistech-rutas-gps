package pe.edu.utp.logistech.service.impl;

import com.google.common.base.Preconditions;
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

        Preconditions.checkArgument(StringUtils.length(origen) <= 150, "El origen no debe superar 150 caracteres");
        Preconditions.checkArgument(StringUtils.length(destino) <= 150, "El destino no debe superar 150 caracteres");
        Preconditions.checkArgument(!StringUtils.equalsIgnoreCase(origen, destino),
                "El origen y destino no pueden ser iguales");
        Preconditions.checkArgument(fechaProgramada != null, "La fecha programada es obligatoria");

        ruta.setOrigen(origen);
        ruta.setDestino(destino);
        ruta.setFechaProgramada(fechaProgramada);
        if (ruta.getIdRuta() == null) {
            ruta.setEstado(EstadoRuta.PROGRAMADA);
        }
    }

    private String validarTexto(String valor, String campo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(valor), "%s es obligatorio", campo);
        return StringUtils.trim(valor);
    }
}

package pe.edu.utp.logistech.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.logistech.dao.VehiculoDao;
import pe.edu.utp.logistech.dao.AsignacionRutaDao;
import pe.edu.utp.logistech.dto.VehiculoFormDto;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.VehiculoService;

@Service
@Transactional
public class VehiculoServiceImpl implements VehiculoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehiculoServiceImpl.class);
    private static final List<EstadoVehiculo> ESTADOS_GESTION = ImmutableList.of(
            EstadoVehiculo.DISPONIBLE,
            EstadoVehiculo.MANTENIMIENTO,
            EstadoVehiculo.INACTIVO
    );
    private static final List<EstadoRuta> ESTADOS_ACTIVOS = ImmutableList.of(
            EstadoRuta.PROGRAMADA,
            EstadoRuta.EN_CURSO
    );

    private final VehiculoDao vehiculoDao;
    private final AsignacionRutaDao asignacionRutaDao;

    public VehiculoServiceImpl(VehiculoDao vehiculoDao, AsignacionRutaDao asignacionRutaDao) {
        this.vehiculoDao = vehiculoDao;
        this.asignacionRutaDao = asignacionRutaDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> listarVehiculos() {
        return vehiculoDao.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoFormDto obtenerFormulario(Long idVehiculo) {
        Vehiculo vehiculo = obtenerVehiculo(idVehiculo);
        return VehiculoFormDto.desdeEntidad(vehiculo);
    }

    @Override
    public Vehiculo registrar(VehiculoFormDto form) {
        Vehiculo vehiculo = new Vehiculo();
        aplicarFormulario(vehiculo, form);
        Preconditions.checkArgument(!vehiculoDao.existePlaca(vehiculo.getPlaca()), "La placa ya esta registrada");
        Vehiculo guardado = vehiculoDao.guardar(vehiculo);
        LOGGER.info("Vehiculo registrado con placa {}", guardado.getPlaca());
        return guardado;
    }

    @Override
    public Vehiculo actualizar(Long idVehiculo, VehiculoFormDto form) {
        Vehiculo vehiculo = obtenerVehiculo(idVehiculo);
        validarVehiculoSinAsignacionActiva(idVehiculo);
        aplicarFormulario(vehiculo, form);
        Preconditions.checkArgument(
                !vehiculoDao.existePlacaEnOtroVehiculo(vehiculo.getPlaca(), idVehiculo),
                "La placa ya pertenece a otro vehiculo"
        );
        Vehiculo actualizado = vehiculoDao.guardar(vehiculo);
        LOGGER.info("Vehiculo actualizado con id {}", actualizado.getIdVehiculo());
        return actualizado;
    }

    @Override
    public void cambiarEstado(Long idVehiculo, EstadoVehiculo estado) {
        validarEstadoGestion(estado);
        Vehiculo vehiculo = obtenerVehiculo(idVehiculo);
        validarVehiculoSinAsignacionActiva(idVehiculo);
        vehiculo.setEstado(estado);
        vehiculoDao.guardar(vehiculo);
        LOGGER.info("Estado de vehiculo {} cambiado a {}", idVehiculo, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoVehiculo> listarEstadosGestion() {
        return ESTADOS_GESTION;
    }

    @Override
    @Transactional(readOnly = true)
    public long contarVehiculos() {
        return vehiculoDao.contar();
    }

    private Vehiculo obtenerVehiculo(Long idVehiculo) {
        Preconditions.checkArgument(idVehiculo != null && idVehiculo > 0, "El vehiculo es obligatorio");
        return vehiculoDao.buscarPorId(idVehiculo)
                .orElseThrow(() -> new LogistechException("No se encontro el vehiculo solicitado"));
    }

    private void aplicarFormulario(Vehiculo vehiculo, VehiculoFormDto form) {
        Preconditions.checkArgument(form != null, "El formulario es obligatorio");

        String placa = StringUtils.upperCase(validarTexto(form.getPlaca(), "La placa"));
        String marca = validarTexto(form.getMarca(), "La marca");
        String modelo = validarTexto(form.getModelo(), "El modelo");
        EstadoVehiculo estado = form.getEstado() == null ? EstadoVehiculo.DISPONIBLE : form.getEstado();
        validarEstadoGestion(estado);

        String placaSinGuion = StringUtils.remove(placa, '-');
        Preconditions.checkArgument(StringUtils.length(placa) >= 6 && StringUtils.length(placa) <= 15,
                "La placa debe tener entre 6 y 15 caracteres");
        Preconditions.checkArgument(StringUtils.isAlphanumeric(placaSinGuion),
                "La placa solo debe contener letras, numeros o guion");
        Preconditions.checkArgument(StringUtils.length(marca) <= 50, "La marca no debe superar 50 caracteres");
        Preconditions.checkArgument(StringUtils.length(modelo) <= 50, "El modelo no debe superar 50 caracteres");

        vehiculo.setPlaca(placa);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setEstado(estado);
    }

    private void validarEstadoGestion(EstadoVehiculo estado) {
        Preconditions.checkArgument(estado != null, "El estado es obligatorio");
        Preconditions.checkArgument(ESTADOS_GESTION.contains(estado), "El estado del vehiculo no es valido");
    }

    private String validarTexto(String valor, String campo) {
        Preconditions.checkArgument(StringUtils.isNotBlank(valor), "%s es obligatorio", campo);
        return StringUtils.trim(valor);
    }

    private void validarVehiculoSinAsignacionActiva(Long idVehiculo) {
        Preconditions.checkArgument(
                !asignacionRutaDao.existeVehiculoActivo(idVehiculo, ESTADOS_ACTIVOS),
                "No se puede modificar un vehiculo con una asignacion activa"
        );
    }
}

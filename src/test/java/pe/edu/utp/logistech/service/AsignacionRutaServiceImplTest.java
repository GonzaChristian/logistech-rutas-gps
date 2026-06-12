package pe.edu.utp.logistech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import pe.edu.utp.logistech.service.impl.AsignacionRutaServiceImpl;

@ExtendWith(MockitoExtension.class)
class AsignacionRutaServiceImplTest {

    @Mock
    private AsignacionRutaDao asignacionRutaDao;

    @Mock
    private RutaDao rutaDao;

    @Mock
    private ConductorDao conductorDao;

    @Mock
    private VehiculoDao vehiculoDao;

    @InjectMocks
    private AsignacionRutaServiceImpl asignacionRutaService;

    @Test
    void registrarDebeAsignarRutaConConductorYVehiculoDisponibles() {
        Ruta ruta = rutaValida();
        Conductor conductor = conductorActivo();
        Vehiculo vehiculo = vehiculoDisponible();
        when(rutaDao.buscarPorId(1L)).thenReturn(Optional.of(ruta));
        when(conductorDao.buscarPorId(2L)).thenReturn(Optional.of(conductor));
        when(vehiculoDao.buscarPorId(3L)).thenReturn(Optional.of(vehiculo));
        when(asignacionRutaDao.existeRutaActiva(eq(1L), any())).thenReturn(false);
        when(asignacionRutaDao.existeConductorActivo(eq(2L), any())).thenReturn(false);
        when(asignacionRutaDao.existeVehiculoActivo(eq(3L), any())).thenReturn(false);
        when(asignacionRutaDao.guardar(any(AsignacionRuta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        asignacionRutaService.registrar(formValido());

        ArgumentCaptor<AsignacionRuta> captor = ArgumentCaptor.forClass(AsignacionRuta.class);
        verify(asignacionRutaDao).guardar(captor.capture());
        AsignacionRuta guardada = captor.getValue();
        assertThat(guardada.getRuta()).isSameAs(ruta);
        assertThat(guardada.getConductor()).isSameAs(conductor);
        assertThat(guardada.getVehiculo()).isSameAs(vehiculo);
        assertThat(guardada.getFechaAsignacion()).isEqualTo(LocalDate.now());
        assertThat(guardada.getEstado()).isEqualTo(EstadoRuta.PROGRAMADA);
        assertThat(vehiculo.getEstado()).isEqualTo(EstadoVehiculo.ASIGNADO);
        verify(vehiculoDao).guardar(vehiculo);
        verify(rutaDao).guardar(ruta);
    }

    @Test
    void registrarDebeRechazarConductorInactivo() {
        Ruta ruta = rutaValida();
        Conductor conductor = conductorActivo();
        conductor.setEstado(EstadoGeneral.INACTIVO);
        when(rutaDao.buscarPorId(1L)).thenReturn(Optional.of(ruta));
        when(conductorDao.buscarPorId(2L)).thenReturn(Optional.of(conductor));
        when(vehiculoDao.buscarPorId(3L)).thenReturn(Optional.of(vehiculoDisponible()));
        when(asignacionRutaDao.existeRutaActiva(eq(1L), any())).thenReturn(false);

        assertThatThrownBy(() -> asignacionRutaService.registrar(formValido()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("conductor");
    }

    @Test
    void registrarDebeRechazarVehiculoNoDisponible() {
        Ruta ruta = rutaValida();
        Vehiculo vehiculo = vehiculoDisponible();
        vehiculo.setEstado(EstadoVehiculo.MANTENIMIENTO);
        when(rutaDao.buscarPorId(1L)).thenReturn(Optional.of(ruta));
        when(conductorDao.buscarPorId(2L)).thenReturn(Optional.of(conductorActivo()));
        when(vehiculoDao.buscarPorId(3L)).thenReturn(Optional.of(vehiculo));
        when(asignacionRutaDao.existeRutaActiva(eq(1L), any())).thenReturn(false);
        when(asignacionRutaDao.existeConductorActivo(eq(2L), any())).thenReturn(false);

        assertThatThrownBy(() -> asignacionRutaService.registrar(formValido()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vehiculo");
    }

    @Test
    void registrarDebeRechazarRutaNoProgramada() {
        Ruta ruta = rutaValida();
        ruta.setEstado(EstadoRuta.EN_CURSO);
        when(rutaDao.buscarPorId(1L)).thenReturn(Optional.of(ruta));
        when(conductorDao.buscarPorId(2L)).thenReturn(Optional.of(conductorActivo()));
        when(vehiculoDao.buscarPorId(3L)).thenReturn(Optional.of(vehiculoDisponible()));

        assertThatThrownBy(() -> asignacionRutaService.registrar(formValido()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ruta");
    }

    @Test
    void cambiarEstadoFinalizadaDebeSincronizarRutaYLiberarVehiculo() {
        AsignacionRuta asignacion = asignacionExistente();
        when(asignacionRutaDao.buscarPorId(10L)).thenReturn(Optional.of(asignacion));

        asignacionRutaService.cambiarEstado(10L, EstadoRuta.FINALIZADA);

        assertThat(asignacion.getEstado()).isEqualTo(EstadoRuta.FINALIZADA);
        assertThat(asignacion.getRuta().getEstado()).isEqualTo(EstadoRuta.FINALIZADA);
        assertThat(asignacion.getVehiculo().getEstado()).isEqualTo(EstadoVehiculo.DISPONIBLE);
        verify(asignacionRutaDao).guardar(asignacion);
        verify(rutaDao).guardar(asignacion.getRuta());
        verify(vehiculoDao).guardar(asignacion.getVehiculo());
    }

    private AsignacionRutaFormDto formValido() {
        AsignacionRutaFormDto form = new AsignacionRutaFormDto();
        form.setIdRuta(1L);
        form.setIdConductor(2L);
        form.setIdVehiculo(3L);
        return form;
    }

    private AsignacionRuta asignacionExistente() {
        AsignacionRuta asignacion = new AsignacionRuta();
        asignacion.setIdAsignacion(10L);
        asignacion.setRuta(rutaValida());
        asignacion.setConductor(conductorActivo());
        asignacion.setVehiculo(vehiculoDisponible());
        asignacion.setFechaAsignacion(LocalDate.now());
        asignacion.setEstado(EstadoRuta.PROGRAMADA);
        return asignacion;
    }

    private Ruta rutaValida() {
        Ruta ruta = new Ruta();
        ruta.setIdRuta(1L);
        ruta.setOrigen("Tienda Norte");
        ruta.setDestino("Tienda Sur");
        ruta.setFechaProgramada(LocalDate.of(2026, 6, 12));
        ruta.setEstado(EstadoRuta.PROGRAMADA);
        return ruta;
    }

    private Conductor conductorActivo() {
        Conductor conductor = new Conductor();
        conductor.setIdConductor(2L);
        conductor.setNombres("Juan");
        conductor.setApellidos("Perez");
        conductor.setDni("74215689");
        conductor.setLicencia("A-IIb");
        conductor.setEstado(EstadoGeneral.ACTIVO);
        return conductor;
    }

    private Vehiculo vehiculoDisponible() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(3L);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setMarca("Hyundai");
        vehiculo.setModelo("H100");
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        return vehiculo;
    }
}

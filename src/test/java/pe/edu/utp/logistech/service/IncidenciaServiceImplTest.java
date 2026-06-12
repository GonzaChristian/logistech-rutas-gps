package pe.edu.utp.logistech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import pe.edu.utp.logistech.dao.IncidenciaDao;
import pe.edu.utp.logistech.dto.IncidenciaFormDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.Incidencia;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoIncidencia;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.service.impl.IncidenciaServiceImpl;

@ExtendWith(MockitoExtension.class)
class IncidenciaServiceImplTest {

    @Mock
    private IncidenciaDao incidenciaDao;

    @Mock
    private AsignacionRutaDao asignacionRutaDao;

    @InjectMocks
    private IncidenciaServiceImpl incidenciaService;

    @Test
    void registrarDebeGuardarIncidenciaPendienteEIniciarRecorrido() {
        AsignacionRuta asignacion = asignacion(EstadoRuta.PROGRAMADA);
        when(asignacionRutaDao.buscarPorId(1L)).thenReturn(Optional.of(asignacion));
        when(incidenciaDao.guardar(any(Incidencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        incidenciaService.registrar(formValido());

        ArgumentCaptor<Incidencia> captor = ArgumentCaptor.forClass(Incidencia.class);
        verify(incidenciaDao).guardar(captor.capture());
        Incidencia guardada = captor.getValue();
        assertThat(guardada.getAsignacion()).isSameAs(asignacion);
        assertThat(guardada.getTipo()).isEqualTo("RETRASO");
        assertThat(guardada.getDescripcion()).isEqualTo("Entrega demorada por trafico");
        assertThat(guardada.getFechaHora()).isNotNull();
        assertThat(guardada.getEstado()).isEqualTo(EstadoIncidencia.PENDIENTE);
        assertThat(asignacion.getEstado()).isEqualTo(EstadoRuta.EN_CURSO);
        assertThat(asignacion.getRuta().getEstado()).isEqualTo(EstadoRuta.EN_CURSO);
        verify(asignacionRutaDao).guardar(asignacion);
    }

    @Test
    void registrarDebeRechazarTipoVacio() {
        IncidenciaFormDto form = formValido();
        form.setTipo(" ");
        when(asignacionRutaDao.buscarPorId(1L)).thenReturn(Optional.of(asignacion(EstadoRuta.EN_CURSO)));

        assertThatThrownBy(() -> incidenciaService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tipo");
    }

    @Test
    void registrarDebeRechazarAsignacionCerrada() {
        when(asignacionRutaDao.buscarPorId(1L)).thenReturn(Optional.of(asignacion(EstadoRuta.CANCELADA)));

        assertThatThrownBy(() -> incidenciaService.registrar(formValido()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("incidencias");
    }

    @Test
    void listarTiposSugeridosDebeIncluirTiposOperativos() {
        assertThat(incidenciaService.listarTiposSugeridos())
                .contains("RETRASO", "DESVIO", "AVERIA", "ACCIDENTE", "OTRO");
    }

    private IncidenciaFormDto formValido() {
        IncidenciaFormDto form = new IncidenciaFormDto();
        form.setIdAsignacion(1L);
        form.setTipo(" retraso ");
        form.setDescripcion(" Entrega demorada por trafico ");
        return form;
    }

    private AsignacionRuta asignacion(EstadoRuta estado) {
        Ruta ruta = new Ruta();
        ruta.setIdRuta(10L);
        ruta.setOrigen("CD Norte");
        ruta.setDestino("Tienda Sur");
        ruta.setFechaProgramada(LocalDate.of(2026, 6, 12));
        ruta.setEstado(estado);

        Conductor conductor = new Conductor();
        conductor.setIdConductor(20L);
        conductor.setNombres("Ana");
        conductor.setApellidos("Lopez");
        conductor.setDni("70000001");
        conductor.setLicencia("AII-B");
        conductor.setEstado(EstadoGeneral.ACTIVO);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(30L);
        vehiculo.setPlaca("GPS-001");
        vehiculo.setMarca("Nissan");
        vehiculo.setModelo("Urban");
        vehiculo.setEstado(EstadoVehiculo.ASIGNADO);

        AsignacionRuta asignacionRuta = new AsignacionRuta();
        asignacionRuta.setIdAsignacion(1L);
        asignacionRuta.setRuta(ruta);
        asignacionRuta.setConductor(conductor);
        asignacionRuta.setVehiculo(vehiculo);
        asignacionRuta.setFechaAsignacion(LocalDate.of(2026, 6, 12));
        asignacionRuta.setEstado(estado);
        return asignacionRuta;
    }
}

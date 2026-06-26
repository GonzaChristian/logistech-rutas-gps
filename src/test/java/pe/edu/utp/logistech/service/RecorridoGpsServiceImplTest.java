package pe.edu.utp.logistech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.logistech.dao.AsignacionRutaDao;
import pe.edu.utp.logistech.dao.RecorridoGpsDao;
import pe.edu.utp.logistech.dto.RecorridoGpsFormDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.RecorridoGps;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.service.impl.RecorridoGpsServiceImpl;

@ExtendWith(MockitoExtension.class)
class RecorridoGpsServiceImplTest {

    @Mock
    private RecorridoGpsDao recorridoGpsDao;

    @Mock
    private AsignacionRutaDao asignacionRutaDao;

    @InjectMocks
    private RecorridoGpsServiceImpl recorridoGpsService;

    @Test
    void registrarDebeGuardarGpsEIniciarRecorridoProgramado() {
        AsignacionRuta asignacion = asignacion(EstadoRuta.PROGRAMADA);
        when(asignacionRutaDao.buscarPorId(1L)).thenReturn(Optional.of(asignacion));
        when(recorridoGpsDao.guardar(any(RecorridoGps.class))).thenAnswer(invocation -> invocation.getArgument(0));

        recorridoGpsService.registrar(formValido());

        ArgumentCaptor<RecorridoGps> captor = ArgumentCaptor.forClass(RecorridoGps.class);
        verify(recorridoGpsDao).guardar(captor.capture());
        RecorridoGps guardado = captor.getValue();
        assertThat(guardado.getAsignacion()).isSameAs(asignacion);
        assertThat(guardado.getLatitud()).isEqualByComparingTo(new BigDecimal("-12.04637400"));
        assertThat(guardado.getLongitud()).isEqualByComparingTo(new BigDecimal("-77.04279300"));
        assertThat(guardado.getFechaHora()).isNotNull();
        assertThat(asignacion.getEstado()).isEqualTo(EstadoRuta.EN_CURSO);
        assertThat(asignacion.getRuta().getEstado()).isEqualTo(EstadoRuta.EN_CURSO);
        verify(asignacionRutaDao).guardar(asignacion);
    }

    @Test
    void registrarDebeRechazarLatitudFueraDeRango() {
        when(asignacionRutaDao.buscarPorId(1L)).thenReturn(Optional.of(asignacion(EstadoRuta.EN_CURSO)));
        RecorridoGpsFormDto form = formValido();
        form.setLatitud(new BigDecimal("120"));

        assertThatThrownBy(() -> recorridoGpsService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("latitud");
    }

    @Test
    void registrarDebeRechazarLongitudFueraDeRango() {
        when(asignacionRutaDao.buscarPorId(1L)).thenReturn(Optional.of(asignacion(EstadoRuta.EN_CURSO)));
        RecorridoGpsFormDto form = formValido();
        form.setLongitud(new BigDecimal("-190"));

        assertThatThrownBy(() -> recorridoGpsService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("longitud");
    }

    @Test
    void registrarDebeRechazarAsignacionCerrada() {
        when(asignacionRutaDao.buscarPorId(1L)).thenReturn(Optional.of(asignacion(EstadoRuta.FINALIZADA)));

        assertThatThrownBy(() -> recorridoGpsService.registrar(formValido()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GPS");
    }

    @Test
    void listarAsignacionesEnControlDebeFiltrarProgramadasYEnCurso() {
        AsignacionRuta programada = asignacion(EstadoRuta.PROGRAMADA);
        AsignacionRuta enCurso = asignacion(EstadoRuta.EN_CURSO);
        AsignacionRuta finalizada = asignacion(EstadoRuta.FINALIZADA);
        when(asignacionRutaDao.listarTodos()).thenReturn(List.of(programada, enCurso, finalizada));

        List<AsignacionRuta> resultado = recorridoGpsService.listarAsignacionesEnControl();

        assertThat(resultado).containsExactly(programada, enCurso);
    }

    private RecorridoGpsFormDto formValido() {
        RecorridoGpsFormDto form = new RecorridoGpsFormDto();
        form.setIdAsignacion(1L);
        form.setLatitud(new BigDecimal("-12.046374"));
        form.setLongitud(new BigDecimal("-77.042793"));
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

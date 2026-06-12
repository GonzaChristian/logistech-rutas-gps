package pe.edu.utp.logistech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.logistech.dao.ConductorDao;
import pe.edu.utp.logistech.dto.ConductorFormDto;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.impl.ConductorServiceImpl;

@ExtendWith(MockitoExtension.class)
class ConductorServiceImplTest {

    @Mock
    private ConductorDao conductorDao;

    @InjectMocks
    private ConductorServiceImpl conductorService;

    @Test
    void registrarDebeValidarLimpiarYGuardarConductor() {
        ConductorFormDto form = formValido();
        when(conductorDao.existeDni("74215689")).thenReturn(false);
        when(conductorDao.guardar(any(Conductor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        conductorService.registrar(form);

        ArgumentCaptor<Conductor> captor = ArgumentCaptor.forClass(Conductor.class);
        verify(conductorDao).guardar(captor.capture());
        Conductor guardado = captor.getValue();
        assertThat(guardado.getNombres()).isEqualTo("Juan");
        assertThat(guardado.getApellidos()).isEqualTo("Perez");
        assertThat(guardado.getDni()).isEqualTo("74215689");
        assertThat(guardado.getLicencia()).isEqualTo("A-IIb");
        assertThat(guardado.getTelefono()).isEqualTo("987654321");
        assertThat(guardado.getEstado()).isEqualTo(EstadoGeneral.ACTIVO);
    }

    @Test
    void registrarDebeRechazarDniDuplicado() {
        ConductorFormDto form = formValido();
        when(conductorDao.existeDni("74215689")).thenReturn(true);

        assertThatThrownBy(() -> conductorService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DNI");
    }

    @Test
    void registrarDebeRechazarTextoObligatorioVacio() {
        ConductorFormDto form = formValido();
        form.setNombres(" ");

        assertThatThrownBy(() -> conductorService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombres");
    }

    @Test
    void actualizarDebeModificarConductorExistente() {
        Conductor existente = conductorExistente();
        ConductorFormDto form = formValido();
        form.setNombres("Luis");
        when(conductorDao.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(conductorDao.existeDniEnOtroConductor("74215689", 1L)).thenReturn(false);
        when(conductorDao.guardar(any(Conductor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Conductor actualizado = conductorService.actualizar(1L, form);

        assertThat(actualizado.getIdConductor()).isEqualTo(1L);
        assertThat(actualizado.getNombres()).isEqualTo("Luis");
        verify(conductorDao).guardar(existente);
    }

    @Test
    void actualizarDebeLanzarErrorSiNoExiste() {
        when(conductorDao.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> conductorService.actualizar(99L, formValido()))
                .isInstanceOf(LogistechException.class)
                .hasMessageContaining("conductor");
    }

    @Test
    void cambiarEstadoDebeActualizarEstado() {
        Conductor existente = conductorExistente();
        when(conductorDao.buscarPorId(1L)).thenReturn(Optional.of(existente));

        conductorService.cambiarEstado(1L, EstadoGeneral.INACTIVO);

        assertThat(existente.getEstado()).isEqualTo(EstadoGeneral.INACTIVO);
        verify(conductorDao).guardar(existente);
    }

    private ConductorFormDto formValido() {
        ConductorFormDto form = new ConductorFormDto();
        form.setNombres(" Juan ");
        form.setApellidos(" Perez ");
        form.setDni("74215689");
        form.setLicencia(" A-IIb ");
        form.setTelefono(" 987654321 ");
        form.setEstado(EstadoGeneral.ACTIVO);
        return form;
    }

    private Conductor conductorExistente() {
        Conductor conductor = new Conductor();
        conductor.setIdConductor(1L);
        conductor.setNombres("Juan");
        conductor.setApellidos("Perez");
        conductor.setDni("74215689");
        conductor.setLicencia("A-IIb");
        conductor.setTelefono("987654321");
        conductor.setEstado(EstadoGeneral.ACTIVO);
        return conductor;
    }
}

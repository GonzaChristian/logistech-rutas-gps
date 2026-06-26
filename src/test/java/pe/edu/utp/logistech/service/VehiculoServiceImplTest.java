package pe.edu.utp.logistech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.logistech.dao.VehiculoDao;
import pe.edu.utp.logistech.dto.VehiculoFormDto;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.impl.VehiculoServiceImpl;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceImplTest {

    @Mock
    private VehiculoDao vehiculoDao;

    @InjectMocks
    private VehiculoServiceImpl vehiculoService;

    @Test
    void registrarDebeValidarNormalizarYGuardarVehiculo() {
        VehiculoFormDto form = formValido();
        when(vehiculoDao.existePlaca("ABC-123")).thenReturn(false);
        when(vehiculoDao.guardar(any(Vehiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        vehiculoService.registrar(form);

        ArgumentCaptor<Vehiculo> captor = ArgumentCaptor.forClass(Vehiculo.class);
        verify(vehiculoDao).guardar(captor.capture());
        Vehiculo guardado = captor.getValue();
        assertThat(guardado.getPlaca()).isEqualTo("ABC-123");
        assertThat(guardado.getMarca()).isEqualTo("Hyundai");
        assertThat(guardado.getModelo()).isEqualTo("H100");
        assertThat(guardado.getEstado()).isEqualTo(EstadoVehiculo.DISPONIBLE);
    }

    @Test
    void registrarDebeRechazarPlacaDuplicada() {
        VehiculoFormDto form = formValido();
        when(vehiculoDao.existePlaca("ABC-123")).thenReturn(true);

        assertThatThrownBy(() -> vehiculoService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("placa");
    }

    @Test
    void listarVehiculosDebeDelegarAlDao() {
        Vehiculo vehiculo = vehiculoExistente();
        when(vehiculoDao.listarTodos()).thenReturn(List.of(vehiculo));

        assertThat(vehiculoService.listarVehiculos()).containsExactly(vehiculo);
    }

    @Test
    void registrarDebeRechazarMarcaVacia() {
        VehiculoFormDto form = formValido();
        form.setMarca(" ");

        assertThatThrownBy(() -> vehiculoService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("marca");
    }

    @Test
    void registrarDebeRechazarEstadoNoGestionable() {
        VehiculoFormDto form = formValido();
        form.setEstado(EstadoVehiculo.ASIGNADO);

        assertThatThrownBy(() -> vehiculoService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("estado");
    }

    @Test
    void actualizarDebeModificarVehiculoExistente() {
        Vehiculo existente = vehiculoExistente();
        VehiculoFormDto form = formValido();
        form.setModelo("N300");
        when(vehiculoDao.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(vehiculoDao.existePlacaEnOtroVehiculo("ABC-123", 1L)).thenReturn(false);
        when(vehiculoDao.guardar(any(Vehiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehiculo actualizado = vehiculoService.actualizar(1L, form);

        assertThat(actualizado.getIdVehiculo()).isEqualTo(1L);
        assertThat(actualizado.getModelo()).isEqualTo("N300");
        verify(vehiculoDao).guardar(existente);
    }

    @Test
    void actualizarDebeLanzarErrorSiNoExiste() {
        when(vehiculoDao.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehiculoService.actualizar(99L, formValido()))
                .isInstanceOf(LogistechException.class)
                .hasMessageContaining("vehiculo");
    }

    @Test
    void cambiarEstadoDebeActualizarEstado() {
        Vehiculo existente = vehiculoExistente();
        when(vehiculoDao.buscarPorId(1L)).thenReturn(Optional.of(existente));

        vehiculoService.cambiarEstado(1L, EstadoVehiculo.MANTENIMIENTO);

        assertThat(existente.getEstado()).isEqualTo(EstadoVehiculo.MANTENIMIENTO);
        verify(vehiculoDao).guardar(existente);
    }

    private VehiculoFormDto formValido() {
        VehiculoFormDto form = new VehiculoFormDto();
        form.setPlaca(" abc-123 ");
        form.setMarca(" Hyundai ");
        form.setModelo(" H100 ");
        form.setEstado(EstadoVehiculo.DISPONIBLE);
        return form;
    }

    private Vehiculo vehiculoExistente() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(1L);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setMarca("Hyundai");
        vehiculo.setModelo("H100");
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        return vehiculo;
    }
}

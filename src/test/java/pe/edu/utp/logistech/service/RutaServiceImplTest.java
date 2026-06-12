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
import pe.edu.utp.logistech.dao.RutaDao;
import pe.edu.utp.logistech.dto.RutaFormDto;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.exception.LogistechException;
import pe.edu.utp.logistech.service.impl.RutaServiceImpl;

@ExtendWith(MockitoExtension.class)
class RutaServiceImplTest {

    @Mock
    private RutaDao rutaDao;

    @InjectMocks
    private RutaServiceImpl rutaService;

    @Test
    void registrarDebeValidarLimpiarYGuardarRuta() {
        RutaFormDto form = formValido();
        when(rutaDao.guardar(any(Ruta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        rutaService.registrar(form);

        ArgumentCaptor<Ruta> captor = ArgumentCaptor.forClass(Ruta.class);
        verify(rutaDao).guardar(captor.capture());
        Ruta guardada = captor.getValue();
        assertThat(guardada.getOrigen()).isEqualTo("Tienda Norte");
        assertThat(guardada.getDestino()).isEqualTo("Tienda Sur");
        assertThat(guardada.getFechaProgramada()).isEqualTo(LocalDate.of(2026, 6, 12));
        assertThat(guardada.getEstado()).isEqualTo(EstadoRuta.PROGRAMADA);
    }

    @Test
    void registrarDebeRechazarOrigenYDestinoIguales() {
        RutaFormDto form = formValido();
        form.setDestino(" tienda norte ");

        assertThatThrownBy(() -> rutaService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("origen y destino");
    }

    @Test
    void registrarDebeRechazarFechaVacia() {
        RutaFormDto form = formValido();
        form.setFechaProgramada(null);

        assertThatThrownBy(() -> rutaService.registrar(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fecha");
    }

    @Test
    void actualizarDebeModificarRutaExistente() {
        Ruta existente = rutaExistente();
        RutaFormDto form = formValido();
        form.setDestino("Tienda Centro");
        when(rutaDao.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(rutaDao.guardar(any(Ruta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ruta actualizada = rutaService.actualizar(1L, form);

        assertThat(actualizada.getIdRuta()).isEqualTo(1L);
        assertThat(actualizada.getDestino()).isEqualTo("Tienda Centro");
        verify(rutaDao).guardar(existente);
    }

    @Test
    void actualizarDebeLanzarErrorSiNoExiste() {
        when(rutaDao.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rutaService.actualizar(99L, formValido()))
                .isInstanceOf(LogistechException.class)
                .hasMessageContaining("ruta");
    }

    private RutaFormDto formValido() {
        RutaFormDto form = new RutaFormDto();
        form.setOrigen(" Tienda Norte ");
        form.setDestino(" Tienda Sur ");
        form.setFechaProgramada(LocalDate.of(2026, 6, 12));
        form.setEstado(EstadoRuta.PROGRAMADA);
        return form;
    }

    private Ruta rutaExistente() {
        Ruta ruta = new Ruta();
        ruta.setIdRuta(1L);
        ruta.setOrigen("Tienda Norte");
        ruta.setDestino("Tienda Sur");
        ruta.setFechaProgramada(LocalDate.of(2026, 6, 12));
        ruta.setEstado(EstadoRuta.PROGRAMADA);
        return ruta;
    }
}

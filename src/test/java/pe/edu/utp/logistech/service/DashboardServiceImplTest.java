package pe.edu.utp.logistech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.logistech.dao.IncidenciaDao;
import pe.edu.utp.logistech.dao.RecorridoGpsDao;
import pe.edu.utp.logistech.dao.RutaDao;
import pe.edu.utp.logistech.dto.DashboardSummaryDto;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.service.impl.DashboardServiceImpl;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private RutaDao rutaDao;

    @Mock
    private IncidenciaDao incidenciaDao;

    @Mock
    private RecorridoGpsDao recorridoGpsDao;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Test
    void obtenerResumenDebeAgruparIndicadoresPrincipales() {
        when(rutaDao.contar()).thenReturn(15L);
        when(rutaDao.contarPorEstado(EstadoRuta.EN_CURSO)).thenReturn(4L);
        when(rutaDao.contarPorEstado(EstadoRuta.FINALIZADA)).thenReturn(9L);
        when(incidenciaDao.contar()).thenReturn(2L);
        when(recorridoGpsDao.contar()).thenReturn(24L);

        DashboardSummaryDto resumen = dashboardService.obtenerResumen();

        assertThat(resumen.rutasRegistradas()).isEqualTo(15L);
        assertThat(resumen.rutasEnCurso()).isEqualTo(4L);
        assertThat(resumen.incidenciasRegistradas()).isEqualTo(2L);
        assertThat(resumen.rutasFinalizadas()).isEqualTo(9L);
        assertThat(resumen.gpsRegistrados()).isEqualTo(24L);

        verify(rutaDao).contar();
        verify(rutaDao).contarPorEstado(EstadoRuta.EN_CURSO);
        verify(rutaDao).contarPorEstado(EstadoRuta.FINALIZADA);
        verify(incidenciaDao).contar();
        verify(recorridoGpsDao).contar();
    }
}

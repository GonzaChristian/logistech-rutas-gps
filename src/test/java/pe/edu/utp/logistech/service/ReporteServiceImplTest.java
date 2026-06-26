package pe.edu.utp.logistech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.logistech.dao.ConductorDao;
import pe.edu.utp.logistech.dao.ReporteDao;
import pe.edu.utp.logistech.dto.ReporteFiltroDto;
import pe.edu.utp.logistech.dto.ReporteRutaDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.service.impl.ReporteServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private ReporteDao reporteDao;

    @Mock
    private ConductorDao conductorDao;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    @Test
    void consultarRutasDebeFiltrarPorEstadoConductorYFecha() {
        AsignacionRuta asignacionIncluida = asignacion(1L, 10L, EstadoRuta.FINALIZADA,
                LocalDate.of(2026, 6, 12), 100L);
        AsignacionRuta otroEstado = asignacion(2L, 11L, EstadoRuta.EN_CURSO,
                LocalDate.of(2026, 6, 12), 100L);
        AsignacionRuta otroConductor = asignacion(3L, 12L, EstadoRuta.FINALIZADA,
                LocalDate.of(2026, 6, 12), 200L);
        AsignacionRuta otraFecha = asignacion(4L, 13L, EstadoRuta.FINALIZADA,
                LocalDate.of(2026, 7, 1), 100L);
        when(reporteDao.listarAsignacionesReporte())
                .thenReturn(List.of(asignacionIncluida, otroEstado, otroConductor, otraFecha));

        ReporteFiltroDto filtro = new ReporteFiltroDto();
        filtro.setEstado(EstadoRuta.FINALIZADA);
        filtro.setIdConductor(100L);
        filtro.setFechaInicio(LocalDate.of(2026, 6, 1));
        filtro.setFechaFin(LocalDate.of(2026, 6, 30));

        List<ReporteRutaDto> resultado = reporteService.consultarRutas(filtro);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdAsignacion()).isEqualTo(1L);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoRuta.FINALIZADA);
        assertThat(resultado.get(0).getDniConductor()).isEqualTo("70000100");
    }

    @Test
    void consultarRutasDebeRechazarFechaInicioMayorQueFechaFin() {
        ReporteFiltroDto filtro = new ReporteFiltroDto();
        filtro.setFechaInicio(LocalDate.of(2026, 7, 1));
        filtro.setFechaFin(LocalDate.of(2026, 6, 1));

        assertThatThrownBy(() -> reporteService.consultarRutas(filtro))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fecha inicial");
    }

    @Test
    void listarConductoresDebeDelegarAlDao() {
        Conductor conductor = conductor(100L);
        when(conductorDao.listarTodos()).thenReturn(List.of(conductor));

        assertThat(reporteService.listarConductores()).containsExactly(conductor);
    }

    @Test
    void consultarRutasDebeRetornarListaVaciaCuandoNoHayCoincidencias() {
        when(reporteDao.listarAsignacionesReporte()).thenReturn(List.of(asignacion(1L, 10L,
                EstadoRuta.EN_CURSO, LocalDate.of(2026, 6, 12), 100L)));

        ReporteFiltroDto filtro = new ReporteFiltroDto();
        filtro.setEstado(EstadoRuta.FINALIZADA);
        filtro.setFechaInicio(LocalDate.of(2026, 6, 1));
        filtro.setFechaFin(LocalDate.of(2026, 6, 30));

        assertThat(reporteService.consultarRutas(filtro)).isEmpty();
    }

    @Test
    void exportarRutasExcelDebeGenerarArchivoConCabecerasYDatos() throws Exception {
        when(reporteDao.listarAsignacionesReporte()).thenReturn(List.of(asignacion(1L, 10L,
                EstadoRuta.EN_CURSO, LocalDate.of(2026, 6, 12), 100L)));

        byte[] excel = reporteService.exportarRutasExcel(new ReporteFiltroDto());

        assertThat(excel).isNotEmpty();
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excel))) {
            assertThat(workbook.getSheetAt(0).getSheetName()).isEqualTo("Reporte de rutas");
            assertThat(workbook.getSheetAt(0).getRow(3).getCell(0).getStringCellValue())
                    .isEqualTo("ID asignacion");
            assertThat(workbook.getSheetAt(0).getRow(4).getCell(2).getStringCellValue())
                    .isEqualTo("Origen 10");
            assertThat(workbook.getSheetAt(0).getRow(4).getCell(6).getStringCellValue())
                    .isEqualTo("EN_CURSO");
        }
    }

    private AsignacionRuta asignacion(Long idAsignacion, Long idRuta, EstadoRuta estado,
                                      LocalDate fechaProgramada, Long idConductor) {
        Ruta ruta = new Ruta();
        ruta.setIdRuta(idRuta);
        ruta.setOrigen("Origen " + idRuta);
        ruta.setDestino("Destino " + idRuta);
        ruta.setFechaProgramada(fechaProgramada);
        ruta.setEstado(estado);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(idRuta + 300L);
        vehiculo.setPlaca("REP-" + idRuta);
        vehiculo.setMarca("Marca");
        vehiculo.setModelo("Modelo");
        vehiculo.setEstado(EstadoVehiculo.ASIGNADO);

        AsignacionRuta asignacion = new AsignacionRuta();
        asignacion.setIdAsignacion(idAsignacion);
        asignacion.setRuta(ruta);
        asignacion.setConductor(conductor(idConductor));
        asignacion.setVehiculo(vehiculo);
        asignacion.setFechaAsignacion(fechaProgramada.minusDays(1));
        asignacion.setEstado(estado);
        return asignacion;
    }

    private Conductor conductor(Long idConductor) {
        Conductor conductor = new Conductor();
        conductor.setIdConductor(idConductor);
        conductor.setNombres("Conductor");
        conductor.setApellidos(String.valueOf(idConductor));
        conductor.setDni("70000" + idConductor);
        conductor.setLicencia("AII-B");
        conductor.setEstado(EstadoGeneral.ACTIVO);
        return conductor;
    }
}

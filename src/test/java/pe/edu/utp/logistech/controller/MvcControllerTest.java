package pe.edu.utp.logistech.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.utp.logistech.dto.AsignacionRutaFormDto;
import pe.edu.utp.logistech.dto.ConductorFormDto;
import pe.edu.utp.logistech.dto.DashboardSummaryDto;
import pe.edu.utp.logistech.dto.IncidenciaFormDto;
import pe.edu.utp.logistech.dto.RecorridoGpsFormDto;
import pe.edu.utp.logistech.dto.ReporteFiltroDto;
import pe.edu.utp.logistech.dto.ReporteRutaDto;
import pe.edu.utp.logistech.dto.RutaFormDto;
import pe.edu.utp.logistech.dto.VehiculoFormDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.Incidencia;
import pe.edu.utp.logistech.entity.RecorridoGps;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoIncidencia;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.security.LogistechUserDetailsService;
import pe.edu.utp.logistech.security.SecurityConfig;
import pe.edu.utp.logistech.service.AsignacionRutaService;
import pe.edu.utp.logistech.service.ConductorService;
import pe.edu.utp.logistech.service.DashboardService;
import pe.edu.utp.logistech.service.IncidenciaService;
import pe.edu.utp.logistech.service.RecorridoGpsService;
import pe.edu.utp.logistech.service.ReporteService;
import pe.edu.utp.logistech.service.RutaService;
import pe.edu.utp.logistech.service.VehiculoService;

@WebMvcTest(controllers = {
        AuthController.class,
        HomeController.class,
        DashboardController.class,
        VehiculoController.class,
        ConductorController.class,
        RutaController.class,
        AsignacionRutaController.class,
        RecorridoGpsController.class,
        IncidenciaController.class,
        ReporteController.class
})
@Import(SecurityConfig.class)
class MvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogistechUserDetailsService userDetailsService;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private VehiculoService vehiculoService;

    @MockBean
    private ConductorService conductorService;

    @MockBean
    private RutaService rutaService;

    @MockBean
    private AsignacionRutaService asignacionRutaService;

    @MockBean
    private RecorridoGpsService recorridoGpsService;

    @MockBean
    private IncidenciaService incidenciaService;

    @MockBean
    private ReporteService reporteService;

    @Test
    void loginDebeMostrarVistaLoginSinAutenticacion() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboardDebeMostrarResumenEnModelo() throws Exception {
        when(dashboardService.obtenerResumen()).thenReturn(new DashboardSummaryDto(5, 2, 1, 3, 8));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(model().attributeExists("resumen"))
                .andExpect(model().attribute("activeMenu", "dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void homeDebeRedirigirAlDashboard() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void vehiculosIndexDebeMostrarVistaConModelo() throws Exception {
        when(vehiculoService.listarVehiculos()).thenReturn(List.of(vehiculo()));
        when(vehiculoService.listarEstadosGestion()).thenReturn(List.of(
                EstadoVehiculo.DISPONIBLE,
                EstadoVehiculo.MANTENIMIENTO,
                EstadoVehiculo.INACTIVO));

        mockMvc.perform(get("/vehiculos"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehiculos/index"))
                .andExpect(model().attributeExists("vehiculoForm", "vehiculos", "estados"))
                .andExpect(model().attribute("activeMenu", "vehiculos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void vehiculosPostDebeRegistrarYRedirigir() throws Exception {
        when(vehiculoService.registrar(any(VehiculoFormDto.class))).thenReturn(vehiculo());

        mockMvc.perform(post("/vehiculos")
                        .with(csrf())
                        .param("placa", "ABC-123")
                        .param("marca", "Hyundai")
                        .param("modelo", "H100")
                        .param("estado", "DISPONIBLE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehiculos"));

        verify(vehiculoService).registrar(any(VehiculoFormDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void conductoresIndexDebeMostrarVistaConModelo() throws Exception {
        when(conductorService.listarConductores()).thenReturn(List.of(conductor()));

        mockMvc.perform(get("/conductores"))
                .andExpect(status().isOk())
                .andExpect(view().name("conductores/index"))
                .andExpect(model().attributeExists("conductorForm", "conductores", "estados"))
                .andExpect(model().attribute("activeMenu", "conductores"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void conductoresPostDebeRegistrarYRedirigir() throws Exception {
        when(conductorService.registrar(any(ConductorFormDto.class))).thenReturn(conductor());

        mockMvc.perform(post("/conductores")
                        .with(csrf())
                        .param("nombres", "Ana")
                        .param("apellidos", "Lopez")
                        .param("dni", "70000001")
                        .param("licencia", "AII-B")
                        .param("telefono", "999999999")
                        .param("estado", "ACTIVO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/conductores"));

        verify(conductorService).registrar(any(ConductorFormDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rutasIndexDebeMostrarVistaConModelo() throws Exception {
        when(rutaService.listarRutas()).thenReturn(List.of(ruta()));

        mockMvc.perform(get("/rutas"))
                .andExpect(status().isOk())
                .andExpect(view().name("rutas/index"))
                .andExpect(model().attributeExists("rutaForm", "rutas"))
                .andExpect(model().attribute("activeMenu", "rutas"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rutasPostDebeRegistrarYRedirigir() throws Exception {
        when(rutaService.registrar(any(RutaFormDto.class))).thenReturn(ruta());

        mockMvc.perform(post("/rutas")
                        .with(csrf())
                        .param("origen", "CD Norte")
                        .param("destino", "Tienda Sur")
                        .param("fechaProgramada", "2026-06-12")
                        .param("estado", "PROGRAMADA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rutas"));

        verify(rutaService).registrar(any(RutaFormDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void asignacionesIndexDebeMostrarVistaConModelo() throws Exception {
        when(asignacionRutaService.listarAsignaciones()).thenReturn(List.of(asignacion()));
        when(asignacionRutaService.listarRutasDisponibles()).thenReturn(List.of(ruta()));
        when(asignacionRutaService.listarConductoresDisponibles()).thenReturn(List.of(conductor()));
        when(asignacionRutaService.listarVehiculosDisponibles()).thenReturn(List.of(vehiculo()));
        when(asignacionRutaService.listarEstadosGestion()).thenReturn(List.of(
                EstadoRuta.PROGRAMADA,
                EstadoRuta.EN_CURSO,
                EstadoRuta.FINALIZADA,
                EstadoRuta.CANCELADA));

        mockMvc.perform(get("/asignaciones"))
                .andExpect(status().isOk())
                .andExpect(view().name("asignaciones/index"))
                .andExpect(model().attributeExists(
                        "asignacionForm",
                        "asignaciones",
                        "rutasDisponibles",
                        "conductoresDisponibles",
                        "vehiculosDisponibles"))
                .andExpect(model().attribute("activeMenu", "asignaciones"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void asignacionesPostDebeRegistrarYRedirigir() throws Exception {
        when(asignacionRutaService.registrar(any(AsignacionRutaFormDto.class))).thenReturn(asignacion());

        mockMvc.perform(post("/asignaciones")
                        .with(csrf())
                        .param("idRuta", "1")
                        .param("idConductor", "2")
                        .param("idVehiculo", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/asignaciones"));

        verify(asignacionRutaService).registrar(any(AsignacionRutaFormDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void recorridosIndexDebeMostrarGpsEIncidencias() throws Exception {
        when(recorridoGpsService.listarAsignacionesEnControl()).thenReturn(List.of(asignacion()));
        when(recorridoGpsService.listarRegistrosGps()).thenReturn(List.of(gps()));
        when(incidenciaService.listarIncidencias()).thenReturn(List.of(incidencia()));
        when(incidenciaService.listarTiposSugeridos()).thenReturn(List.of("RETRASO", "AVERIA"));

        mockMvc.perform(get("/recorridos"))
                .andExpect(status().isOk())
                .andExpect(view().name("recorridos/index"))
                .andExpect(model().attributeExists(
                        "gpsForm",
                        "incidenciaForm",
                        "asignacionesControl",
                        "registrosGps",
                        "incidencias",
                        "tiposIncidencia"))
                .andExpect(model().attribute("activeMenu", "recorridos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void recorridosPostGpsDebeRegistrarYRedirigir() throws Exception {
        when(recorridoGpsService.registrar(any(RecorridoGpsFormDto.class))).thenReturn(gps());

        mockMvc.perform(post("/recorridos/gps")
                        .with(csrf())
                        .param("idAsignacion", "10")
                        .param("latitud", "-12.046374")
                        .param("longitud", "-77.042793"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recorridos"));

        verify(recorridoGpsService).registrar(any(RecorridoGpsFormDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERVISOR")
    void supervisorPuedeConsultarConductoresPeroNoRegistrarlos() throws Exception {
        mockMvc.perform(get("/conductores"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/conductores")
                        .with(csrf())
                        .param("nombres", "Ana")
                        .param("apellidos", "Lopez")
                        .param("dni", "70000001")
                        .param("licencia", "AII-B")
                        .param("estado", "ACTIVO"))
                .andExpect(status().isForbidden());

        verify(conductorService, never()).registrar(any(ConductorFormDto.class));
    }

    @Test
    @WithMockUser(roles = "SUPERVISOR")
    void supervisorPuedeRegistrarControlGps() throws Exception {
        when(recorridoGpsService.registrar(any(RecorridoGpsFormDto.class))).thenReturn(gps());

        mockMvc.perform(post("/recorridos/gps")
                        .with(csrf())
                        .param("idAsignacion", "10")
                        .param("latitud", "-12.046374")
                        .param("longitud", "-77.042793"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recorridos"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void incidenciasIndexDebeMostrarVistaConModelo() throws Exception {
        when(recorridoGpsService.listarAsignacionesEnControl()).thenReturn(List.of(asignacion()));
        when(incidenciaService.listarIncidencias()).thenReturn(List.of(incidencia()));
        when(incidenciaService.listarTiposSugeridos()).thenReturn(List.of("RETRASO", "AVERIA"));

        mockMvc.perform(get("/incidencias"))
                .andExpect(status().isOk())
                .andExpect(view().name("incidencias/index"))
                .andExpect(model().attributeExists(
                        "incidenciaForm",
                        "asignacionesControl",
                        "incidencias",
                        "tiposIncidencia"))
                .andExpect(model().attribute("activeMenu", "incidencias"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void incidenciasPostDebeRegistrarYRedirigir() throws Exception {
        when(incidenciaService.registrar(any(IncidenciaFormDto.class))).thenReturn(incidencia());

        mockMvc.perform(post("/incidencias")
                        .with(csrf())
                        .param("idAsignacion", "10")
                        .param("tipo", "RETRASO")
                        .param("descripcion", "Demora por trafico"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/incidencias"));

        verify(incidenciaService).registrar(any(IncidenciaFormDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reportesIndexDebeMostrarFiltrosYResultados() throws Exception {
        when(reporteService.consultarRutas(any(ReporteFiltroDto.class)))
                .thenReturn(List.of(ReporteRutaDto.desdeAsignacion(asignacion())));
        when(reporteService.listarConductores()).thenReturn(List.of(conductor()));

        mockMvc.perform(get("/reportes")
                        .param("estado", "PROGRAMADA")
                        .param("idConductor", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("reportes/index"))
                .andExpect(model().attributeExists("filtro", "filas", "conductores", "estados"))
                .andExpect(model().attribute("activeMenu", "reportes"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reportesIndexConRangoFechasInvalidoDebeMostrarErrorSin500() throws Exception {
        when(reporteService.consultarRutas(any(ReporteFiltroDto.class)))
                .thenThrow(new IllegalArgumentException("La fecha inicial no puede ser mayor que la fecha final"));
        when(reporteService.listarConductores()).thenReturn(List.of(conductor()));

        mockMvc.perform(get("/reportes")
                        .param("fechaInicio", "2026-07-01")
                        .param("fechaFin", "2026-06-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("reportes/index"))
                .andExpect(model().attributeExists("error", "filas", "conductores", "estados"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void reportesExportarDebeRetornarArchivoExcel() throws Exception {
        when(reporteService.exportarRutasExcel(any(ReporteFiltroDto.class))).thenReturn(new byte[] {1, 2, 3});

        mockMvc.perform(get("/reportes/exportar"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().bytes(new byte[] {1, 2, 3}));
    }

    private AsignacionRuta asignacion() {
        AsignacionRuta asignacion = new AsignacionRuta();
        asignacion.setIdAsignacion(10L);
        asignacion.setRuta(ruta());
        asignacion.setConductor(conductor());
        asignacion.setVehiculo(vehiculo());
        asignacion.setFechaAsignacion(LocalDate.of(2026, 6, 12));
        asignacion.setEstado(EstadoRuta.PROGRAMADA);
        return asignacion;
    }

    private Ruta ruta() {
        Ruta ruta = new Ruta();
        ruta.setIdRuta(1L);
        ruta.setOrigen("CD Norte");
        ruta.setDestino("Tienda Sur");
        ruta.setFechaProgramada(LocalDate.of(2026, 6, 12));
        ruta.setEstado(EstadoRuta.PROGRAMADA);
        return ruta;
    }

    private RecorridoGps gps() {
        RecorridoGps gps = new RecorridoGps();
        gps.setIdGps(20L);
        gps.setAsignacion(asignacion());
        gps.setLatitud(new BigDecimal("-12.04637400"));
        gps.setLongitud(new BigDecimal("-77.04279300"));
        gps.setFechaHora(LocalDateTime.of(2026, 6, 12, 8, 30));
        return gps;
    }

    private Incidencia incidencia() {
        Incidencia incidencia = new Incidencia();
        incidencia.setIdIncidencia(30L);
        incidencia.setAsignacion(asignacion());
        incidencia.setTipo("RETRASO");
        incidencia.setDescripcion("Demora por trafico");
        incidencia.setFechaHora(LocalDateTime.of(2026, 6, 12, 9, 0));
        incidencia.setEstado(EstadoIncidencia.PENDIENTE);
        return incidencia;
    }

    private Conductor conductor() {
        Conductor conductor = new Conductor();
        conductor.setIdConductor(2L);
        conductor.setNombres("Ana");
        conductor.setApellidos("Lopez");
        conductor.setDni("70000001");
        conductor.setLicencia("AII-B");
        conductor.setTelefono("999999999");
        conductor.setEstado(EstadoGeneral.ACTIVO);
        return conductor;
    }

    private Vehiculo vehiculo() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(3L);
        vehiculo.setPlaca("ABC-123");
        vehiculo.setMarca("Hyundai");
        vehiculo.setModelo("H100");
        vehiculo.setEstado(EstadoVehiculo.DISPONIBLE);
        return vehiculo;
    }
}

package pe.edu.utp.logistech.service.impl;

import com.google.common.base.Preconditions;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.logistech.dao.ConductorDao;
import pe.edu.utp.logistech.dao.ReporteDao;
import pe.edu.utp.logistech.dto.ReporteFiltroDto;
import pe.edu.utp.logistech.dto.ReporteRutaDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.util.ExcelReportUtil;
import pe.edu.utp.logistech.service.ReporteService;

@Service
@Transactional
public class ReporteServiceImpl implements ReporteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteServiceImpl.class);

    private final ReporteDao reporteDao;
    private final ConductorDao conductorDao;

    public ReporteServiceImpl(ReporteDao reporteDao, ConductorDao conductorDao) {
        this.reporteDao = reporteDao;
        this.conductorDao = conductorDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteRutaDto> consultarRutas(ReporteFiltroDto filtro) {
        ReporteFiltroDto filtroSeguro = filtro == null ? new ReporteFiltroDto() : filtro;
        validarRangoFechas(filtroSeguro);
        return reporteDao.listarAsignacionesReporte().stream()
                .filter(asignacion -> coincideEstado(asignacion, filtroSeguro))
                .filter(asignacion -> coincideConductor(asignacion, filtroSeguro))
                .filter(asignacion -> coincideFecha(asignacion, filtroSeguro))
                .sorted(Comparator
                        .comparing((AsignacionRuta asignacion) -> asignacion.getRuta().getFechaProgramada()).reversed()
                        .thenComparing(AsignacionRuta::getIdAsignacion, Comparator.reverseOrder()))
                .map(ReporteRutaDto::desdeAsignacion)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarRutasExcel(ReporteFiltroDto filtro) {
        List<ReporteRutaDto> filas = consultarRutas(filtro);
        LOGGER.info("Exportando reporte de rutas con {} registros", filas.size());
        return ExcelReportUtil.exportarReporteRutas(filas, filtro == null ? new ReporteFiltroDto() : filtro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conductor> listarConductores() {
        return conductorDao.listarTodos();
    }

    private void validarRangoFechas(ReporteFiltroDto filtro) {
        LocalDate inicio = filtro.getFechaInicio();
        LocalDate fin = filtro.getFechaFin();
        Preconditions.checkArgument(inicio == null || fin == null || !inicio.isAfter(fin),
                "La fecha inicial no puede ser mayor que la fecha final");
    }

    private boolean coincideEstado(AsignacionRuta asignacion, ReporteFiltroDto filtro) {
        return filtro.getEstado() == null || asignacion.getEstado() == filtro.getEstado();
    }

    private boolean coincideConductor(AsignacionRuta asignacion, ReporteFiltroDto filtro) {
        return filtro.getIdConductor() == null
                || filtro.getIdConductor() <= 0
                || asignacion.getConductor().getIdConductor().equals(filtro.getIdConductor());
    }

    private boolean coincideFecha(AsignacionRuta asignacion, ReporteFiltroDto filtro) {
        LocalDate fechaProgramada = asignacion.getRuta().getFechaProgramada();
        LocalDate inicio = filtro.getFechaInicio();
        LocalDate fin = filtro.getFechaFin();
        return (inicio == null || !fechaProgramada.isBefore(inicio))
                && (fin == null || !fechaProgramada.isAfter(fin));
    }
}

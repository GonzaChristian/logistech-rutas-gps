package pe.edu.utp.logistech.dao.impl;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.ReporteDao;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.repository.AsignacionRutaRepository;
import pe.edu.utp.logistech.repository.ReporteRepository;

@Repository
public class ReporteDaoImpl implements ReporteDao {

    private final ReporteRepository reporteRepository;
    private final AsignacionRutaRepository asignacionRutaRepository;

    public ReporteDaoImpl(ReporteRepository reporteRepository, AsignacionRutaRepository asignacionRutaRepository) {
        this.reporteRepository = reporteRepository;
        this.asignacionRutaRepository = asignacionRutaRepository;
    }

    @Override
    public List<AsignacionRuta> listarAsignacionesReporte() {
        return asignacionRutaRepository.findAllByOrderByFechaAsignacionDescIdAsignacionDesc();
    }

    @Override
    public long contar() {
        return reporteRepository.count();
    }
}

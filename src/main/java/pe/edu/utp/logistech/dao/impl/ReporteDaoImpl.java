package pe.edu.utp.logistech.dao.impl;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.ReporteDao;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.repository.AsignacionRutaRepository;

@Repository
public class ReporteDaoImpl implements ReporteDao {

    private final AsignacionRutaRepository asignacionRutaRepository;

    public ReporteDaoImpl(AsignacionRutaRepository asignacionRutaRepository) {
        this.asignacionRutaRepository = asignacionRutaRepository;
    }

    @Override
    public List<AsignacionRuta> listarAsignacionesReporte() {
        return asignacionRutaRepository.findAllByOrderByFechaAsignacionDescIdAsignacionDesc();
    }

}

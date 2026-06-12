package pe.edu.utp.logistech.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.utp.logistech.dao.IncidenciaDao;
import pe.edu.utp.logistech.dao.RecorridoGpsDao;
import pe.edu.utp.logistech.dao.RutaDao;
import pe.edu.utp.logistech.dto.DashboardSummaryDto;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final RutaDao rutaDao;
    private final IncidenciaDao incidenciaDao;
    private final RecorridoGpsDao recorridoGpsDao;

    public DashboardServiceImpl(RutaDao rutaDao, IncidenciaDao incidenciaDao, RecorridoGpsDao recorridoGpsDao) {
        this.rutaDao = rutaDao;
        this.incidenciaDao = incidenciaDao;
        this.recorridoGpsDao = recorridoGpsDao;
    }

    @Override
    public DashboardSummaryDto obtenerResumen() {
        LOGGER.info("Generando resumen del dashboard");
        return new DashboardSummaryDto(
                rutaDao.contar(),
                rutaDao.contarPorEstado(EstadoRuta.EN_CURSO),
                incidenciaDao.contar(),
                rutaDao.contarPorEstado(EstadoRuta.FINALIZADA),
                recorridoGpsDao.contar()
        );
    }
}

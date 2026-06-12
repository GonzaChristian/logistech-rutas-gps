package pe.edu.utp.logistech.dao.impl;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.RecorridoGpsDao;
import pe.edu.utp.logistech.entity.RecorridoGps;
import pe.edu.utp.logistech.repository.RecorridoGpsRepository;

@Repository
public class RecorridoGpsDaoImpl implements RecorridoGpsDao {

    private final RecorridoGpsRepository recorridoGpsRepository;

    public RecorridoGpsDaoImpl(RecorridoGpsRepository recorridoGpsRepository) {
        this.recorridoGpsRepository = recorridoGpsRepository;
    }

    @Override
    public List<RecorridoGps> listarTodos() {
        return recorridoGpsRepository.findAllByOrderByFechaHoraDescIdGpsDesc();
    }

    @Override
    public RecorridoGps guardar(RecorridoGps recorridoGps) {
        return recorridoGpsRepository.save(recorridoGps);
    }

    @Override
    public long contar() {
        return recorridoGpsRepository.count();
    }
}

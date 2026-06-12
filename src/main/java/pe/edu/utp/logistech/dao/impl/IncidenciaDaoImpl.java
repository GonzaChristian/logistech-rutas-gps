package pe.edu.utp.logistech.dao.impl;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.IncidenciaDao;
import pe.edu.utp.logistech.entity.Incidencia;
import pe.edu.utp.logistech.repository.IncidenciaRepository;

@Repository
public class IncidenciaDaoImpl implements IncidenciaDao {

    private final IncidenciaRepository incidenciaRepository;

    public IncidenciaDaoImpl(IncidenciaRepository incidenciaRepository) {
        this.incidenciaRepository = incidenciaRepository;
    }

    @Override
    public List<Incidencia> listarTodos() {
        return incidenciaRepository.findAllByOrderByFechaHoraDescIdIncidenciaDesc();
    }

    @Override
    public Incidencia guardar(Incidencia incidencia) {
        return incidenciaRepository.save(incidencia);
    }

    @Override
    public long contar() {
        return incidenciaRepository.count();
    }
}

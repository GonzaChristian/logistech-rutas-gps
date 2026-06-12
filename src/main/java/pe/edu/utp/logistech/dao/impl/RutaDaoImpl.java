package pe.edu.utp.logistech.dao.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.RutaDao;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.repository.RutaRepository;

@Repository
public class RutaDaoImpl implements RutaDao {

    private final RutaRepository rutaRepository;

    public RutaDaoImpl(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    @Override
    public List<Ruta> listarTodos() {
        return rutaRepository.findAllByOrderByFechaProgramadaDescIdRutaDesc();
    }

    @Override
    public Optional<Ruta> buscarPorId(Long idRuta) {
        return rutaRepository.findById(idRuta);
    }

    @Override
    public Ruta guardar(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    @Override
    public long contar() {
        return rutaRepository.count();
    }

    @Override
    public long contarPorEstado(EstadoRuta estado) {
        return rutaRepository.countByEstado(estado);
    }
}

package pe.edu.utp.logistech.dao.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.ConductorDao;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.repository.ConductorRepository;

@Repository
public class ConductorDaoImpl implements ConductorDao {

    private final ConductorRepository conductorRepository;

    public ConductorDaoImpl(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    @Override
    public List<Conductor> listarTodos() {
        return conductorRepository.findAll();
    }

    @Override
    public Optional<Conductor> buscarPorId(Long idConductor) {
        return conductorRepository.findById(idConductor);
    }

    @Override
    public Conductor guardar(Conductor conductor) {
        return conductorRepository.save(conductor);
    }

    @Override
    public boolean existeDni(String dni) {
        return conductorRepository.existsByDni(dni);
    }

    @Override
    public boolean existeDniEnOtroConductor(String dni, Long idConductor) {
        return conductorRepository.existsByDniAndIdConductorNot(dni, idConductor);
    }

    @Override
    public long contar() {
        return conductorRepository.count();
    }
}

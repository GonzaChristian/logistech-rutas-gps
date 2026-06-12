package pe.edu.utp.logistech.dao;

import java.util.List;
import java.util.Optional;
import pe.edu.utp.logistech.entity.Conductor;

public interface ConductorDao {

    List<Conductor> listarTodos();

    Optional<Conductor> buscarPorId(Long idConductor);

    Conductor guardar(Conductor conductor);

    boolean existeDni(String dni);

    boolean existeDniEnOtroConductor(String dni, Long idConductor);

    long contar();
}

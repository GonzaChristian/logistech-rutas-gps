package pe.edu.utp.logistech.dao;

import java.util.List;
import java.util.Optional;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

public interface RutaDao {

    List<Ruta> listarTodos();

    Optional<Ruta> buscarPorId(Long idRuta);

    Ruta guardar(Ruta ruta);

    long contar();

    long contarPorEstado(EstadoRuta estado);
}

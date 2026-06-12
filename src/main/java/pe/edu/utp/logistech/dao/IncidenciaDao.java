package pe.edu.utp.logistech.dao;

import java.util.List;
import pe.edu.utp.logistech.entity.Incidencia;

public interface IncidenciaDao {

    List<Incidencia> listarTodos();

    Incidencia guardar(Incidencia incidencia);

    long contar();
}

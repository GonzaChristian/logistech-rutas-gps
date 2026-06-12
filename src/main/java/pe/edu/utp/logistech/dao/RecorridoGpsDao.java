package pe.edu.utp.logistech.dao;

import java.util.List;
import pe.edu.utp.logistech.entity.RecorridoGps;

public interface RecorridoGpsDao {

    List<RecorridoGps> listarTodos();

    RecorridoGps guardar(RecorridoGps recorridoGps);

    long contar();
}

package pe.edu.utp.logistech.dao;

import java.util.List;
import pe.edu.utp.logistech.entity.AsignacionRuta;

public interface ReporteDao {

    List<AsignacionRuta> listarAsignacionesReporte();

    long contar();
}

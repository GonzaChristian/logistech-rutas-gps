package pe.edu.utp.logistech.service;

import java.util.List;
import pe.edu.utp.logistech.dto.RecorridoGpsFormDto;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.RecorridoGps;

public interface RecorridoGpsService {

    List<RecorridoGps> listarRegistrosGps();

    RecorridoGps registrar(RecorridoGpsFormDto form);

    List<AsignacionRuta> listarAsignacionesEnControl();

    long contarRegistrosGps();
}

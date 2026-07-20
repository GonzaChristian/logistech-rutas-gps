package pe.edu.utp.logistech.service;

import java.util.List;
import pe.edu.utp.logistech.dto.ReporteFiltroDto;
import pe.edu.utp.logistech.dto.ReporteRutaDto;
import pe.edu.utp.logistech.entity.Conductor;

public interface ReporteService {

    List<ReporteRutaDto> consultarRutas(ReporteFiltroDto filtro);

    byte[] exportarRutasExcel(ReporteFiltroDto filtro);

    List<Conductor> listarConductores();

}

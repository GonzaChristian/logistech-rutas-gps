package pe.edu.utp.logistech.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.logistech.entity.RecorridoGps;

public interface RecorridoGpsRepository extends JpaRepository<RecorridoGps, Long> {

    @EntityGraph(attributePaths = {
            "asignacion",
            "asignacion.ruta",
            "asignacion.conductor",
            "asignacion.vehiculo"
    })
    List<RecorridoGps> findAllByOrderByFechaHoraDescIdGpsDesc();
}

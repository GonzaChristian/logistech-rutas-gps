package pe.edu.utp.logistech.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

public interface RutaRepository extends JpaRepository<Ruta, Long> {

    List<Ruta> findAllByOrderByFechaProgramadaDescIdRutaDesc();

    long countByEstado(EstadoRuta estado);
}

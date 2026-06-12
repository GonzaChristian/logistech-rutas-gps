package pe.edu.utp.logistech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.logistech.entity.Conductor;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {

    boolean existsByDni(String dni);

    boolean existsByDniAndIdConductorNot(String dni, Long idConductor);
}

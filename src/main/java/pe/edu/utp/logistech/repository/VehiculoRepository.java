package pe.edu.utp.logistech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.logistech.entity.Vehiculo;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    boolean existsByPlaca(String placa);

    boolean existsByPlacaAndIdVehiculoNot(String placa, Long idVehiculo);
}

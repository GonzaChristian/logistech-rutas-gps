package pe.edu.utp.logistech.dao;

import java.util.List;
import java.util.Optional;
import pe.edu.utp.logistech.entity.Vehiculo;

public interface VehiculoDao {

    List<Vehiculo> listarTodos();

    Optional<Vehiculo> buscarPorId(Long idVehiculo);

    Vehiculo guardar(Vehiculo vehiculo);

    boolean existePlaca(String placa);

    boolean existePlacaEnOtroVehiculo(String placa, Long idVehiculo);

    long contar();
}

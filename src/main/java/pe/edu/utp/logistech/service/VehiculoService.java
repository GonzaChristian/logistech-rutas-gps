package pe.edu.utp.logistech.service;

import java.util.List;
import pe.edu.utp.logistech.dto.VehiculoFormDto;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;

public interface VehiculoService {

    List<Vehiculo> listarVehiculos();

    VehiculoFormDto obtenerFormulario(Long idVehiculo);

    Vehiculo registrar(VehiculoFormDto form);

    Vehiculo actualizar(Long idVehiculo, VehiculoFormDto form);

    void cambiarEstado(Long idVehiculo, EstadoVehiculo estado);

    List<EstadoVehiculo> listarEstadosGestion();

    long contarVehiculos();
}

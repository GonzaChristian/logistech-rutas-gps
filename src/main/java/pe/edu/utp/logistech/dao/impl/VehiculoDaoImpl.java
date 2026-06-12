package pe.edu.utp.logistech.dao.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.VehiculoDao;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.repository.VehiculoRepository;

@Repository
public class VehiculoDaoImpl implements VehiculoDao {

    private final VehiculoRepository vehiculoRepository;

    public VehiculoDaoImpl(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public List<Vehiculo> listarTodos() {
        return vehiculoRepository.findAll();
    }

    @Override
    public Optional<Vehiculo> buscarPorId(Long idVehiculo) {
        return vehiculoRepository.findById(idVehiculo);
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public boolean existePlaca(String placa) {
        return vehiculoRepository.existsByPlaca(placa);
    }

    @Override
    public boolean existePlacaEnOtroVehiculo(String placa, Long idVehiculo) {
        return vehiculoRepository.existsByPlacaAndIdVehiculoNot(placa, idVehiculo);
    }

    @Override
    public long contar() {
        return vehiculoRepository.count();
    }
}

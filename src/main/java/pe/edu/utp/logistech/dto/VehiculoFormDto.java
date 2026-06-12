package pe.edu.utp.logistech.dto;

import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;

public class VehiculoFormDto {

    private Long idVehiculo;
    private String placa;
    private String marca;
    private String modelo;
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;

    public static VehiculoFormDto desdeEntidad(Vehiculo vehiculo) {
        VehiculoFormDto dto = new VehiculoFormDto();
        dto.setIdVehiculo(vehiculo.getIdVehiculo());
        dto.setPlaca(vehiculo.getPlaca());
        dto.setMarca(vehiculo.getMarca());
        dto.setModelo(vehiculo.getModelo());
        dto.setEstado(vehiculo.getEstado());
        return dto;
    }

    public Long getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Long idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }
}

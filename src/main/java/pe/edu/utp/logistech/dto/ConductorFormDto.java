package pe.edu.utp.logistech.dto;

import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;

public class ConductorFormDto {

    private Long idConductor;
    private String nombres;
    private String apellidos;
    private String dni;
    private String licencia;
    private String telefono;
    private EstadoGeneral estado = EstadoGeneral.ACTIVO;

    public static ConductorFormDto desdeEntidad(Conductor conductor) {
        ConductorFormDto dto = new ConductorFormDto();
        dto.setIdConductor(conductor.getIdConductor());
        dto.setNombres(conductor.getNombres());
        dto.setApellidos(conductor.getApellidos());
        dto.setDni(conductor.getDni());
        dto.setLicencia(conductor.getLicencia());
        dto.setTelefono(conductor.getTelefono());
        dto.setEstado(conductor.getEstado());
        return dto;
    }

    public Long getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(Long idConductor) {
        this.idConductor = idConductor;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public EstadoGeneral getEstado() {
        return estado;
    }

    public void setEstado(EstadoGeneral estado) {
        this.estado = estado;
    }
}

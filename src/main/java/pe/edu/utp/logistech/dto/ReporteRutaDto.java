package pe.edu.utp.logistech.dto;

import java.time.LocalDate;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

public class ReporteRutaDto {

    private final Long idAsignacion;
    private final Long idRuta;
    private final String origen;
    private final String destino;
    private final LocalDate fechaProgramada;
    private final LocalDate fechaAsignacion;
    private final EstadoRuta estado;
    private final String conductor;
    private final String dniConductor;
    private final String vehiculo;
    private final String placa;

    private ReporteRutaDto(AsignacionRuta asignacion) {
        this.idAsignacion = asignacion.getIdAsignacion();
        this.idRuta = asignacion.getRuta().getIdRuta();
        this.origen = asignacion.getRuta().getOrigen();
        this.destino = asignacion.getRuta().getDestino();
        this.fechaProgramada = asignacion.getRuta().getFechaProgramada();
        this.fechaAsignacion = asignacion.getFechaAsignacion();
        this.estado = asignacion.getEstado();
        this.conductor = asignacion.getConductor().getNombres() + " " + asignacion.getConductor().getApellidos();
        this.dniConductor = asignacion.getConductor().getDni();
        this.vehiculo = asignacion.getVehiculo().getMarca() + " " + asignacion.getVehiculo().getModelo();
        this.placa = asignacion.getVehiculo().getPlaca();
    }

    public static ReporteRutaDto desdeAsignacion(AsignacionRuta asignacion) {
        return new ReporteRutaDto(asignacion);
    }

    public Long getIdAsignacion() {
        return idAsignacion;
    }

    public Long getIdRuta() {
        return idRuta;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public EstadoRuta getEstado() {
        return estado;
    }

    public String getConductor() {
        return conductor;
    }

    public String getDniConductor() {
        return dniConductor;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public String getPlaca() {
        return placa;
    }
}

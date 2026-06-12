package pe.edu.utp.logistech.dto;

import java.time.LocalDate;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

public class ReporteFiltroDto {

    private EstadoRuta estado;
    private Long idConductor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public EstadoRuta getEstado() {
        return estado;
    }

    public void setEstado(EstadoRuta estado) {
        this.estado = estado;
    }

    public Long getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(Long idConductor) {
        this.idConductor = idConductor;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}

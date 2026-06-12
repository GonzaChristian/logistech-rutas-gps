package pe.edu.utp.logistech.dto;

import java.time.LocalDate;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

public class RutaFormDto {

    private Long idRuta;
    private String origen;
    private String destino;
    private LocalDate fechaProgramada;
    private EstadoRuta estado = EstadoRuta.PROGRAMADA;

    public static RutaFormDto desdeEntidad(Ruta ruta) {
        RutaFormDto dto = new RutaFormDto();
        dto.setIdRuta(ruta.getIdRuta());
        dto.setOrigen(ruta.getOrigen());
        dto.setDestino(ruta.getDestino());
        dto.setFechaProgramada(ruta.getFechaProgramada());
        dto.setEstado(ruta.getEstado());
        return dto;
    }

    public Long getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Long idRuta) {
        this.idRuta = idRuta;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDate fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public EstadoRuta getEstado() {
        return estado;
    }

    public void setEstado(EstadoRuta estado) {
        this.estado = estado;
    }
}

package pe.edu.utp.logistech.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.Incidencia;
import pe.edu.utp.logistech.entity.RecorridoGps;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoIncidencia;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;

final class RepositoryTestData {

    private RepositoryTestData() {
    }

    static Conductor conductor(String dni, EstadoGeneral estado) {
        Conductor conductor = new Conductor();
        conductor.setNombres("Conductor");
        conductor.setApellidos(dni);
        conductor.setDni(dni);
        conductor.setLicencia("AII-B-" + dni.substring(dni.length() - 3));
        conductor.setTelefono("999" + dni.substring(dni.length() - 6));
        conductor.setEstado(estado);
        return conductor;
    }

    static Vehiculo vehiculo(String placa, EstadoVehiculo estado) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placa);
        vehiculo.setMarca("Toyota");
        vehiculo.setModelo("Hilux");
        vehiculo.setEstado(estado);
        return vehiculo;
    }

    static Ruta ruta(String origen, String destino, LocalDate fecha, EstadoRuta estado) {
        Ruta ruta = new Ruta();
        ruta.setOrigen(origen);
        ruta.setDestino(destino);
        ruta.setFechaProgramada(fecha);
        ruta.setEstado(estado);
        return ruta;
    }

    static AsignacionRuta asignacion(Ruta ruta, Conductor conductor, Vehiculo vehiculo,
                                     LocalDate fechaAsignacion, EstadoRuta estado) {
        AsignacionRuta asignacion = new AsignacionRuta();
        asignacion.setRuta(ruta);
        asignacion.setConductor(conductor);
        asignacion.setVehiculo(vehiculo);
        asignacion.setFechaAsignacion(fechaAsignacion);
        asignacion.setEstado(estado);
        return asignacion;
    }

    static RecorridoGps gps(AsignacionRuta asignacion, String latitud, String longitud, LocalDateTime fechaHora) {
        RecorridoGps gps = new RecorridoGps();
        gps.setAsignacion(asignacion);
        gps.setLatitud(new BigDecimal(latitud));
        gps.setLongitud(new BigDecimal(longitud));
        gps.setFechaHora(fechaHora);
        return gps;
    }

    static Incidencia incidencia(AsignacionRuta asignacion, String tipo, LocalDateTime fechaHora,
                                EstadoIncidencia estado) {
        Incidencia incidencia = new Incidencia();
        incidencia.setAsignacion(asignacion);
        incidencia.setTipo(tipo);
        incidencia.setDescripcion("Incidencia de prueba");
        incidencia.setFechaHora(fechaHora);
        incidencia.setEstado(estado);
        return incidencia;
    }
}

package pe.edu.utp.logistech.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
import pe.edu.utp.logistech.repository.AsignacionRutaRepository;
import pe.edu.utp.logistech.repository.ConductorRepository;
import pe.edu.utp.logistech.repository.IncidenciaRepository;
import pe.edu.utp.logistech.repository.RecorridoGpsRepository;
import pe.edu.utp.logistech.repository.RutaRepository;
import pe.edu.utp.logistech.repository.VehiculoRepository;

@Component
@Profile("demo")
public class DemoDataInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final RutaRepository rutaRepository;
    private final AsignacionRutaRepository asignacionRutaRepository;
    private final RecorridoGpsRepository recorridoGpsRepository;
    private final IncidenciaRepository incidenciaRepository;

    public DemoDataInitializer(ConductorRepository conductorRepository,
            VehiculoRepository vehiculoRepository,
            RutaRepository rutaRepository,
            AsignacionRutaRepository asignacionRutaRepository,
            RecorridoGpsRepository recorridoGpsRepository,
            IncidenciaRepository incidenciaRepository) {
        this.conductorRepository = conductorRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.rutaRepository = rutaRepository;
        this.asignacionRutaRepository = asignacionRutaRepository;
        this.recorridoGpsRepository = recorridoGpsRepository;
        this.incidenciaRepository = incidenciaRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (conductorRepository.count() > 0) {
            return;
        }

        List<Conductor> conductores = conductorRepository.saveAll(crearConductores());
        List<Vehiculo> vehiculos = vehiculoRepository.saveAll(crearVehiculos());
        List<Ruta> rutas = rutaRepository.saveAll(crearRutas());
        List<AsignacionRuta> asignaciones = asignacionRutaRepository.saveAll(
                crearAsignaciones(rutas, conductores, vehiculos));

        recorridoGpsRepository.saveAll(crearRecorridos(asignaciones));
        incidenciaRepository.saveAll(crearIncidencias(asignaciones));

        LOGGER.info("Datos demo cargados: {} conductores, {} vehiculos, {} rutas, {} asignaciones, {} GPS y {} incidencias",
                conductores.size(), vehiculos.size(), rutas.size(), asignaciones.size(),
                recorridoGpsRepository.count(), incidenciaRepository.count());
    }

    private List<Conductor> crearConductores() {
        return List.of(
                conductor("Carlos", "Mendoza Ruiz", "74125896", "A-IIb", "987410001", EstadoGeneral.ACTIVO),
                conductor("Lucia", "Fernandez Soto", "72839415", "A-IIIa", "987410002", EstadoGeneral.ACTIVO),
                conductor("Miguel", "Torres Vega", "71628594", "A-IIb", "987410003", EstadoGeneral.ACTIVO),
                conductor("Rosa", "Quispe Flores", "70394821", "A-IIIb", "987410004", EstadoGeneral.ACTIVO),
                conductor("Jorge", "Ramirez Leon", "69581734", "A-IIb", "987410005", EstadoGeneral.ACTIVO),
                conductor("Patricia", "Salazar Rojas", "68472915", "A-IIIa", "987410006", EstadoGeneral.ACTIVO),
                conductor("Diego", "Castillo Pena", "67381529", "A-IIb", "987410007", EstadoGeneral.ACTIVO),
                conductor("Elena", "Vargas Medina", "66294183", "A-IIIb", "987410008", EstadoGeneral.ACTIVO),
                conductor("Andres", "Chavez Luna", "65182749", "A-IIb", "987410009", EstadoGeneral.ACTIVO),
                conductor("Sofia", "Navarro Cruz", "64093817", "A-IIIa", "987410010", EstadoGeneral.ACTIVO),
                conductor("Luis", "Paredes Silva", "63917482", "A-IIb", "987410011", EstadoGeneral.ACTIVO),
                conductor("Monica", "Reyes Campos", "62839571", "A-IIb", "987410012", EstadoGeneral.INACTIVO));
    }

    private List<Vehiculo> crearVehiculos() {
        return List.of(
                vehiculo("BKR-201", "Hyundai", "H100", EstadoVehiculo.ASIGNADO),
                vehiculo("AFQ-482", "Isuzu", "NPR", EstadoVehiculo.ASIGNADO),
                vehiculo("C7P-315", "JAC", "X200", EstadoVehiculo.ASIGNADO),
                vehiculo("BMT-764", "Toyota", "Hiace", EstadoVehiculo.DISPONIBLE),
                vehiculo("AWL-928", "Foton", "Aumark", EstadoVehiculo.DISPONIBLE),
                vehiculo("C5R-146", "Mitsubishi", "Canter", EstadoVehiculo.DISPONIBLE),
                vehiculo("BXP-573", "Chevrolet", "N300", EstadoVehiculo.MANTENIMIENTO),
                vehiculo("A9D-807", "Kia", "K2700", EstadoVehiculo.INACTIVO),
                vehiculo("C2N-439", "Hyundai", "HD65", EstadoVehiculo.ASIGNADO),
                vehiculo("BRV-682", "Isuzu", "NLR", EstadoVehiculo.DISPONIBLE),
                vehiculo("A6K-254", "JAC", "X200", EstadoVehiculo.DISPONIBLE),
                vehiculo("C8T-931", "Toyota", "Dyna", EstadoVehiculo.MANTENIMIENTO));
    }

    private List<Ruta> crearRutas() {
        LocalDate hoy = LocalDate.now();
        return List.of(
                ruta("CD Villa El Salvador", "Tienda Miraflores", hoy.plusDays(1), EstadoRuta.PROGRAMADA),
                ruta("CD Huachipa", "Tienda San Isidro", hoy, EstadoRuta.EN_CURSO),
                ruta("CD Callao", "Tienda Los Olivos", hoy, EstadoRuta.EN_CURSO),
                ruta("CD Villa El Salvador", "Tienda Chorrillos", hoy.minusDays(1), EstadoRuta.FINALIZADA),
                ruta("CD Huachipa", "Tienda Ate", hoy.minusDays(2), EstadoRuta.FINALIZADA),
                ruta("CD Callao", "Tienda San Miguel", hoy.minusDays(3), EstadoRuta.CANCELADA),
                ruta("CD Villa El Salvador", "Tienda Surco", hoy.minusDays(4), EstadoRuta.FINALIZADA),
                ruta("CD Huachipa", "Tienda La Molina", hoy.minusDays(5), EstadoRuta.CANCELADA),
                ruta("CD Callao", "Tienda Independencia", hoy.plusDays(2), EstadoRuta.PROGRAMADA),
                ruta("CD Villa El Salvador", "Tienda Barranco", hoy.minusDays(6), EstadoRuta.FINALIZADA),
                ruta("CD Huachipa", "Tienda Santa Anita", hoy.plusDays(3), EstadoRuta.PROGRAMADA),
                ruta("CD Callao", "Tienda Magdalena", hoy.plusDays(4), EstadoRuta.PROGRAMADA),
                ruta("CD Villa El Salvador", "Tienda Lurin", hoy.plusDays(5), EstadoRuta.PROGRAMADA),
                ruta("CD Huachipa", "Tienda San Juan de Lurigancho", hoy.plusDays(6), EstadoRuta.PROGRAMADA));
    }

    private List<AsignacionRuta> crearAsignaciones(List<Ruta> rutas, List<Conductor> conductores,
            List<Vehiculo> vehiculos) {
        List<AsignacionRuta> asignaciones = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            asignaciones.add(asignacion(rutas.get(i), conductores.get(i), vehiculos.get(i)));
        }
        return asignaciones;
    }

    private List<RecorridoGps> crearRecorridos(List<AsignacionRuta> asignaciones) {
        List<RecorridoGps> recorridos = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);
        for (int i = 1; i <= 8; i++) {
            int puntos = i == 1 || i == 2 ? 5 : (i <= 4 ? 3 : 2);
            for (int punto = 0; punto < puntos; punto++) {
                recorridos.add(recorrido(asignaciones.get(i),
                        new BigDecimal("-12.046374").subtract(new BigDecimal("0.004").multiply(BigDecimal.valueOf(i + punto))),
                        new BigDecimal("-77.042793").add(new BigDecimal("0.003").multiply(BigDecimal.valueOf(i + punto))),
                        ahora.minusDays(Math.max(0, i - 2)).minusMinutes((long) (puntos - punto) * 18)));
            }
        }
        return recorridos;
    }

    private List<Incidencia> crearIncidencias(List<AsignacionRuta> asignaciones) {
        LocalDateTime ahora = LocalDateTime.now().withSecond(0).withNano(0);
        return List.of(
                incidencia(asignaciones.get(1), "TRAFICO", "Congestion vehicular en Javier Prado",
                        ahora.minusMinutes(35), EstadoIncidencia.PENDIENTE),
                incidencia(asignaciones.get(2), "RETRASO", "Demora de 20 minutos en el punto de entrega",
                        ahora.minusHours(1), EstadoIncidencia.EN_REVISION),
                incidencia(asignaciones.get(3), "CLIENTE_AUSENTE", "Encargado de tienda no se encontraba disponible",
                        ahora.minusDays(1), EstadoIncidencia.RESUELTA),
                incidencia(asignaciones.get(4), "ACCESO_RESTRINGIDO", "Ingreso bloqueado temporalmente por descarga externa",
                        ahora.minusDays(2), EstadoIncidencia.RESUELTA),
                incidencia(asignaciones.get(5), "AVERIA", "Falla electrica detectada antes de iniciar el recorrido",
                        ahora.minusDays(3), EstadoIncidencia.RESUELTA),
                incidencia(asignaciones.get(6), "CLIMA", "Lluvia intensa redujo la velocidad del recorrido",
                        ahora.minusDays(4), EstadoIncidencia.RESUELTA),
                incidencia(asignaciones.get(7), "CANCELACION", "La tienda solicito reprogramar la entrega",
                        ahora.minusDays(5), EstadoIncidencia.RESUELTA),
                incidencia(asignaciones.get(8), "DOCUMENTACION", "Guia de remision pendiente de validacion",
                        ahora.minusHours(3), EstadoIncidencia.PENDIENTE));
    }

    private Conductor conductor(String nombres, String apellidos, String dni, String licencia,
            String telefono, EstadoGeneral estado) {
        Conductor conductor = new Conductor();
        conductor.setNombres(nombres);
        conductor.setApellidos(apellidos);
        conductor.setDni(dni);
        conductor.setLicencia(licencia);
        conductor.setTelefono(telefono);
        conductor.setEstado(estado);
        return conductor;
    }

    private Vehiculo vehiculo(String placa, String marca, String modelo, EstadoVehiculo estado) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(placa);
        vehiculo.setMarca(marca);
        vehiculo.setModelo(modelo);
        vehiculo.setEstado(estado);
        return vehiculo;
    }

    private Ruta ruta(String origen, String destino, LocalDate fecha, EstadoRuta estado) {
        Ruta ruta = new Ruta();
        ruta.setOrigen(origen);
        ruta.setDestino(destino);
        ruta.setFechaProgramada(fecha);
        ruta.setEstado(estado);
        return ruta;
    }

    private AsignacionRuta asignacion(Ruta ruta, Conductor conductor, Vehiculo vehiculo) {
        AsignacionRuta asignacion = new AsignacionRuta();
        asignacion.setRuta(ruta);
        asignacion.setConductor(conductor);
        asignacion.setVehiculo(vehiculo);
        asignacion.setFechaAsignacion(ruta.getFechaProgramada().minusDays(1));
        asignacion.setEstado(ruta.getEstado());
        return asignacion;
    }

    private RecorridoGps recorrido(AsignacionRuta asignacion, BigDecimal latitud, BigDecimal longitud,
            LocalDateTime fechaHora) {
        RecorridoGps recorrido = new RecorridoGps();
        recorrido.setAsignacion(asignacion);
        recorrido.setLatitud(latitud);
        recorrido.setLongitud(longitud);
        recorrido.setFechaHora(fechaHora);
        return recorrido;
    }

    private Incidencia incidencia(AsignacionRuta asignacion, String tipo, String descripcion,
            LocalDateTime fechaHora, EstadoIncidencia estado) {
        Incidencia incidencia = new Incidencia();
        incidencia.setAsignacion(asignacion);
        incidencia.setTipo(tipo);
        incidencia.setDescripcion(descripcion);
        incidencia.setFechaHora(fechaHora);
        incidencia.setEstado(estado);
        return incidencia;
    }
}

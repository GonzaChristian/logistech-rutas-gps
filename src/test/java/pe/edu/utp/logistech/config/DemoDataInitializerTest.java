package pe.edu.utp.logistech.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;
import pe.edu.utp.logistech.repository.AsignacionRutaRepository;
import pe.edu.utp.logistech.repository.ConductorRepository;
import pe.edu.utp.logistech.repository.IncidenciaRepository;
import pe.edu.utp.logistech.repository.RecorridoGpsRepository;
import pe.edu.utp.logistech.repository.RutaRepository;
import pe.edu.utp.logistech.repository.VehiculoRepository;

@SpringBootTest
@ActiveProfiles("demo")
class DemoDataInitializerTest {

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private AsignacionRutaRepository asignacionRutaRepository;

    @Autowired
    private RecorridoGpsRepository recorridoGpsRepository;

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Test
    void perfilDemoDebeCargarDatosRelacionadosYCoherentes() {
        assertThat(conductorRepository.count()).isEqualTo(12);
        assertThat(vehiculoRepository.count()).isEqualTo(12);
        assertThat(rutaRepository.count()).isEqualTo(14);
        assertThat(asignacionRutaRepository.count()).isEqualTo(10);
        assertThat(recorridoGpsRepository.count()).isEqualTo(24);
        assertThat(incidenciaRepository.count()).isEqualTo(8);

        Set<EstadoRuta> estadosActivos = Set.of(EstadoRuta.PROGRAMADA, EstadoRuta.EN_CURSO);
        assertThat(asignacionRutaRepository.findAllByOrderByFechaAsignacionDescIdAsignacionDesc())
                .filteredOn(asignacion -> estadosActivos.contains(asignacion.getEstado()))
                .allSatisfy(asignacion -> {
                    assertThat(asignacion.getRuta().getEstado()).isEqualTo(asignacion.getEstado());
                    assertThat(asignacion.getVehiculo().getEstado()).isEqualTo(EstadoVehiculo.ASIGNADO);
                });
    }
}

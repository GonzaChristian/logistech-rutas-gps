package pe.edu.utp.logistech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.RecorridoGps;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;

@DataJpaTest
@ActiveProfiles("test")
class RecorridoGpsRepositoryTest {

    @Autowired
    private RecorridoGpsRepository recorridoGpsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllDebeOrdenarRegistrosGpsPorFechaHoraEIdDescendente() {
        AsignacionRuta asignacion = crearAsignacion();
        RecorridoGps antiguo = entityManager.persistAndFlush(RepositoryTestData.gps(asignacion,
                "-12.01000000", "-77.01000000", LocalDateTime.of(2026, 6, 12, 8, 0)));
        RecorridoGps nuevoA = entityManager.persistAndFlush(RepositoryTestData.gps(asignacion,
                "-12.02000000", "-77.02000000", LocalDateTime.of(2026, 6, 12, 9, 0)));
        RecorridoGps nuevoB = entityManager.persistAndFlush(RepositoryTestData.gps(asignacion,
                "-12.03000000", "-77.03000000", LocalDateTime.of(2026, 6, 12, 9, 0)));
        entityManager.clear();

        List<RecorridoGps> registros = recorridoGpsRepository.findAllByOrderByFechaHoraDescIdGpsDesc();

        assertThat(registros).extracting(RecorridoGps::getIdGps)
                .containsExactly(nuevoB.getIdGps(), nuevoA.getIdGps(), antiguo.getIdGps());
        assertThat(registros.get(0).getAsignacion().getRuta().getDestino()).isEqualTo("Tienda GPS");
    }

    private AsignacionRuta crearAsignacion() {
        Ruta ruta = entityManager.persist(RepositoryTestData.ruta("CD GPS", "Tienda GPS",
                LocalDate.of(2026, 6, 12), EstadoRuta.EN_CURSO));
        Conductor conductor = entityManager.persist(RepositoryTestData.conductor("73000001", EstadoGeneral.ACTIVO));
        Vehiculo vehiculo = entityManager.persist(RepositoryTestData.vehiculo("GPS-001", EstadoVehiculo.ASIGNADO));
        return entityManager.persistAndFlush(RepositoryTestData.asignacion(ruta, conductor, vehiculo,
                LocalDate.of(2026, 6, 12), EstadoRuta.EN_CURSO));
    }
}

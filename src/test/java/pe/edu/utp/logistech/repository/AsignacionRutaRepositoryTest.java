package pe.edu.utp.logistech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utp.logistech.entity.AsignacionRuta;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;

@DataJpaTest
@ActiveProfiles("test")
class AsignacionRutaRepositoryTest {

    @Autowired
    private AsignacionRutaRepository asignacionRutaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsDebeDetectarAsignacionesActivasPorRutaConductorYVehiculo() {
        Ruta ruta = entityManager.persist(RepositoryTestData.ruta("CD Norte", "Tienda Norte",
                LocalDate.of(2026, 6, 12), EstadoRuta.PROGRAMADA));
        Conductor conductor = entityManager.persist(RepositoryTestData.conductor("71000001", EstadoGeneral.ACTIVO));
        Vehiculo vehiculo = entityManager.persist(RepositoryTestData.vehiculo("ACT-001", EstadoVehiculo.ASIGNADO));
        entityManager.persistAndFlush(RepositoryTestData.asignacion(ruta, conductor, vehiculo,
                LocalDate.of(2026, 6, 12), EstadoRuta.EN_CURSO));

        List<EstadoRuta> activos = List.of(EstadoRuta.PROGRAMADA, EstadoRuta.EN_CURSO);

        assertThat(asignacionRutaRepository.existsByRuta_IdRutaAndEstadoIn(ruta.getIdRuta(), activos)).isTrue();
        assertThat(asignacionRutaRepository.existsByConductor_IdConductorAndEstadoIn(
                conductor.getIdConductor(), activos)).isTrue();
        assertThat(asignacionRutaRepository.existsByVehiculo_IdVehiculoAndEstadoIn(
                vehiculo.getIdVehiculo(), activos)).isTrue();
    }

    @Test
    void existsDebeIgnorarAsignacionesFinalizadasAlBuscarActivas() {
        Ruta ruta = entityManager.persist(RepositoryTestData.ruta("CD Sur", "Tienda Sur",
                LocalDate.of(2026, 6, 13), EstadoRuta.FINALIZADA));
        Conductor conductor = entityManager.persist(RepositoryTestData.conductor("71000002", EstadoGeneral.ACTIVO));
        Vehiculo vehiculo = entityManager.persist(RepositoryTestData.vehiculo("ACT-002", EstadoVehiculo.DISPONIBLE));
        entityManager.persistAndFlush(RepositoryTestData.asignacion(ruta, conductor, vehiculo,
                LocalDate.of(2026, 6, 13), EstadoRuta.FINALIZADA));

        assertThat(asignacionRutaRepository.existsByRuta_IdRutaAndEstadoIn(
                ruta.getIdRuta(), List.of(EstadoRuta.PROGRAMADA, EstadoRuta.EN_CURSO))).isFalse();
    }

    @Test
    void findAllDebeOrdenarPorFechaAsignacionEIdDescendente() {
        AsignacionRuta antigua = crearAsignacion("72000001", "ORD-001",
                LocalDate.of(2026, 6, 10), EstadoRuta.PROGRAMADA);
        AsignacionRuta nuevaA = crearAsignacion("72000002", "ORD-002",
                LocalDate.of(2026, 6, 12), EstadoRuta.PROGRAMADA);
        AsignacionRuta nuevaB = crearAsignacion("72000003", "ORD-003",
                LocalDate.of(2026, 6, 12), EstadoRuta.EN_CURSO);

        List<AsignacionRuta> asignaciones = asignacionRutaRepository.findAllByOrderByFechaAsignacionDescIdAsignacionDesc();

        assertThat(asignaciones).extracting(AsignacionRuta::getIdAsignacion)
                .containsExactly(nuevaB.getIdAsignacion(), nuevaA.getIdAsignacion(), antigua.getIdAsignacion());
        assertThat(asignaciones.get(0).getRuta().getOrigen()).isEqualTo("CD ORD-003");
    }

    private AsignacionRuta crearAsignacion(String dni, String placa, LocalDate fecha, EstadoRuta estado) {
        Ruta ruta = entityManager.persist(RepositoryTestData.ruta("CD " + placa, "Tienda " + placa, fecha, estado));
        Conductor conductor = entityManager.persist(RepositoryTestData.conductor(dni, EstadoGeneral.ACTIVO));
        Vehiculo vehiculo = entityManager.persist(RepositoryTestData.vehiculo(placa, EstadoVehiculo.ASIGNADO));
        return entityManager.persistAndFlush(RepositoryTestData.asignacion(ruta, conductor, vehiculo, fecha, estado));
    }
}

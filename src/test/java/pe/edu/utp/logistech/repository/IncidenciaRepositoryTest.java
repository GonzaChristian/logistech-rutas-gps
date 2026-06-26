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
import pe.edu.utp.logistech.entity.Incidencia;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.EstadoIncidencia;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;

@DataJpaTest
@ActiveProfiles("test")
class IncidenciaRepositoryTest {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllDebeOrdenarIncidenciasPorFechaHoraEIdDescendente() {
        AsignacionRuta asignacion = crearAsignacion();
        Incidencia antigua = entityManager.persistAndFlush(RepositoryTestData.incidencia(asignacion,
                "RETRASO", LocalDateTime.of(2026, 6, 12, 8, 0), EstadoIncidencia.PENDIENTE));
        Incidencia nuevaA = entityManager.persistAndFlush(RepositoryTestData.incidencia(asignacion,
                "AVERIA", LocalDateTime.of(2026, 6, 12, 9, 0), EstadoIncidencia.EN_REVISION));
        Incidencia nuevaB = entityManager.persistAndFlush(RepositoryTestData.incidencia(asignacion,
                "DESVIO", LocalDateTime.of(2026, 6, 12, 9, 0), EstadoIncidencia.RESUELTA));
        entityManager.clear();

        List<Incidencia> incidencias = incidenciaRepository.findAllByOrderByFechaHoraDescIdIncidenciaDesc();

        assertThat(incidencias).extracting(Incidencia::getIdIncidencia)
                .containsExactly(nuevaB.getIdIncidencia(), nuevaA.getIdIncidencia(), antigua.getIdIncidencia());
        assertThat(incidencias.get(0).getAsignacion().getVehiculo().getPlaca()).isEqualTo("INC-001");
    }

    private AsignacionRuta crearAsignacion() {
        Ruta ruta = entityManager.persist(RepositoryTestData.ruta("CD INC", "Tienda INC",
                LocalDate.of(2026, 6, 12), EstadoRuta.EN_CURSO));
        Conductor conductor = entityManager.persist(RepositoryTestData.conductor("74000001", EstadoGeneral.ACTIVO));
        Vehiculo vehiculo = entityManager.persist(RepositoryTestData.vehiculo("INC-001", EstadoVehiculo.ASIGNADO));
        return entityManager.persistAndFlush(RepositoryTestData.asignacion(ruta, conductor, vehiculo,
                LocalDate.of(2026, 6, 12), EstadoRuta.EN_CURSO));
    }
}

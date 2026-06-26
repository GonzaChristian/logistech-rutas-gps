package pe.edu.utp.logistech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utp.logistech.entity.Ruta;
import pe.edu.utp.logistech.entity.enums.EstadoRuta;

@DataJpaTest
@ActiveProfiles("test")
class RutaRepositoryTest {

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByOrderByFechaProgramadaDescIdRutaDescDebeOrdenarPorFechaEId() {
        Ruta antigua = entityManager.persistAndFlush(RepositoryTestData.ruta("CD 1", "Tienda 1",
                LocalDate.of(2026, 6, 10), EstadoRuta.PROGRAMADA));
        Ruta nuevaA = entityManager.persistAndFlush(RepositoryTestData.ruta("CD 2", "Tienda 2",
                LocalDate.of(2026, 6, 12), EstadoRuta.PROGRAMADA));
        Ruta nuevaB = entityManager.persistAndFlush(RepositoryTestData.ruta("CD 3", "Tienda 3",
                LocalDate.of(2026, 6, 12), EstadoRuta.EN_CURSO));

        List<Ruta> rutas = rutaRepository.findAllByOrderByFechaProgramadaDescIdRutaDesc();

        assertThat(rutas).extracting(Ruta::getIdRuta)
                .containsExactly(nuevaB.getIdRuta(), nuevaA.getIdRuta(), antigua.getIdRuta());
    }

    @Test
    void countByEstadoDebeContarRutasPorEstado() {
        entityManager.persist(RepositoryTestData.ruta("CD 1", "Tienda 1",
                LocalDate.of(2026, 6, 10), EstadoRuta.PROGRAMADA));
        entityManager.persist(RepositoryTestData.ruta("CD 2", "Tienda 2",
                LocalDate.of(2026, 6, 11), EstadoRuta.PROGRAMADA));
        entityManager.persistAndFlush(RepositoryTestData.ruta("CD 3", "Tienda 3",
                LocalDate.of(2026, 6, 12), EstadoRuta.FINALIZADA));

        assertThat(rutaRepository.countByEstado(EstadoRuta.PROGRAMADA)).isEqualTo(2);
        assertThat(rutaRepository.countByEstado(EstadoRuta.FINALIZADA)).isEqualTo(1);
    }
}

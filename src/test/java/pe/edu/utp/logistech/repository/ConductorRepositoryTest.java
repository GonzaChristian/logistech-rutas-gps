package pe.edu.utp.logistech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utp.logistech.entity.Conductor;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;

@DataJpaTest
@ActiveProfiles("test")
class ConductorRepositoryTest {

    @Autowired
    private ConductorRepository conductorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByDniDebeEncontrarConductorPersistido() {
        entityManager.persistAndFlush(RepositoryTestData.conductor("70000001", EstadoGeneral.ACTIVO));

        assertThat(conductorRepository.existsByDni("70000001")).isTrue();
        assertThat(conductorRepository.existsByDni("79999999")).isFalse();
    }

    @Test
    void existsByDniAndIdConductorNotDebeExcluirElMismoRegistro() {
        Conductor conductor = entityManager.persistAndFlush(
                RepositoryTestData.conductor("70000002", EstadoGeneral.ACTIVO));
        entityManager.persistAndFlush(RepositoryTestData.conductor("70000003", EstadoGeneral.ACTIVO));

        assertThat(conductorRepository.existsByDniAndIdConductorNot("70000002", conductor.getIdConductor()))
                .isFalse();
        assertThat(conductorRepository.existsByDniAndIdConductorNot("70000002", 999L))
                .isTrue();
    }
}

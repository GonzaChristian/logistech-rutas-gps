package pe.edu.utp.logistech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import pe.edu.utp.logistech.entity.Vehiculo;
import pe.edu.utp.logistech.entity.enums.EstadoVehiculo;

@DataJpaTest
@ActiveProfiles("test")
class VehiculoRepositoryTest {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByPlacaDebeEncontrarVehiculoPersistido() {
        entityManager.persistAndFlush(RepositoryTestData.vehiculo("ABC-123", EstadoVehiculo.DISPONIBLE));

        assertThat(vehiculoRepository.existsByPlaca("ABC-123")).isTrue();
        assertThat(vehiculoRepository.existsByPlaca("ZZZ-999")).isFalse();
    }

    @Test
    void existsByPlacaAndIdVehiculoNotDebeExcluirElMismoRegistro() {
        Vehiculo vehiculo = entityManager.persistAndFlush(
                RepositoryTestData.vehiculo("DEF-456", EstadoVehiculo.DISPONIBLE));
        entityManager.persistAndFlush(RepositoryTestData.vehiculo("GHI-789", EstadoVehiculo.DISPONIBLE));

        assertThat(vehiculoRepository.existsByPlacaAndIdVehiculoNot("DEF-456", vehiculo.getIdVehiculo()))
                .isFalse();
        assertThat(vehiculoRepository.existsByPlacaAndIdVehiculoNot("DEF-456", 999L))
                .isTrue();
    }
}

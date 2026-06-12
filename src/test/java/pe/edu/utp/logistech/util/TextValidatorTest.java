package pe.edu.utp.logistech.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TextValidatorTest {

    @Test
    void requeridoDebeLimpiarTextoValido() {
        assertThat(TextValidator.requerido("  ABC-123  ", "placa")).isEqualTo("ABC-123");
    }

    @Test
    void requeridoDebeRechazarTextoVacio() {
        assertThatThrownBy(() -> TextValidator.requerido("   ", "placa"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("placa");
    }
}

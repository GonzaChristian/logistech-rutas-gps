package pe.edu.utp.logistech.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.utp.logistech.dao.UsuarioDao;
import pe.edu.utp.logistech.entity.Usuario;
import pe.edu.utp.logistech.entity.enums.Rol;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UsuarioDao usuarioDao;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void runDebeCrearUsuariosDemoConPasswordBCryptSiNoHayUsuarios() {
        when(usuarioDao.contar()).thenReturn(0L);
        List<Usuario> guardados = new ArrayList<>();
        when(usuarioDao.guardar(usuarioCaptor.capture())).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            guardados.add(usuario);
            return usuario;
        });

        DataInitializer initializer = new DataInitializer(usuarioDao, passwordEncoder);
        initializer.run(new DefaultApplicationArguments());

        assertThat(guardados).hasSize(2);
        assertThat(guardados).extracting(Usuario::getRol).containsExactly(Rol.ADMIN, Rol.SUPERVISOR);
        assertThat(guardados.get(0).getContrasena()).startsWith("$2");
        assertThat(passwordEncoder.matches("admin123", guardados.get(0).getContrasena())).isTrue();
        assertThat(passwordEncoder.matches("supervisor123", guardados.get(1).getContrasena())).isTrue();
    }

    @Test
    void runNoDebeCrearUsuariosSiYaExisten() {
        when(usuarioDao.contar()).thenReturn(2L);

        DataInitializer initializer = new DataInitializer(usuarioDao, passwordEncoder);
        initializer.run(new DefaultApplicationArguments());

        verify(usuarioDao, never()).guardar(any(Usuario.class));
    }
}

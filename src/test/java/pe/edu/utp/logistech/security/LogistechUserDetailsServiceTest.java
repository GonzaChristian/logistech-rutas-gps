package pe.edu.utp.logistech.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pe.edu.utp.logistech.dao.UsuarioDao;
import pe.edu.utp.logistech.entity.Usuario;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.Rol;

@ExtendWith(MockitoExtension.class)
class LogistechUserDetailsServiceTest {

    @Mock
    private UsuarioDao usuarioDao;

    @InjectMocks
    private LogistechUserDetailsService userDetailsService;

    @Test
    void loadUserByUsernameDebeRetornarUsuarioActivoConRol() {
        Usuario usuario = crearUsuario("supervisor@logistech.local", Rol.SUPERVISOR, EstadoGeneral.ACTIVO);
        when(usuarioDao.buscarPorCorreo("supervisor@logistech.local")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername(" supervisor@logistech.local ");

        assertThat(userDetails.getUsername()).isEqualTo("supervisor@logistech.local");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_SUPERVISOR");
    }

    @Test
    void loadUserByUsernameDebeDeshabilitarUsuarioInactivo() {
        Usuario usuario = crearUsuario("admin@logistech.local", Rol.ADMIN, EstadoGeneral.INACTIVO);
        when(usuarioDao.buscarPorCorreo("admin@logistech.local")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@logistech.local");

        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsernameDebeLanzarErrorSiNoExiste() {
        when(usuarioDao.buscarPorCorreo("nadie@logistech.local")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nadie@logistech.local"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    private Usuario crearUsuario(String correo, Rol rol, EstadoGeneral estado) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario Demo");
        usuario.setCorreo(correo);
        usuario.setContrasena("$2a$10$dXJ3SW6G7P50lGmZqYxEbeW3O7g8A1IygzYg4Lf.2mRyRYQfHfA9y");
        usuario.setRol(rol);
        usuario.setEstado(estado);
        return usuario;
    }
}

package pe.edu.utp.logistech.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.utp.logistech.dao.UsuarioDao;
import pe.edu.utp.logistech.entity.Usuario;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;

@Service
public class LogistechUserDetailsService implements UserDetailsService {

    private final UsuarioDao usuarioDao;

    public LogistechUserDetailsService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        String correoNormalizado = StringUtils.trimToEmpty(correo).toLowerCase();
        Usuario usuario = usuarioDao.buscarPorCorreo(correoNormalizado)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        boolean activo = EstadoGeneral.ACTIVO.equals(usuario.getEstado());
        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())
                .roles(usuario.getRol().name())
                .disabled(!activo)
                .build();
    }
}

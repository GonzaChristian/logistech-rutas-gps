package pe.edu.utp.logistech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.logistech.dao.UsuarioDao;
import pe.edu.utp.logistech.entity.Usuario;
import pe.edu.utp.logistech.entity.enums.EstadoGeneral;
import pe.edu.utp.logistech.entity.enums.Rol;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioDao usuarioDao;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioDao usuarioDao, PasswordEncoder passwordEncoder) {
        this.usuarioDao = usuarioDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioDao.contar() > 0) {
            return;
        }

        usuarioDao.guardar(crearUsuarioDemo("Administrador LOGISTECH", "admin@logistech.local", "admin123", Rol.ADMIN));
        usuarioDao.guardar(crearUsuarioDemo("Supervisor Logistico", "supervisor@logistech.local", "supervisor123", Rol.SUPERVISOR));
        LOGGER.info("Usuarios demo creados con contrasenas cifradas BCrypt");
    }

    private Usuario crearUsuarioDemo(String nombre, String correo, String contrasenaPlano, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(contrasenaPlano));
        usuario.setRol(rol);
        usuario.setEstado(EstadoGeneral.ACTIVO);
        return usuario;
    }
}

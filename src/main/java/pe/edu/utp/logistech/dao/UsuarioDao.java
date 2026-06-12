package pe.edu.utp.logistech.dao;

import java.util.Optional;
import pe.edu.utp.logistech.entity.Usuario;

public interface UsuarioDao {

    Optional<Usuario> buscarPorCorreo(String correo);

    Usuario guardar(Usuario usuario);

    long contar();
}

package pe.edu.utp.logistech.dao.impl;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import pe.edu.utp.logistech.dao.UsuarioDao;
import pe.edu.utp.logistech.entity.Usuario;
import pe.edu.utp.logistech.repository.UsuarioRepository;

@Repository
public class UsuarioDaoImpl implements UsuarioDao {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDaoImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public long contar() {
        return usuarioRepository.count();
    }
}

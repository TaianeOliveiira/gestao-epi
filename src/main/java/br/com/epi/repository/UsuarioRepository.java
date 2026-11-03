package br.com.epi.repository;

import br.com.epi.model.Perfil;
import br.com.epi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByLoginAndIdNot(String login, Long id);

    List<Usuario> findAllByOrderByNomeAsc();

    List<Usuario> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    long countByPerfilAndAtivoTrue(Perfil perfil);
}
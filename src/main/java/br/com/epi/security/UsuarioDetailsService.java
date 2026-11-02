package br.com.epi.security;

import br.com.epi.model.Usuario;
import br.com.epi.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        String chave = login == null ? "" : login.trim().toLowerCase();
        Usuario u = repository.findByLogin(chave)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new UsuarioLogado(u.getId(), u.getNome(), u.getPerfil(), u.getLogin(), u.getSenhaHash(), u.isAtivo());
    }
}
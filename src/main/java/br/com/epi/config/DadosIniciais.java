package br.com.epi.config;

import br.com.epi.model.Perfil;
import br.com.epi.model.Usuario;
import br.com.epi.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciais implements ApplicationRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public DadosIniciais(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            return;
        }
        criar("Administrador do Sistema", "admin", "admin123", Perfil.ADMINISTRADOR);
        criar("Técnico de Segurança", "tecnico", "tecnico123", Perfil.TECNICO);
    }

    private void criar(String nome, String login, String senha, Perfil perfil) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setLogin(login);
        u.setSenhaHash(encoder.encode(senha));
        u.setPerfil(perfil);
        u.setAtivo(true);
        repository.save(u);
    }
}
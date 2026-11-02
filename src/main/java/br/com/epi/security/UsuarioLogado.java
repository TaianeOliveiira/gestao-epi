package br.com.epi.security;

import br.com.epi.model.Perfil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UsuarioLogado extends User {

    private final Long id;
    private final String nome;
    private final Perfil perfil;

    public UsuarioLogado(Long id, String nome, Perfil perfil, String login, String senhaHash, boolean ativo) {
        super(login, senhaHash, ativo, true, true, true,
                List.of(new SimpleGrantedAuthority(perfil.getPapel())));
        this.id = id;
        this.nome = nome;
        this.perfil = perfil;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }

    public Perfil getPerfil() { return perfil; }
}
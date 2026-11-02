package br.com.epi.controller;

import br.com.epi.model.Perfil;
import br.com.epi.model.Usuario;
import br.com.epi.model.UsuarioForm;
import br.com.epi.repository.UsuarioRepository;
import br.com.epi.security.UsuarioLogado;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public UsuarioController(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @GetMapping
    public String listar(@RequestParam(name = "nome", required = false, defaultValue = "") String nome,
                         @AuthenticationPrincipal UsuarioLogado logado, Model model) {
        String busca = nome.trim();
        model.addAttribute("usuarios", busca.isEmpty()
                ? repository.findAllByOrderByNomeAsc()
                : repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca));
        model.addAttribute("nome", busca);
        model.addAttribute("logadoId", logado.getId());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new UsuarioForm());
        preparar(model, false);
        return "usuarios/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute("form") @Valid UsuarioForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        String login = normalizar(form.getLogin());
        if (login != null && !login.isEmpty() && repository.existsByLogin(login)) {
            result.rejectValue("login", "duplicado", "Este login já está em uso");
        }
        validarSenha(form, result, true);

        if (result.hasErrors()) {
            preparar(model, false);
            model.addAttribute("erro", "Falha ao cadastrar: corrija os campos destacados.");
            return "usuarios/form";
        }

        Usuario u = new Usuario();
        u.setNome(form.getNome().trim());
        u.setLogin(login);
        u.setPerfil(form.getPerfil());
        u.setAtivo(form.isAtivo());
        u.setSenhaHash(encoder.encode(form.getSenha()));
        repository.save(u);

        redirect.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso!");
        return "redirect:/usuarios/novo";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Usuario> encontrado = repository.findById(id);
        if (encontrado.isEmpty()) {
            redirect.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/usuarios";
        }
        Usuario u = encontrado.get();
        UsuarioForm form = new UsuarioForm();
        form.setId(u.getId());
        form.setNome(u.getNome());
        form.setLogin(u.getLogin());
        form.setPerfil(u.getPerfil());
        form.setAtivo(u.isAtivo());
        model.addAttribute("form", form);
        preparar(model, true);
        return "usuarios/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("form") @Valid UsuarioForm form, BindingResult result,
                            @AuthenticationPrincipal UsuarioLogado logado,
                            Model model, RedirectAttributes redirect) {
        Optional<Usuario> encontrado = repository.findById(id);
        if (encontrado.isEmpty()) {
            redirect.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/usuarios";
        }
        Usuario u = encontrado.get();
        form.setId(id);

        String login = normalizar(form.getLogin());
        if (login != null && !login.isEmpty() && repository.existsByLoginAndIdNot(login, id)) {
            result.rejectValue("login", "duplicado", "Este login já está em uso");
        }
        validarSenha(form, result, false);

        boolean eraAdminAtivo = u.getPerfil() == Perfil.ADMINISTRADOR && u.isAtivo();
        boolean continuaAdminAtivo = form.getPerfil() == Perfil.ADMINISTRADOR && form.isAtivo();
        if (eraAdminAtivo && !continuaAdminAtivo) {
            if (id.equals(logado.getId())) {
                result.rejectValue("ativo", "proprio",
                        "Você não pode remover o seu próprio acesso de administrador");
            } else if (repository.countByPerfilAndAtivoTrue(Perfil.ADMINISTRADOR) <= 1) {
                result.rejectValue("ativo", "ultimo",
                        "É preciso manter pelo menos um administrador ativo");
            }
        }

        if (result.hasErrors()) {
            preparar(model, true);
            model.addAttribute("erro", "Falha ao atualizar: corrija os campos destacados.");
            return "usuarios/form";
        }

        u.setNome(form.getNome().trim());
        u.setLogin(login);
        u.setPerfil(form.getPerfil());
        u.setAtivo(form.isAtivo());
        if (form.getSenha() != null && !form.getSenha().isEmpty()) {
            u.setSenhaHash(encoder.encode(form.getSenha()));
        }
        repository.save(u);

        redirect.addFlashAttribute("sucesso", "Usuário atualizado com sucesso!");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, @AuthenticationPrincipal UsuarioLogado logado,
                          RedirectAttributes redirect) {
        Optional<Usuario> encontrado = repository.findById(id);
        if (encontrado.isEmpty()) {
            redirect.addFlashAttribute("erro", "Usuário não encontrado.");
        } else if (id.equals(logado.getId())) {
            redirect.addFlashAttribute("erro", "Você não pode excluir o seu próprio usuário.");
        } else if (encontrado.get().getPerfil() == Perfil.ADMINISTRADOR && encontrado.get().isAtivo()
                && repository.countByPerfilAndAtivoTrue(Perfil.ADMINISTRADOR) <= 1) {
            redirect.addFlashAttribute("erro", "É preciso manter pelo menos um administrador ativo.");
        } else {
            repository.deleteById(id);
            redirect.addFlashAttribute("sucesso", "Usuário excluído com sucesso!");
        }
        return "redirect:/usuarios";
    }

    private void preparar(Model model, boolean edicao) {
        model.addAttribute("edicao", edicao);
        model.addAttribute("perfis", Perfil.values());
    }

    private static String normalizar(String login) {
        return login == null ? null : login.trim().toLowerCase();
    }

    private static void validarSenha(UsuarioForm form, BindingResult result, boolean obrigatoria) {
        String senha = form.getSenha() == null ? "" : form.getSenha();
        if (senha.isEmpty()) {
            if (obrigatoria) {
                result.rejectValue("senha", "obrigatoria", "A senha é obrigatória");
            }
            return;
        }
        if (senha.length() < 6) {
            result.rejectValue("senha", "curta", "A senha deve ter no mínimo 6 caracteres");
        } else if (!senha.equals(form.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "diferente", "As senhas não conferem");
        }
    }
}
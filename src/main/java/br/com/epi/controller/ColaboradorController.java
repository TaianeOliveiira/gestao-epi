package br.com.epi.controller;

import br.com.epi.model.Colaborador;
import br.com.epi.repository.ColaboradorRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ColaboradorController {

    private final ColaboradorRepository repository;

    public ColaboradorController(ColaboradorRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/colaboradores";
    }

    // ---------- LISTAR / PESQUISAR POR NOME ----------
    @GetMapping("/colaboradores")
    public String listar(@RequestParam(name = "nome", required = false, defaultValue = "") String nome,
                         Model model) {
        String busca = nome.trim();
        model.addAttribute("colaboradores", busca.isEmpty()
                ? repository.findAllByOrderByNomeAsc()
                : repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca));
        model.addAttribute("nome", busca);
        return "colaboradores/lista";
    }

    // ---------- CADASTRAR ----------
    @GetMapping("/colaboradores/novo")
    public String novo(Model model) {
        model.addAttribute("colaborador", new Colaborador());
        model.addAttribute("edicao", false);
        return "colaboradores/form";
    }

    @PostMapping("/colaboradores")
    public String salvar(@Valid Colaborador colaborador,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("edicao", false);
            model.addAttribute("erro", "Falha ao cadastrar: corrija os campos destacados.");
            return "colaboradores/form";
        }
        try {
            colaborador.setId(null);
            repository.save(colaborador);
            redirect.addFlashAttribute("sucesso", "Colaborador cadastrado com sucesso!");
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("edicao", false);
            model.addAttribute("erro", "Falha ao cadastrar: matrícula ou CPF já cadastrado.");
            return "colaboradores/form";
        }
        // permanece na tela de cadastro
        return "redirect:/colaboradores/novo";
    }

    // ---------- EDITAR ----------
    @GetMapping("/colaboradores/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return repository.findById(id).map(c -> {
            model.addAttribute("colaborador", c);
            model.addAttribute("edicao", true);
            return "colaboradores/form";
        }).orElseGet(() -> {
            redirect.addFlashAttribute("erro", "Colaborador não encontrado.");
            return "redirect:/colaboradores";
        });
    }

    @PostMapping("/colaboradores/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid Colaborador colaborador,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirect) {
        if (!repository.existsById(id)) {
            redirect.addFlashAttribute("erro", "Colaborador não encontrado.");
            return "redirect:/colaboradores";
        }
        if (result.hasErrors()) {
            colaborador.setId(id);
            model.addAttribute("edicao", true);
            model.addAttribute("erro", "Falha ao atualizar: corrija os campos destacados.");
            return "colaboradores/form";
        }
        try {
            colaborador.setId(id);
            repository.save(colaborador);
            redirect.addFlashAttribute("sucesso", "Colaborador atualizado com sucesso!");
        } catch (DataIntegrityViolationException e) {
            colaborador.setId(id);
            model.addAttribute("edicao", true);
            model.addAttribute("erro", "Falha ao atualizar: matrícula ou CPF já cadastrado.");
            return "colaboradores/form";
        }
        return "redirect:/colaboradores";
    }

    // ---------- EXCLUIR ----------
    @PostMapping("/colaboradores/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                redirect.addFlashAttribute("sucesso", "Colaborador excluído com sucesso!");
            } else {
                redirect.addFlashAttribute("erro", "Colaborador não encontrado.");
            }
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro",
                    "Não é possível excluir: o colaborador possui empréstimos de EPI registrados.");
        }
        return "redirect:/colaboradores";
    }
}

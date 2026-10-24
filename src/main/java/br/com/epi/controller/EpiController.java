package br.com.epi.controller;

import br.com.epi.model.Epi;
import br.com.epi.repository.EpiRepository;
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
public class EpiController {

    private final EpiRepository repository;

    public EpiController(EpiRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/equipamentos")
    public String listar(@RequestParam(name = "nome", required = false, defaultValue = "") String nome,
                         Model model) {
        String busca = nome.trim();
        model.addAttribute("equipamentos", busca.isEmpty()
                ? repository.findAllByOrderByNomeAsc()
                : repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca));
        model.addAttribute("nome", busca);
        return "equipamentos/lista";
    }

    @GetMapping("/equipamentos/novo")
    public String novo(Model model) {
        model.addAttribute("epi", new Epi());
        model.addAttribute("edicao", false);
        return "equipamentos/form";
    }

    @PostMapping("/equipamentos")
    public String salvar(@Valid Epi epi, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("edicao", false);
            model.addAttribute("erro", "Falha ao cadastrar: corrija os campos destacados.");
            return "equipamentos/form";
        }
        try {
            epi.setId(null);
            repository.save(epi);
            redirect.addFlashAttribute("sucesso", "Equipamento cadastrado com sucesso!");
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("edicao", false);
            model.addAttribute("erro", "Falha ao cadastrar: já existe um equipamento com este CA.");
            return "equipamentos/form";
        }
        return "redirect:/equipamentos/novo";
    }

    @GetMapping("/equipamentos/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return repository.findById(id).map(e -> {
            model.addAttribute("epi", e);
            model.addAttribute("edicao", true);
            return "equipamentos/form";
        }).orElseGet(() -> {
            redirect.addFlashAttribute("erro", "Equipamento não encontrado.");
            return "redirect:/equipamentos";
        });
    }

    @PostMapping("/equipamentos/{id}")
    public String atualizar(@PathVariable Long id, @Valid Epi epi, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        if (!repository.existsById(id)) {
            redirect.addFlashAttribute("erro", "Equipamento não encontrado.");
            return "redirect:/equipamentos";
        }
        epi.setId(id);
        if (result.hasErrors()) {
            model.addAttribute("edicao", true);
            model.addAttribute("erro", "Falha ao atualizar: corrija os campos destacados.");
            return "equipamentos/form";
        }
        try {
            repository.save(epi);
            redirect.addFlashAttribute("sucesso", "Equipamento atualizado com sucesso!");
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("edicao", true);
            model.addAttribute("erro", "Falha ao atualizar: já existe um equipamento com este CA.");
            return "equipamentos/form";
        }
        return "redirect:/equipamentos";
    }

    @PostMapping("/equipamentos/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                redirect.addFlashAttribute("sucesso", "Equipamento excluído com sucesso!");
            } else {
                redirect.addFlashAttribute("erro", "Equipamento não encontrado.");
            }
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro",
                    "Não é possível excluir: o equipamento possui empréstimos registrados.");
        }
        return "redirect:/equipamentos";
    }
}

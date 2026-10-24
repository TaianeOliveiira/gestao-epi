package br.com.epi.controller;

import br.com.epi.model.Emprestimo;
import br.com.epi.model.StatusEmprestimo;
import br.com.epi.repository.EmprestimoRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class RelatorioController {

    private final EmprestimoRepository emprestimoRepository;

    public RelatorioController(EmprestimoRepository emprestimoRepository) {
        this.emprestimoRepository = emprestimoRepository;
    }

    @GetMapping("/relatorios/emprestimos")
    public String emprestimos(@RequestParam(name = "colaborador", required = false, defaultValue = "") String colaborador,
                              @RequestParam(name = "equipamento", required = false, defaultValue = "") String equipamento,
                              @RequestParam(name = "status", required = false) StatusEmprestimo status,
                              Model model) {

        String nomeColaborador = colaborador.trim();
        String nomeEquipamento = equipamento.trim();

        // Filtro do tipo E: cada campo preenchido reduz o resultado
        Specification<Emprestimo> filtro = (root, query, cb) -> {
            List<Predicate> condicoes = new ArrayList<>();
            if (!nomeColaborador.isEmpty()) {
                condicoes.add(cb.like(cb.lower(root.get("colaborador").get("nome")),
                        "%" + nomeColaborador.toLowerCase() + "%"));
            }
            if (!nomeEquipamento.isEmpty()) {
                condicoes.add(cb.like(cb.lower(root.get("epi").get("nome")),
                        "%" + nomeEquipamento.toLowerCase() + "%"));
            }
            if (status != null) {
                condicoes.add(cb.equal(root.get("status"), status));
            }
            return cb.and(condicoes.toArray(new Predicate[0]));
        };

        model.addAttribute("emprestimos",
                emprestimoRepository.findAll(filtro, Sort.by(Sort.Direction.DESC, "dataEmprestimo")));
        model.addAttribute("colaborador", nomeColaborador);
        model.addAttribute("equipamento", nomeEquipamento);
        model.addAttribute("status", status);
        model.addAttribute("statusOpcoes", Arrays.asList(StatusEmprestimo.values()));
        return "relatorios/emprestimos";
    }
}

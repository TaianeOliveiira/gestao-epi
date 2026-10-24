package br.com.epi.controller;

import br.com.epi.model.Colaborador;
import br.com.epi.model.Emprestimo;
import br.com.epi.model.EmprestimoForm;
import br.com.epi.model.Epi;
import br.com.epi.model.StatusEmprestimo;
import br.com.epi.repository.ColaboradorRepository;
import br.com.epi.repository.EmprestimoRepository;
import br.com.epi.repository.EpiRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class EmprestimoController {

    private final EmprestimoRepository emprestimoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final EpiRepository epiRepository;

    public EmprestimoController(EmprestimoRepository emprestimoRepository,
                                ColaboradorRepository colaboradorRepository,
                                EpiRepository epiRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.epiRepository = epiRepository;
    }

    private static LocalDateTime agora() {
        return LocalDateTime.now().withSecond(0).withNano(0);
    }

    @GetMapping("/emprestimos")
    public String listar(Model model) {
        model.addAttribute("emprestimos", emprestimoRepository.findAllByOrderByIdDesc());
        model.addAttribute("qtdEmprestado", emprestimoRepository.countByStatus(StatusEmprestimo.EMPRESTADO));
        model.addAttribute("qtdFornecido", emprestimoRepository.countByStatus(StatusEmprestimo.FORNECIDO));
        model.addAttribute("qtdDevolvido", emprestimoRepository.countByStatus(StatusEmprestimo.DEVOLVIDO));
        return "emprestimos/lista";
    }

    @GetMapping("/emprestimos/novo")
    public String novo(Model model) {
        EmprestimoForm form = new EmprestimoForm();
        form.setDataEmprestimo(agora());
        model.addAttribute("form", form);
        preparar(model, false);
        return "emprestimos/form";
    }

    @PostMapping("/emprestimos")
    @Transactional
    public String salvar(@ModelAttribute("form") @Valid EmprestimoForm form,
                         BindingResult result, Model model, RedirectAttributes redirect) {

        Colaborador colaborador = form.getColaboradorId() == null ? null
                : colaboradorRepository.findById(form.getColaboradorId()).orElse(null);
        Epi epi = form.getEpiId() == null ? null
                : epiRepository.findById(form.getEpiId()).orElse(null);

        if (colaborador == null) {
            result.rejectValue("colaboradorId", "obrigatorio", "Selecione o colaborador");
        }
        if (epi == null) {
            result.rejectValue("epiId", "obrigatorio", "Selecione o equipamento");
        }
        StatusEmprestimo status = form.getStatus();
        if (status != null && status != StatusEmprestimo.EMPRESTADO && status != StatusEmprestimo.FORNECIDO) {
            result.rejectValue("status", "invalido", "No cadastro só é possível Emprestado ou Fornecido");
        }
        if (status == StatusEmprestimo.EMPRESTADO) {
            LocalDateTime prevista = form.getDataPrevistaDevolucao();
            if (prevista == null) {
                result.rejectValue("dataPrevistaDevolucao", "obrigatorio", "Informe a data prevista da devolução");
            } else if (!prevista.isAfter(LocalDateTime.now())) {
                result.rejectValue("dataPrevistaDevolucao", "invalida",
                        "A data prevista deve ser posterior à data e hora atuais");
            } else if (form.getDataEmprestimo() != null && !prevista.isAfter(form.getDataEmprestimo())) {
                result.rejectValue("dataPrevistaDevolucao", "invalida",
                        "A data prevista deve ser posterior à data do empréstimo");
            }
        }
        if (epi != null && form.getQuantidade() != null) {
            if (epi.getValidadeCa() != null && epi.getValidadeCa().isBefore(LocalDate.now())) {
                result.rejectValue("epiId", "vencido", "O CA deste equipamento está vencido");
            } else if (epi.getQuantidadeEstoque() < form.getQuantidade()) {
                result.rejectValue("epiId", "estoque",
                        "Estoque insuficiente (disponível: " + epi.getQuantidadeEstoque() + ")");
            }
        }

        if (result.hasErrors()) {
            preparar(model, false);
            model.addAttribute("erro", "Falha ao cadastrar: corrija os campos destacados.");
            return "emprestimos/form";
        }

        Emprestimo e = new Emprestimo();
        e.setColaborador(colaborador);
        e.setEpi(epi);
        e.setQuantidade(form.getQuantidade());
        e.setDataEmprestimo(form.getDataEmprestimo());
        e.setDataPrevistaDevolucao(status == StatusEmprestimo.EMPRESTADO ? form.getDataPrevistaDevolucao() : null);
        e.setStatus(status);
        e.setObservacao(vazioParaNulo(form.getObservacao()));

        epi.setQuantidadeEstoque(epi.getQuantidadeEstoque() - form.getQuantidade());
        epiRepository.save(epi);
        emprestimoRepository.save(e);

        redirect.addFlashAttribute("sucesso", "Empréstimo cadastrado com sucesso!");
        return "redirect:/emprestimos/novo";
    }

    @GetMapping("/emprestimos/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Emprestimo> encontrado = emprestimoRepository.findById(id);
        if (encontrado.isEmpty()) {
            redirect.addFlashAttribute("erro", "Empréstimo não encontrado.");
            return "redirect:/emprestimos";
        }
        Emprestimo e = encontrado.get();
        EmprestimoForm form = new EmprestimoForm();
        form.setStatus(e.getStatus());
        form.setDataDevolucao(e.getDataDevolucao());
        form.setObservacao(e.getObservacao());
        form.setObservacaoDevolucao(e.getObservacaoDevolucao());
        model.addAttribute("form", form);
        model.addAttribute("emprestimo", e);
        preparar(model, true);
        return "emprestimos/form";
    }

    @PostMapping("/emprestimos/{id}")
    @Transactional
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("form") EmprestimoForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {

        Optional<Emprestimo> encontrado = emprestimoRepository.findById(id);
        if (encontrado.isEmpty()) {
            redirect.addFlashAttribute("erro", "Empréstimo não encontrado.");
            return "redirect:/emprestimos";
        }
        Emprestimo e = encontrado.get();
        StatusEmprestimo novo = form.getStatus();

        if (novo == null) {
            result.rejectValue("status", "obrigatorio", "Selecione o status");
        } else if (novo.isDevolucao()) {
            if (form.getDataDevolucao() == null) {
                result.rejectValue("dataDevolucao", "obrigatorio", "Informe a data da devolução");
            } else if (form.getDataDevolucao().isBefore(e.getDataEmprestimo())) {
                result.rejectValue("dataDevolucao", "invalida",
                        "A data da devolução não pode ser anterior à data do empréstimo");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("emprestimo", e);
            preparar(model, true);
            model.addAttribute("erro", "Falha ao atualizar: corrija os campos destacados.");
            return "emprestimos/form";
        }

        int antes = e.getStatus() == StatusEmprestimo.DEVOLVIDO ? 1 : 0;
        int depois = novo == StatusEmprestimo.DEVOLVIDO ? 1 : 0;
        int ajuste = (depois - antes) * e.getQuantidade();
        if (ajuste != 0) {
            Epi epi = e.getEpi();
            epi.setQuantidadeEstoque(epi.getQuantidadeEstoque() + ajuste);
            epiRepository.save(epi);
        }

        e.setStatus(novo);
        e.setObservacao(vazioParaNulo(form.getObservacao()));
        if (novo.isDevolucao()) {
            e.setDataDevolucao(form.getDataDevolucao());
            e.setObservacaoDevolucao(vazioParaNulo(form.getObservacaoDevolucao()));
        } else {
            e.setDataDevolucao(null);
            e.setObservacaoDevolucao(null);
        }
        emprestimoRepository.save(e);

        redirect.addFlashAttribute("sucesso", "Status do empréstimo atualizado com sucesso!");
        return "redirect:/emprestimos";
    }

    private void preparar(Model model, boolean edicao) {
        List<StatusEmprestimo> opcoes = edicao
                ? Arrays.asList(StatusEmprestimo.values())
                : List.of(StatusEmprestimo.EMPRESTADO, StatusEmprestimo.FORNECIDO);
        model.addAttribute("edicao", edicao);
        model.addAttribute("statusOpcoes", opcoes);
        if (!edicao) {
            model.addAttribute("colaboradores", colaboradorRepository.findByAtivoTrueOrderByNomeAsc());
            model.addAttribute("equipamentos", epiRepository.findByAtivoTrueOrderByNomeAsc());
        }
    }

    private static String vazioParaNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}

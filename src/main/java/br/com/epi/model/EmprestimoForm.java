package br.com.epi.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class EmprestimoForm {

    private Long colaboradorId;
    private Long epiId;

    @NotNull(message = "Informe a quantidade")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantidade = 1;

    @NotNull(message = "Informe a data do empréstimo")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataEmprestimo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataPrevistaDevolucao;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataDevolucao;

    @NotNull(message = "Selecione o status")
    private StatusEmprestimo status = StatusEmprestimo.EMPRESTADO;

    @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
    private String observacao;

    @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
    private String observacaoDevolucao;

    public Long getColaboradorId() { return colaboradorId; }
    public void setColaboradorId(Long colaboradorId) { this.colaboradorId = colaboradorId; }

    public Long getEpiId() { return epiId; }
    public void setEpiId(Long epiId) { this.epiId = epiId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public LocalDateTime getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDateTime dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public LocalDateTime getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public void setDataPrevistaDevolucao(LocalDateTime d) { this.dataPrevistaDevolucao = d; }

    public LocalDateTime getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDateTime dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public StatusEmprestimo getStatus() { return status; }
    public void setStatus(StatusEmprestimo status) { this.status = status; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public String getObservacaoDevolucao() { return observacaoDevolucao; }
    public void setObservacaoDevolucao(String observacaoDevolucao) { this.observacaoDevolucao = observacaoDevolucao; }
}

package br.com.epi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "epi")
public class Epi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 80, message = "A categoria deve ter no máximo 80 caracteres")
    @Column(nullable = false, length = 80)
    private String categoria;

    @NotBlank(message = "O CA é obrigatório")
    @Size(max = 30, message = "O CA deve ter no máximo 30 caracteres")
    @Column(nullable = false, unique = true, length = 30)
    private String ca;

    @NotNull(message = "A validade do CA é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "validade_ca", nullable = false)
    private LocalDate validadeCa;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque = 0;

    @Column(nullable = false)
    private boolean ativo = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getCa() { return ca; }
    public void setCa(String ca) { this.ca = ca; }

    public LocalDate getValidadeCa() { return validadeCa; }
    public void setValidadeCa(LocalDate validadeCa) { this.validadeCa = validadeCa; }

    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}

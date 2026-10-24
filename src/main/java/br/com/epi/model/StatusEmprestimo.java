package br.com.epi.model;

public enum StatusEmprestimo {

    EMPRESTADO("Emprestado", "text-bg-primary"),
    FORNECIDO("Fornecido", "text-bg-info"),
    DEVOLVIDO("Devolvido", "text-bg-success"),
    DANIFICADO("Danificado", "text-bg-warning"),
    PERDIDO("Perdido", "text-bg-danger");

    private final String rotulo;
    private final String cor;

    StatusEmprestimo(String rotulo, String cor) {
        this.rotulo = rotulo;
        this.cor = cor;
    }

    public String getRotulo() { return rotulo; }

    public String getCor() { return cor; }

    public boolean isDevolucao() {
        return this == DEVOLVIDO || this == DANIFICADO || this == PERDIDO;
    }
}

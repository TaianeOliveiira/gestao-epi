package br.com.epi.model;

public enum Perfil {

    ADMINISTRADOR("Administrador"),
    TECNICO("Técnico de Segurança do Trabalho");

    private final String rotulo;

    Perfil(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() { return rotulo; }

    public String getPapel() { return "ROLE_" + name(); }
}

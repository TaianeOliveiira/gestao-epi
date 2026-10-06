-- Script de criação do banco (DER do sistema de gestão de EPIs)
-- MySQL 8+
CREATE DATABASE IF NOT EXISTS gestao_epi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestao_epi;

CREATE TABLE usuario (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    login         VARCHAR(50)  NOT NULL UNIQUE,
    senha_hash    VARCHAR(255) NOT NULL,
    perfil        ENUM('ADMIN','TECNICO_SEGURANCA') NOT NULL DEFAULT 'TECNICO_SEGURANCA',
    ativo         BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE colaborador (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(120) NOT NULL,
    matricula      VARCHAR(20)  NOT NULL UNIQUE,
    cpf            VARCHAR(14)  NOT NULL UNIQUE,
    cargo          VARCHAR(80)  NOT NULL,
    setor          VARCHAR(80)  NOT NULL,
    email          VARCHAR(120),
    data_admissao  DATE,
    ativo          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE epi (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome           VARCHAR(120) NOT NULL,
    categoria      VARCHAR(60)  NOT NULL,
    ca             VARCHAR(20)  NOT NULL UNIQUE COMMENT 'Certificado de Aprovação',
    validade_ca    DATE,
    quantidade_estoque INT NOT NULL DEFAULT 0,
    ativo          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE emprestimo (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    colaborador_id           BIGINT NOT NULL,
    epi_id                   BIGINT NOT NULL,
    usuario_id               BIGINT NOT NULL COMMENT 'Quem registrou o empréstimo',
    quantidade               INT NOT NULL DEFAULT 1,
    data_emprestimo          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_prevista_devolucao  DATE,
    data_devolucao           DATETIME,
    status                   ENUM('EM_USO','DEVOLVIDO','ATRASADO') NOT NULL DEFAULT 'EM_USO',
    observacao               VARCHAR(255),
    CONSTRAINT fk_emp_colaborador FOREIGN KEY (colaborador_id) REFERENCES colaborador(id),
    CONSTRAINT fk_emp_epi         FOREIGN KEY (epi_id)         REFERENCES epi(id),
    CONSTRAINT fk_emp_usuario     FOREIGN KEY (usuario_id)     REFERENCES usuario(id)
);

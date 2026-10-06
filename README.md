# Gestão de EPIs

Sistema de gerenciamento de Equipamentos de Proteção Individual (EPIs) para uma indústria têxtil.
Esta entrega contém a **tela de colaboradores** com CRUD completo (Etapa 2) e a documentação técnica (Etapa 1, em [`docs/`](docs/)).

## Funcionalidades da tela de colaboradores

- Cadastro de colaboradores, com mensagem de sucesso/falha (Bootstrap) e permanência na tela de cadastro
- Listagem com pesquisa por nome
- Edição em tela semelhante à de cadastro, já preenchida
- Exclusão com modal de confirmação
- Dados persistidos em banco MySQL

## Tecnologias

Java 17 · Spring Boot 3.3 · Spring Data JPA · Thymeleaf · Bootstrap 5 · MySQL 8 · Maven · Docker

## Como executar

### Opção 1 — Docker (recomendada, não precisa instalar Java nem MySQL)

```bash
docker compose up --build
```

Acesse: http://localhost:8080

### Opção 2 — Local com MySQL

Pré-requisitos: Java 17+, Maven 3.9+ e MySQL 8 rodando.

1. Ajuste usuário e senha, se necessário, em `src/main/resources/application.properties` (padrão: `root` / `root`). O banco `gestao_epi` é criado automaticamente.
2. Execute:

```bash
mvn spring-boot:run
```

Acesse: http://localhost:8080

### Opção 3 — Teste rápido sem MySQL (banco em memória H2)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

> Os dados do H2 são apagados ao encerrar a aplicação.

## Estrutura do projeto

```
gestao-epi/
├── docs/                    # DER, casos de uso, requisitos, wireframes, schema.sql
├── src/main/java/br/com/epi/
│   ├── model/Colaborador.java
│   ├── repository/ColaboradorRepository.java
│   └── controller/ColaboradorController.java
├── src/main/resources/
│   ├── templates/colaboradores/{lista,form}.html
│   └── application*.properties
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Rotas

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/colaboradores?nome=` | Lista e pesquisa por nome |
| GET | `/colaboradores/novo` | Formulário de cadastro |
| POST | `/colaboradores` | Salva novo colaborador |
| GET | `/colaboradores/{id}/editar` | Formulário de edição |
| POST | `/colaboradores/{id}` | Atualiza colaborador |
| POST | `/colaboradores/{id}/excluir` | Exclui colaborador |

## Docker: como foi integrado

- O `Dockerfile` usa *multi-stage build*: a primeira etapa compila o projeto com Maven e a segunda copia apenas o `.jar` para uma imagem leve com JRE 17.
- O `docker-compose.yml` sobe dois serviços: `db` (MySQL 8, com volume para persistir os dados) e `app` (a aplicação), que só inicia depois que o banco estiver saudável. A conexão é configurada pelas variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`.

## Versionamento (GitHub)

```bash
git init
git add .
git commit -m "Tela de colaboradores com CRUD"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/gestao-epi.git
git push -u origin main
```

## Equipe

- Integrante 1
- Integrante 2
- Integrante 3

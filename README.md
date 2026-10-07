# Gestão de EPIs

Este projeto é um sistema de gerenciamento de Equipamentos de Proteção Individual (EPIs) pensado para uma indústria do setor têxtil. A ideia é que o técnico de segurança do trabalho consiga cadastrar colaboradores, equipamentos e usuários do sistema, e controlar o empréstimo dos EPIs para cada colaborador, de forma simples e intuitiva.

O trabalho foi dividido em duas etapas. A primeira é a documentação técnica, que está na pasta docs e reúne o modelo do banco de dados (DER), o diagrama de casos de uso, a lista de requisitos funcionais e não funcionais e os wireframes das telas. A segunda é a tela de colaboradores, que já está programada e funcionando, com cadastro, listagem, pesquisa por nome, edição e exclusão.

## Tecnologias utilizadas

O sistema foi feito em Java 17 com o framework Spring Boot. As telas são páginas HTML geradas com Thymeleaf e estilizadas com Bootstrap 5. Os dados ficam guardados em um banco MySQL, acessado pelo Spring Data JPA. O projeto é gerenciado pelo Maven e também pode ser executado dentro de containers com Docker.

## O que a tela de colaboradores faz

No cadastro, o usuário preenche os dados do colaborador (nome, matrícula, CPF, cargo, setor, e-mail, data de admissão e se está ativo) e clica em cadastrar. O sistema mostra uma mensagem verde de sucesso ou uma mensagem vermelha de falha, por exemplo quando algum campo obrigatório não foi preenchido ou quando a matrícula ou o CPF já existem. Depois de cadastrar, o sistema continua na própria tela de cadastro, pronto para o próximo colaborador.

Na tela de listagem aparecem todos os colaboradores cadastrados, em ordem alfabética. No topo existe um campo de pesquisa: basta digitar parte do nome e clicar em pesquisar para filtrar a lista. O botão limpar volta a mostrar todos.

Cada linha da lista tem os botões editar e excluir. O botão editar abre uma tela parecida com a de cadastro, só que com os campos já preenchidos com as informações do colaborador, para que o usuário altere o que precisar e salve. O botão excluir abre uma janela de confirmação perguntando se o usuário realmente quer apagar aquele colaborador, e só depois de confirmar o registro é removido.

## Como executar o projeto

Existem três formas de rodar o sistema. Escolha a que for mais fácil no seu computador.

### Usando Docker

Esta é a forma mais simples, porque não precisa instalar Java nem MySQL. Basta ter o Docker instalado e aberto. Dentro da pasta do projeto, onde está o arquivo docker-compose.yml, execute:

```
docker compose up --build
```

Na primeira vez o Docker baixa as imagens e compila o projeto, então pode demorar alguns minutos. Quando terminar, abra o navegador em http://localhost:8080 e o sistema estará funcionando.

### Usando Java, Maven e MySQL

Para esta opção é preciso ter o Java 17 ou mais novo, o Maven e um servidor MySQL 8 rodando no computador. O sistema usa por padrão o usuário root com a senha root. Se o seu MySQL tiver outro usuário ou senha, altere o arquivo src/main/resources/application.properties. O banco de dados chamado gestao_epi é criado automaticamente na primeira execução, e as tabelas também. Com tudo pronto, rode na pasta do projeto:

```
mvn spring-boot:run
```

Depois acesse http://localhost:8080 no navegador.

### Testando rápido, sem MySQL

Se você só quer ver o sistema funcionando e não tem o MySQL, pode usar o banco em memória H2, que já vem incluído no projeto. Rode:

```
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

e acesse http://localhost:8080. Nesse modo os dados são apagados toda vez que o programa é encerrado, então ele serve apenas para testes. Esta forma também funciona no GitHub Codespaces. Quando a porta 8080 aparecer na aba Ports, clique para abrir no navegador.

## Como o projeto está organizado

Na raiz ficam o pom.xml, que lista as dependências do Maven, o Dockerfile e o docker-compose.yml, que servem para executar o projeto com Docker, e este README. A pasta docs guarda a documentação da primeira etapa, com as imagens do DER, dos casos de uso e dos wireframes, o texto com os requisitos e o arquivo schema.sql com o script de criação das tabelas.

O código fica em src/main/java/br/com/epi e segue a divisão em camadas. A classe Colaborador, na pasta model, representa a tabela de colaboradores. A interface ColaboradorRepository, na pasta repository, faz a comunicação com o banco de dados. A classe ColaboradorController, na pasta controller, recebe os pedidos do navegador (cadastrar, listar, pesquisar, editar e excluir) e decide qual tela mostrar. As telas ficam em src/main/resources/templates/colaboradores, nos arquivos lista.html e form.html. O form.html é usado tanto para cadastrar quanto para editar. As configurações do banco de dados estão em src/main/resources/application.properties.

## Endereços do sistema

A listagem e a pesquisa ficam em /colaboradores, e a pesquisa por nome usa o parâmetro nome, por exemplo /colaboradores?nome=ana. O formulário de cadastro fica em /colaboradores/novo. A edição de um colaborador abre em /colaboradores/ID/editar, onde ID é o número do colaborador. A exclusão é feita por um envio de formulário para /colaboradores/ID/excluir, que só acontece depois da confirmação no modal.

## Sobre o uso do Docker

O arquivo Dockerfile descreve como montar a imagem da aplicação em duas etapas. Na primeira, uma imagem com Maven e Java compila o código e gera o arquivo jar. Na segunda, apenas o jar é copiado para uma imagem mais leve, que só tem o Java necessário para executar. Isso deixa a imagem final menor.

O arquivo docker-compose.yml sobe dois containers juntos: o do banco MySQL, com um volume para os dados não se perderem quando o container é parado, e o da aplicação. A aplicação só começa depois que o banco avisa que está pronto, e recebe o endereço, o usuário e a senha do banco por variáveis de ambiente, que são DB_URL, DB_USER e DB_PASSWORD.

## Versionamento com GitHub

Para enviar o projeto ao GitHub, crie um repositório vazio e, dentro da pasta do projeto, execute os comandos abaixo, trocando SEU_USUARIO pelo seu nome de usuário.

```
git init
git add .
git commit -m "Tela de colaboradores com CRUD"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/gestao-epi.git
git push -u origin main
```

## Equipe

Taiane Silva de Oliveira
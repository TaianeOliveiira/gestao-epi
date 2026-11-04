# Gestão de EPIs

Este projeto é um sistema de gerenciamento de Equipamentos de Proteção Individual (EPIs) pensado para uma empresa do setor têxtil e de construção. A ideia é que o técnico de segurança do trabalho consiga cadastrar colaboradores, equipamentos e usuários do sistema, controlar o empréstimo dos EPIs para cada colaborador e consultar o histórico em um relatório, de forma simples e intuitiva.

O trabalho foi feito em etapas. A primeira é a documentação técnica, que está na pasta docs e reúne o modelo do banco de dados (DER), o diagrama de casos de uso, a lista de requisitos funcionais e não funcionais e os wireframes das telas. A segunda é o sistema programado e funcionando, com a tela base, o cadastro de colaboradores, o cadastro de equipamentos, o controle de empréstimo de EPI, o relatório de empréstimos e o controle de usuários com login e logout.

## Tecnologias utilizadas

O sistema foi feito em Java 17 com o framework Spring Boot. As telas são páginas HTML geradas com Thymeleaf e estilizadas com Bootstrap 5. Os dados ficam guardados em um banco MySQL, acessado pelo Spring Data JPA. O login e a proteção das páginas usam o Spring Security, e as senhas são guardadas criptografadas com o algoritmo BCrypt. O projeto é gerenciado pelo Maven e também pode ser executado dentro de containers com Docker.

## Acesso ao sistema

Ao abrir o sistema aparece a tela de login. Na primeira execução, quando o banco ainda está vazio, o sistema cria sozinho dois usuários para que seja possível entrar. O primeiro é o administrador, com o login admin e a senha admin123. O segundo é o técnico de segurança, com o login tecnico e a senha tecnico123. Depois de entrar como administrador, é possível cadastrar outros usuários e trocar essas senhas pela tela de usuários.

Existem dois perfis. O administrador tem acesso a todas as telas, inclusive a de usuários. O técnico de segurança usa todas as telas do sistema, menos a de usuários, e se tentar abrir esse endereço vê uma tela de acesso negado. Para sair do sistema basta clicar no botão Sair, no final do menu lateral.

## Tela base

Todas as telas usam a mesma base, que tem um menu lateral para navegação, dividido em cadastros, ações, relatórios e configurações. No topo aparecem o nome e o perfil do usuário que está logado, junto com a sua foto. O item Usuários do menu só aparece para o administrador. As mensagens de sucesso e de falha aparecem em destaque, em verde ou em vermelho, no alto de cada tela.

## Colaboradores

No cadastro, o usuário preenche os dados do colaborador (nome, matrícula, CPF, cargo, setor, e-mail, data de admissão e se está ativo) e clica em cadastrar. O sistema mostra uma mensagem de sucesso ou de falha, por exemplo quando algum campo obrigatório não foi preenchido ou quando a matrícula ou o CPF já existem. Depois de cadastrar, o sistema continua na própria tela de cadastro, pronto para o próximo colaborador.

Na tela de listagem aparecem todos os colaboradores, em ordem alfabética, com um campo de pesquisa por nome. Cada linha tem os botões editar e excluir. O botão editar abre uma tela parecida com a de cadastro, já preenchida, para alterar o que for preciso. O botão excluir abre uma janela de confirmação, e o registro só é removido depois que o usuário confirma. Um colaborador que já tem empréstimos registrados não pode ser excluído, e o sistema avisa isso.

## Equipamentos

O cadastro de equipamentos funciona do mesmo jeito que o de colaboradores. O usuário informa os dados do EPI, como nome, número do CA, validade do CA e quantidade em estoque, e o sistema mostra a mensagem de sucesso ou de falha e permanece na tela de cadastro. A listagem tem pesquisa por nome, e cada linha tem os botões editar e excluir, com janela de confirmação antes de apagar.

## Controle de EPI

A tela de controle de EPI registra o empréstimo de um equipamento a um colaborador. O empréstimo guarda o colaborador e o equipamento por chaves estrangeiras, a data de entrega, a data prevista para devolução e a data efetiva da devolução. O status do empréstimo é escolhido em um campo de seleção e pode ser Emprestado, Fornecido, Devolvido, Danificado ou Perdido. Emprestado é o EPI entregue temporariamente, que será devolvido depois. Fornecido é o EPI entregue de forma definitiva, como um uniforme. Devolvido indica que o colaborador já retornou o item. Danificado indica que o item voltou com defeito. Perdido indica que o colaborador não conseguiu devolver por extravio.

No momento de cadastrar, só aparecem as opções Emprestado e Fornecido. As opções Devolvido, Danificado e Perdido só aparecem na edição. Quando o status Emprestado é escolhido, a data prevista para devolução é obrigatória e precisa ser posterior à data e hora atuais. Os campos de data da devolução e de observação na devolução só aparecem quando o status for Devolvido, Danificado ou Perdido.

O sistema também confere o estoque e a validade do CA na hora do empréstimo. Cada empréstimo retira uma unidade do estoque, e a unidade só volta para o estoque quando o status passa para Devolvido. Na edição do empréstimo, o usuário atualiza o status, mas os campos do colaborador, do equipamento, da data do empréstimo e da data prevista de devolução ficam bloqueados e não podem ser alterados.

## Relatório de empréstimos

A tela de relatórios lista todos os empréstimos, como uma tabela histórica, mostrando o que foi emprestado, o que está em uso e o que já foi devolvido. É possível filtrar pelo nome do colaborador, pelo nome do equipamento e pelo status. Os filtros funcionam em conjunto, ou seja, o sistema aplica todos os que foram preenchidos ao mesmo tempo. Por exemplo, ao pesquisar o nome de um colaborador junto com o status Emprestado, o relatório mostra quais equipamentos aquele colaborador pegou e ainda não devolveu.

## Usuários

A tela de usuários só pode ser usada pelo administrador. Nela é possível cadastrar um usuário informando nome, login, perfil, senha e confirmação da senha, e escolher se o usuário está ativo. A listagem tem pesquisa por nome, e cada usuário pode ser editado ou excluído. Na edição, a senha pode ficar em branco para manter a senha atual. O login não pode se repetir e a senha precisa ter pelo menos seis caracteres. Um usuário inativo não consegue entrar no sistema. Por segurança, o administrador não consegue excluir a si mesmo e o sistema não deixa o último administrador ativo ser removido ou desativado. As senhas ficam guardadas apenas como um código criptografado (BCrypt), então ninguém consegue ver a senha de outra pessoa, só trocar por uma nova.

## Como executar o projeto

Existem três formas de rodar o sistema. Escolha a que for mais fácil no seu computador.

### Usando Docker

Esta é a forma mais simples, porque não precisa instalar Java nem MySQL. Basta ter o Docker instalado e aberto. Dentro da pasta do projeto, onde está o arquivo docker-compose.yml, execute:

```
docker compose up --build
```

Na primeira vez o Docker baixa as imagens e compila o projeto, então pode demorar alguns minutos. Quando terminar, abra o navegador em http://localhost:8080 e entre com o login admin e a senha admin123.

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

e acesse http://localhost:8080. Nesse modo os dados são apagados toda vez que o programa é encerrado, então ele serve apenas para testes. Como o banco começa vazio a cada execução, os usuários admin e tecnico são criados de novo toda vez. Esta forma também funciona no GitHub Codespaces. Quando a porta 8080 aparecer na aba Portas, clique no globo para abrir no navegador.

## Como o projeto está organizado

Na raiz ficam o pom.xml, que lista as dependências do Maven, o Dockerfile e o docker-compose.yml, que servem para executar o projeto com Docker, e este README. A pasta docs guarda a documentação da primeira etapa, com as imagens do DER, dos casos de uso e dos wireframes, o texto com os requisitos e o arquivo schema.sql com o script de criação das tabelas.

O código fica em src/main/java/br/com/epi e segue a divisão em camadas. A pasta model guarda as classes que representam as tabelas e os formulários, como Colaborador, Epi, Emprestimo, StatusEmprestimo, Usuario e Perfil. A pasta repository guarda as interfaces que fazem a comunicação com o banco de dados. A pasta controller guarda as classes que recebem os pedidos do navegador e decidem qual tela mostrar, uma para cada área do sistema. A pasta security guarda a configuração do login, da criptografia das senhas e das permissões de cada perfil. A pasta config guarda a criação dos usuários iniciais.

As telas ficam em src/main/resources/templates, em uma pasta para cada área, e a tela base, usada por todas as outras, fica na subpasta fragments. As telas de login e de acesso negado ficam soltas nessa mesma pasta. As configurações do banco de dados estão em src/main/resources/application.properties.

## Endereços do sistema

A tela de login fica em /login. A listagem e a pesquisa de colaboradores ficam em /colaboradores, e a pesquisa por nome usa o parâmetro nome, por exemplo /colaboradores?nome=ana. O formulário de cadastro fica em /colaboradores/novo, a edição abre em /colaboradores/ID/editar, onde ID é o número do colaborador, e a exclusão é feita por um envio de formulário para /colaboradores/ID/excluir, depois da confirmação no modal. Os equipamentos seguem o mesmo padrão, a partir de /equipamentos. O controle de EPI fica em /emprestimos, o relatório em /relatorios/emprestimos e o controle de usuários, só para o administrador, em /usuarios.

## Sobre o uso do Docker

O arquivo Dockerfile descreve como montar a imagem da aplicação em duas etapas. Na primeira, uma imagem com Maven e Java compila o código e gera o arquivo jar. Na segunda, apenas o jar é copiado para uma imagem mais leve, que só tem o Java necessário para executar. Isso deixa a imagem final menor.

O arquivo docker-compose.yml sobe dois containers juntos: o do banco MySQL, com um volume para os dados não se perderem quando o container é parado, e o da aplicação. A aplicação só começa depois que o banco avisa que está pronto, e recebe o endereço, o usuário e a senha do banco por variáveis de ambiente, que são DB_URL, DB_USER e DB_PASSWORD.

## Versionamento com GitHub

O código do projeto está guardado em um repositório no GitHub, em https://github.com/TaianeOliveiira/gestao-epi. Para enviar novas alterações, execute os comandos abaixo dentro da pasta do projeto.

```
git add .
git commit -m "Descrição da alteração"
git push
```

## Equipe

Taiane Silva de Oliveira.
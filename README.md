# Gestão de EPIs

Este projeto é um sistema de gerenciamento de Equipamentos de Proteção Individual (EPIs) pensado para uma empresa de construção civil. A ideia é que o técnico de segurança do trabalho consiga cadastrar colaboradores e equipamentos e controlar o empréstimo dos EPIs para cada colaborador, de forma simples e intuitiva, resolvendo o problema apontado na auditoria interna da empresa, em que os colaboradores esquecem o EPI em casa ou não retiram o equipamento no departamento de Saúde e Segurança do Trabalho.

O trabalho foi dividido em etapas. A primeira é a documentação técnica, que está na pasta docs e reúne o modelo do banco de dados (DER), o diagrama de casos de uso, a lista de requisitos funcionais e não funcionais e os wireframes das telas. As etapas seguintes são as telas do sistema, que já estão programadas e funcionando: a tela base, o gerenciamento de colaboradores, o gerenciamento de equipamentos, o controle de empréstimo de EPI com a atualização do status e o relatório de empréstimos com filtros.

## Tecnologias utilizadas

O sistema foi feito em Java 17 com o framework Spring Boot. As telas são páginas HTML geradas com Thymeleaf e estilizadas com Bootstrap 5. Os dados ficam guardados em um banco MySQL, acessado pelo Spring Data JPA. O projeto é gerenciado pelo Maven e também pode ser executado dentro de containers com Docker.

## Tela base

Todas as páginas do sistema usam o mesmo molde, que fica no arquivo templates/fragments/base.html. Ele tem um menu lateral com os itens Colaboradores, Equipamentos, Controle de EPI e Relatórios, e mostra no canto superior o nome e a foto do usuário logado. Como o login ainda não faz parte desta etapa, essas informações são exibidas de forma fixa na tela.

## Colaboradores

No cadastro, o usuário preenche os dados do colaborador (nome, matrícula, CPF, cargo, setor, e-mail, data de admissão e se está ativo) e clica em cadastrar. O sistema mostra uma mensagem verde de sucesso ou uma mensagem vermelha de falha, por exemplo quando algum campo obrigatório não foi preenchido ou quando a matrícula ou o CPF já existem. Depois de cadastrar, o sistema continua na própria tela de cadastro, pronto para o próximo colaborador.

Na tela de listagem aparecem todos os colaboradores cadastrados, em ordem alfabética. No topo existe um campo de pesquisa: basta digitar parte do nome e clicar em pesquisar para filtrar a lista. O botão limpar volta a mostrar todos. Cada linha da lista tem os botões editar e excluir. O botão editar abre uma tela parecida com a de cadastro, só que com os campos já preenchidos, para que o usuário altere o que precisar e salve. O botão excluir abre uma janela de confirmação perguntando se o usuário realmente quer remover aquele colaborador, e só depois de confirmar o registro é apagado.

## Equipamentos

O cadastro de equipamentos guarda o nome, a categoria, o número do CA (Certificado de Aprovação), a validade do CA, a quantidade em estoque e se o equipamento está ativo. O CA não pode se repetir, e o sistema avisa quando já existe um equipamento com o mesmo número. Assim como em colaboradores, o sistema mostra mensagem de sucesso ou falha, permanece na tela de cadastro depois de salvar, tem pesquisa por nome na listagem, uma tela de edição parecida com a de cadastro e uma janela de confirmação antes de excluir.

## Controle de EPI

Esta é a tela de empréstimo. Para registrar um empréstimo, o usuário escolhe o colaborador e o equipamento em listas, informa a quantidade, a data do empréstimo e a data prevista para a devolução, escolhe o status e pode escrever a condição do equipamento no momento da entrega. Os colaboradores e os equipamentos são ligados ao empréstimo por chaves estrangeiras, então cada empréstimo sempre pertence a um colaborador e a um equipamento que existem no banco.

O sistema aceita cinco status. Emprestado indica que o EPI foi entregue temporariamente e será devolvido depois. Fornecido é usado quando o EPI é entregue de forma definitiva e não precisa ser devolvido. Devolvido indica que o colaborador já retornou o equipamento. Danificado é usado quando o EPI volta com defeito e passa por manutenção. Perdido é usado quando o colaborador não consegue devolver o equipamento por extravio.

No cadastro, o campo de status mostra apenas Emprestado e Fornecido. Os outros três só aparecem na edição. Quando o status é Emprestado, a data prevista da devolução é obrigatória e precisa ser posterior à data e hora atuais. O sistema também recusa o empréstimo de um equipamento com o CA vencido ou com estoque insuficiente. Ao cadastrar, a quantidade emprestada é descontada do estoque do equipamento.

Na listagem de empréstimos aparecem a contagem de itens emprestados, fornecidos e devolvidos e uma tabela com todos os empréstimos, que funciona como um histórico. Em cada linha há o botão para atualizar o status. A tela de atualização é parecida com a de cadastro, mas o colaborador, o equipamento, a quantidade, a data do empréstimo e a data prevista aparecem bloqueados e não podem ser alterados, e essa regra também é garantida no servidor. Os campos data da devolução e observação na devolução só aparecem quando o status escolhido é Devolvido, Danificado ou Perdido. Quando o status muda para Devolvido, a quantidade volta para o estoque do equipamento.

## Relatório de empréstimos

A tela de relatório fica no menu Relatórios e permite pesquisar os empréstimos pelo nome do colaborador, pelo nome do equipamento e pelo status. Os filtros funcionam em conjunto, ou seja, quanto mais campos forem preenchidos, mais o resultado é reduzido. Por exemplo, ao pesquisar pelo nome de um colaborador junto com o status Emprestado, o sistema mostra somente os equipamentos que ele pegou e ainda não devolveu. A busca por nome não diferencia letras maiúsculas de minúsculas e aceita apenas uma parte do nome. Quando nenhum empréstimo corresponde à busca, o sistema mostra a mensagem de que não foram encontrados dados correspondentes. Cada linha do resultado tem um botão para editar o empréstimo.

## Como executar o projeto

Existem três formas de rodar o sistema. Escolha a que for mais fácil no seu computador.

### Usando Docker

Esta é a forma mais simples, porque não precisa instalar Java nem MySQL. Basta ter o Docker instalado e aberto. Dentro da pasta do projeto, onde está o arquivo docker-compose.yml, execute o comando abaixo.

    docker compose up --build

Na primeira vez o Docker baixa as imagens e compila o projeto, então pode demorar alguns minutos. Quando terminar, abra o navegador em http://localhost:8080 e o sistema estará funcionando.

### Usando Java, Maven e MySQL

Para esta opção é preciso ter o Java 17 ou mais novo, o Maven e um servidor MySQL 8 rodando no computador. O sistema usa por padrão o usuário root com a senha root. Se o seu MySQL tiver outro usuário ou senha, altere o arquivo src/main/resources/application.properties. O banco de dados chamado gestao_epi é criado automaticamente na primeira execução, e as tabelas também. Com tudo pronto, rode na pasta do projeto o comando abaixo.

    mvn spring-boot:run

Depois acesse http://localhost:8080 no navegador.

### Testando rápido, sem MySQL

Se você só quer ver o sistema funcionando e não tem o MySQL, pode usar o banco em memória H2, que já vem incluído no projeto. Rode o comando abaixo.

    mvn spring-boot:run -Dspring-boot.run.profiles=h2

Depois acesse http://localhost:8080. Nesse modo os dados são apagados toda vez que o programa é encerrado, então ele serve apenas para testes. Esta forma também funciona no GitHub Codespaces. Quando a porta 8080 aparecer na aba Portas, clique no ícone de globo para abrir no navegador.

## Exemplo de uso

Para ver o sistema funcionando do começo ao fim, cadastre primeiro um colaborador, por exemplo Carlos Eduardo Lima com a matrícula 1001, e um equipamento, por exemplo Luva de Proteção com o CA 38001, a validade em uma data futura e estoque 10. Depois entre em Controle de EPI, clique em novo empréstimo, escolha o colaborador e o equipamento, informe a quantidade 2 e uma data prevista de devolução futura, e cadastre. O estoque da luva passa a ser 8. Em seguida, abra o botão de atualizar status, troque para Devolvido, informe a data da devolução e salve, e o estoque volta para 10. Por fim, abra o menu Relatórios, pesquise pelo nome do colaborador junto com o status Devolvido e confira que o empréstimo aparece no resultado.

## Como o projeto está organizado

Na raiz ficam o pom.xml, que lista as dependências do Maven, o Dockerfile e o docker-compose.yml, que servem para executar o projeto com Docker, e este README. A pasta docs guarda a documentação da primeira etapa, com as imagens do DER, dos casos de uso e dos wireframes, o texto com os requisitos e o arquivo schema.sql com o script de criação das tabelas.

O código fica em src/main/java/br/com/epi e segue a divisão em camadas. Na pasta model estão as classes que representam as tabelas, que são Colaborador, Epi e Emprestimo, além do StatusEmprestimo, que lista os cinco status, e do EmprestimoForm, que recebe os dados do formulário de empréstimo. Na pasta repository ficam as interfaces que fazem a comunicação com o banco de dados. Na pasta controller ficam as classes que recebem os pedidos do navegador e decidem qual tela mostrar, que são o ColaboradorController, o EpiController, o EmprestimoController e o RelatorioController. As telas ficam em src/main/resources/templates, separadas em uma pasta para cada assunto, e a tela base fica na pasta fragments. As configurações do banco de dados estão em src/main/resources/application.properties.

## Endereços do sistema

A listagem e a pesquisa de colaboradores ficam em /colaboradores, o cadastro em /colaboradores/novo e a edição em /colaboradores/ID/editar, onde ID é o número do registro. Os equipamentos seguem o mesmo padrão, em /equipamentos, /equipamentos/novo e /equipamentos/ID/editar. O controle de EPI fica em /emprestimos, o cadastro de empréstimo em /emprestimos/novo e a atualização do status em /emprestimos/ID/editar. O relatório fica em /relatorios/emprestimos e recebe os filtros colaborador, equipamento e status no próprio endereço, por exemplo /relatorios/emprestimos?colaborador=carlos&status=EMPRESTADO. A exclusão é feita por um envio de formulário, que só acontece depois da confirmação na janela de aviso.

## Sobre o uso do Docker

O arquivo Dockerfile descreve como montar a imagem da aplicação em duas etapas. Na primeira, uma imagem com Maven e Java compila o código e gera o arquivo jar. Na segunda, apenas o jar é copiado para uma imagem mais leve, que só tem o Java necessário para executar. Isso deixa a imagem final menor.

O arquivo docker-compose.yml sobe dois containers juntos: o do banco MySQL, com um volume para os dados não se perderem quando o container é parado, e o da aplicação. A aplicação só começa depois que o banco avisa que está pronto, e recebe o endereço, o usuário e a senha do banco por variáveis de ambiente, que são DB_URL, DB_USER e DB_PASSWORD.

## Versionamento com GitHub

O projeto está versionado no GitHub. Para enviar novas alterações, basta rodar os comandos abaixo dentro da pasta do projeto, trocando a mensagem do commit pelo que foi alterado.

    git add .
    git commit -m "Descrição da alteração"
    git push

## Equipe

Taiane Silva de Oliveira.

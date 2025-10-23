# Vaadin CRUD

Aplicação web para cadastro e gestão de produtos, com um dashboard para visualização de preço médio e o total de produtos cadastrados. Tecnologias principais: Java 21, Spring Boot 3.5.6, Vaadin e PostgreSQL.

## 🧱 Pré-requisitos
- **Java 21**
- **Maven 3.9+**
- **PostgreSQL 17.4**
- IDE Recomendada: **Eclipse**
- SGDB recomendado: **PgAdmin4**
- Terminal recomendado: **Git bash**

## ⚙️ Configuração do projeto
### 1. Configurar o banco de dados
- Crie o banco de dados no PostgreSQL com nome: `partnercrud`
  - Caso tenha instalado o PgAdmin 4:
    - Clique com o botão direito em "databases" -> create -> database... 

No arquivo application.properties, configure suas credenciais para acesso ao banco:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/partnercrud
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```
### 2. Instalação do projeto
Faça o download do projeto .zip ou clone o repositório: 
```
git clone https://github.com/Anchel17/crud-partner-pro.git
```
Na pasta raíz do projeto, abra o terminal de sua preferência e execute o comando: 
```
mvn clean install
```
## Execução do projeto
- Na pasta raíz do projeto, execute no terminal o comando: 
```
mvn spring-boot:run
```
- A aplicação estará aberta em: http://localhost:8080
- A tela principal exibe a lista de produtos inicialmente vazia e o botão de criar novo produto. Ao cadastrar o produto, são oferecidas as opções de editar e excluir o produto.
- O projeto conta com uma dashboard que pode ser acessada pelo menu lateral, nela contém a quantidade de produtos e o valor médio dos preços dos produtos cadastrados.

## Arquitetura da aplicação
O projeto segue uma arquitetura **cliente desacoplado**, com divisão entre:
- **Camada de apresentação (Vaadin)** — interface web SPA-like
- **Camada de negócio (Spring Boot)** — regras de negócio e controle de fluxo
- **Camada de persistência (JPA + PostgreSQL)** — abstração do banco relacional

---

## 🧩 Decisões técnicas e de arquitetura
### 🔹 **Spring Boot + Spring Data JPA**
O Spring Boot provê um servidor embutido(Tomcat) e configuração mínima, permitindo concentrar o desenvolvimento da lógica de negócio, 
ao invés de configurações manuais de servidor e dependências.
O Spring Data JPA complementa a produtividade do Spring Boot, oferecendo uma abstração de persistência robusta e segura, com operações
CRUD completas.

### 🔹 **Vaadin**
O Vaadin foi escolhido pela proposta de unificar o desenvolvimento front-end e back-end em Java, reduzindo a complexidade de configurar 
e rodar projeto Full Stack com 2 frameworks com linguagens distintas. Assim, o Vaadin gera e renderiza os componentes da interface 
no servidor, simplificando a integração.

### 🔹 **RestTemplate**
Apesar do RestTemplate estar em estado de deprecated, foi a solução ideal para fazer a comunicação do módulo Vaadin -> Spring Boot via HTTP,
oferecendo uma implementação mais simples, direta e de fácil leitura quando comparada ao WebClient, já que o projeto não precisa de operações
assíncronas complexas ou alto volume de requisições simultâneas.

### 🔹 **Camada DTO**
Isola a entidade JPA do tráfego HTTP, facilita a aplicação de validações e conversões de tipos antes da persistência, além de evitar 
vazamento de detalhes de implementação, fortalecendo a segurança.

### 🔹 **Validação de Dados no DTO**
O uso de anotações com o Jakarta Validation `@NotBlank`, `@NotNull`, `@DecimalMin` e `@PastOrPresent` garantem integridade dos dados 
antes mesmo de chegar à camada de persistência.

### 🔹 **BigDecimal para valores monetários**
BigDecimal foi escolhido por oferecer melhor precisão e controle de arredondamento do que `double` ou `float`, garantindo
cálculos consistentes.

### 🔹 **Design e UX**
- Uso de modal `Dialog` para formulários para manter contexto e fluidez, sem adicionar a complexidade de adicionar uma nova página
somente para o formulário, tornando a interação mais ágil e com melhor usabilidade;
- Campos obrigatórios e validações visuais são destacados diretamente nos componentes Vaadin, melhorando a usabilidade e prevenção de erros;
- Validações de campos e máscaras de entrada evitam erros comuns de digitação.

---
## 🚀 Melhorias Futuras
- **Adoção do WebClient**: 
Substituir o RestTemplate pelo WebClient para suporte reativo e gerenciamento de futuras chamadas assíncronas entre o Vaadin e a API.

- **Camada de Autenticação e Autorização**:
Implementar autenticação com token JWT e Spring Security, com controle de acesso por perfis (ex: administrador, usuário comum).

- **Paginação no Grid**:
Adicionar paginação ao Grid de produtos para melhor desempenho em grandes volumes de dados.

- **Melhoria no Dashboard**:
Expandir o dashboard para incluir novos gráficos (ex: quantidade por categoria, quantidade de produtos cadastrados ao longo da semana) 
e métricas adicionais.

- **Testes Automatizados**:
Inclusão de testes unitários com JUnit e de integração para garantir a estabilidade e segurança da aplicação.
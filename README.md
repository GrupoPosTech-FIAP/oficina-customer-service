# Oficina Customer Service

Microsserviço de **Gestão e Cadastro de Clientes** da oficina mecânica, responsável pelo ciclo completo de dados dos clientes (Pessoas Físicas e Jurídicas), incluindo cadastro com validação algorítmica de documentos (**CPF** e **CNPJ** via Módulo 11), consulta unificada por documento, listagem de clientes ativos, alteração cadastral e exclusão lógica (**Soft Delete**).

Desenvolvido como parte do ecossistema de microsserviços da pós-graduação **SOAT (Software Architecture) da FIAP**.

---

## 🛠️ Stack Tecnológica

| Componente | Tecnologia | Versão / Detalhes |
| :--- | :--- | :--- |
| **Linguagem** | Java | 25 (compatível com 21+) |
| **Framework Base** | Spring Boot | 4.0.9 (Spring WebMvc, Spring Data JPA, Spring Validation) |
| **Banco de Dados Principal** | PostgreSQL | 15 (Alpine via Docker) |
| **Banco de Dados em Memória** | H2 Database | Perfil `local` para testes e desenvolvimento rápido |
| **ORM / Persistência** | Hibernate ORM | 7.x |
| **Conteinerização** | Docker & Docker Compose | Multi-stage build com `eclipse-temurin:25` |
| **Orquestração de Contêineres** | Kubernetes & Kustomize | Suporte a Deployment, Service e HPA no cluster EKS |
| **Registry de Imagens** | Amazon ECR | Repositório `oficina-customer-service` |
| **Deploy & CI/CD** | GitHub Actions | Workflows de integração e entrega contínua |
| **Monitoramento & Saúde** | Spring Boot Actuator | `/actuator/health` (liveness, readiness e probes de inicialização) |
| **Documentação da API** | SpringDoc OpenAPI | 3.0.3 (Swagger UI v3) |
| **Produtividade** | Lombok | 1.18.x |
| **Testes Unitários / Integração** | JUnit 5 & Mockito | Execução via Maven Surefire |

---

## 🏛️ Arquitetura

O microsserviço adota rigorosamente os princípios da **Clean Architecture (Arquitetura Limpa)** e **Ports & Adapters (Arquitetura Hexagonal)**, isolando o núcleo de domínio das dependências de frameworks, banco de dados ou interfaces de entrada:

```
[ Adapters: Web / Persistence / Config ] ──> [ Application: Use Cases / Gateways ] ──> [ Domain: Entidades / Value Objects ]
```

* **`domain/cliente/`**: Entidades de negócio e Value Objects imutáveis.
  * [`Cliente`](src/main/java/com/mecanica/oficina_customer_service/domain/cliente/Cliente.java): Entidade raiz com invariantes de criação, reconstituição, controle temporal (`dataCadastro`, `dataAtualizacao`) e status ativo.
  * [`Documento`](src/main/java/com/mecanica/oficina_customer_service/domain/cliente/Documento.java): *Sealed interface* que define o contrato documental e o método fábrica inteligente `parse()`.
  * [`Cpf`](src/main/java/com/mecanica/oficina_customer_service/domain/cliente/Cpf.java): Value Object para validação de CPF (11 dígitos, cálculo dos dois dígitos verificadores via Módulo 11 e rejeição de sequências repetidas).
  * [`Cnpj`](src/main/java/com/mecanica/oficina_customer_service/domain/cliente/Cnpj.java): Value Object para validação de CNPJ (14 dígitos, cálculo dos dígitos verificadores com pesos e rejeição de sequências repetidas).
  * [`Email`](src/main/java/com/mecanica/oficina_customer_service/domain/cliente/Email.java): Value Object com validação estrita via Regex RFC e normalização para caixa baixa.
  * [`Telefone`](src/main/java/com/mecanica/oficina_customer_service/domain/cliente/Telefone.java): Value Object com sanitização numérica e verificação de extensão mínima (DDD + número).
* **`application/cliente/`**: Casos de uso e portas de saída independentes de tecnologia externa.
  * Casos de Uso: [`CadastrarClienteUseCase`](src/main/java/com/mecanica/oficina_customer_service/application/cliente/usecase/CadastrarClienteUseCase.java), [`ConsultarClienteUseCase`](src/main/java/com/mecanica/oficina_customer_service/application/cliente/usecase/ConsultarClienteUseCase.java), [`ListarClientesUseCase`](src/main/java/com/mecanica/oficina_customer_service/application/cliente/usecase/ListarClientesUseCase.java), [`AlterarClienteUseCase`](src/main/java/com/mecanica/oficina_customer_service/application/cliente/usecase/AlterarClienteUseCase.java) e [`DeletarClienteUseCase`](src/main/java/com/mecanica/oficina_customer_service/application/cliente/usecase/DeletarClienteUseCase.java).
  * Porta de Saída: [`ClienteGateway`](src/main/java/com/mecanica/oficina_customer_service/application/cliente/gateway/ClienteGateway.java).
* **`adapters/`**:
  * `web/`: Controlador REST ([`ClienteController`](src/main/java/com/mecanica/oficina_customer_service/adapters/web/ClienteController.java)), Presenter ([`ClientePresenter`](src/main/java/com/mecanica/oficina_customer_service/adapters/web/presenter/ClientePresenter.java)), DTOs de entrada/saída ([`CadastrarClienteRequest`](src/main/java/com/mecanica/oficina_customer_service/adapters/web/dto/request/CadastrarClienteRequest.java), [`AlterarClienteRequest`](src/main/java/com/mecanica/oficina_customer_service/adapters/web/dto/request/AlterarClienteRequest.java), [`ConsultarClienteResponse`](src/main/java/com/mecanica/oficina_customer_service/adapters/web/dto/response/ConsultarClienteResponse.java)) e manipulador de exceções ([`GlobalExceptionHandler`](src/main/java/com/mecanica/oficina_customer_service/adapters/web/GlobalExceptionHandler.java)).
  * `persistence/`: Entidade ORM ([`ClienteJpaEntity`](src/main/java/com/mecanica/oficina_customer_service/adapters/persistence/ClienteJpaEntity.java)), repositório Spring Data ([`ClienteSpringDataRepository`](src/main/java/com/mecanica/oficina_customer_service/adapters/persistence/repository/ClienteSpringDataRepository.java)) e adaptador da porta ([`ClienteJpaGateway`](src/main/java/com/mecanica/oficina_customer_service/adapters/persistence/ClienteJpaGateway.java)).
  * `config/`: Injeção de dependências dos casos de uso ([`ApplicationConfig`](src/main/java/com/mecanica/oficina_customer_service/adapters/config/ApplicationConfig.java)).

### 🌐 Integração no Ecossistema de Microsserviços e AWS

O microsserviço opera como o provedor central de dados cadastrais dos clientes dentro do ecossistema distribuído da oficina na AWS:

```mermaid
graph TD
    Client["📱 Cliente / Frontend / Serviços Internos"] --> AGW["🚪 AWS API Gateway (oficina-auth-gateway)\n- POST /auth (autenticação JWT)\n- ANY /cliente/* (JWT Authorizer)"]
    
    subgraph EKS["🖥️ Cluster EKS (eks-oficina-terraform)"]
        SVC["🌐 Service: oficina-customer\n(Type: LoadBalancer / Porta 8083)"]
        HPA["📈 HPA\n(1-5 réplicas | 70% CPU / 80% RAM)"]
        POD["📦 Pods: oficina-customer-service\n- Java 25 / Spring Boot 4.0.9\n- Probes: /actuator/health"]
        SVC --> POD
        HPA -.->|Controla| POD
    end

    subgraph Dados["🗄️ Camada de Dados (oficina-infra-database)"]
        RDS[("🐘 AWS RDS PostgreSQL\nDatabase: customer_db")]
    end

    subgraph Consumidores["⚙️ Microsserviços Dependentes"]
        OS_SVC["📋 oficina-os-service\n(Valida cliente na abertura de OS)"]
    end

    AGW -->|HTTP Proxy| SVC
    POD -->|JDBC Connection| RDS
    OS_SVC -.->|Consulta GET /cliente/{doc}| AGW

    style POD fill:#22c55e,color:#fff
    style AGW fill:#3b82f6,color:#fff
    style RDS fill:#6366f1,color:#fff
    style OS_SVC fill:#0ea5e9,color:#fff
```

#### Papel dos Repositórios Integrados:
* **`oficina-infra-cluster`**: Provisiona a VPC, Subnets, o cluster EKS (`eks-oficina-terraform`) e o repositório ECR (`oficina-customer-service`).
* **`oficina-infra-database`**: Provisiona a instância PostgreSQL no RDS; os dados de clientes ficam isolados na base lógica `customer_db` (*Database-per-Service*).
* **`oficina-auth-gateway`**: Ponto único de entrada com autorização JWT para as rotas `/cliente/*`.
* **`oficina-customer-service`**: Repositório deste microsserviço, com ciclo de vida independente de build e deploy.

---

## 🗄️ Banco de Dados e Justificativa da Escolha

O banco de dados relacional **PostgreSQL 15** foi selecionado como banco principal do serviço pelos seguintes motivos técnicos:

1. **Garantias ACID e Unicidade Cadastral Concorrente**:
   * O documento (CPF ou CNPJ) possui constraint única (`unique = true, nullable = false`) na coluna `documento`. O PostgreSQL garante atomicidade e consistência estrita, impedindo duplicações mesmo em cenários de requisições concorrentes simultâneas.
2. **Exclusão Lógica (Soft Delete) e Preservação Histórica**:
   * Clientes desativados mantêm o registro persistido (`ativo = false`), garantindo a integridade referencial histórica de Ordens de Serviço antigas e auditoria de serviços prestados.
3. **Identificadores Universais (`UUID`)**:
   * O identificador interno `id` é gerado via `UUID`, permitindo correlação desacoplada e segura em eventos assíncronos e integrações sem expor contadores sequenciais de negócio.
4. **Rastreabilidade Temporal**:
   * Armazenamento explícito de `dataCadastro` e `dataAtualizacao` com suporte nativo a tipos temporais do padrão ISO-8601 (`timestamp without time zone`).
5. **Banco H2 Auxiliar (In-Memory)**:
   * Disponibilizado sob o perfil `local` para acelerar o ciclo de desenvolvimento e viabilizar a execução imediata de testes automatizados e suítes de CI sem dependência de containers de banco.

---

## 📋 Endpoints da API (REST)

A API disponibiliza operações RESTful padronizadas sob o prefixo `/cliente`:

| Método | Endpoint | Descrição | Status de Sucesso | Possíveis Erros |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/cliente` | Cadastra um novo cliente (PF ou PJ) | `201 Created` | `400 Bad Request`, `409 Conflict` |
| `GET` | `/cliente` | Lista todos os clientes ativos | `200 OK` | `500 Internal Error` |
| `GET` | `/cliente/{documento}` | Consulta um cliente ativo pelo CPF ou CNPJ | `200 OK` | `404 Not Found` |
| `PUT` | `/cliente/{documento}` | Atualiza nome, e-mail e telefone do cliente | `204 No Content` | `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/cliente/{documento}` | Inativa o cliente via exclusão lógica (*Soft Delete*) | `204 No Content` | `500 Internal Error` |

---

### Exemplos de Requisições e Respostas

#### 1. Cadastrar Novo Cliente (`POST /cliente`)

> [!NOTE]
> O campo `documento` aceita tanto CPF (11 dígitos) quanto CNPJ (14 dígitos), com ou sem pontuação. O domínio realiza a sanitização e a validação algorítmica.

```http
POST /cliente HTTP/1.1
Host: localhost:8083
Content-Type: application/json

{
  "nome": "João da Silva",
  "documento": "52998224725",
  "email": "joao.silva@exemplo.com",
  "telefone": "11987654321"
}
```

**Resposta (`201 Created`):**
```json
{
  "id": "e2f1b4c3-8a9d-4e72-b5e1-6d7c8e9f0a1b",
  "nome": "João da Silva",
  "documento": "52998224725",
  "email": "joao.silva@exemplo.com",
  "telefone": "11987654321"
}
```

---

#### 2. Consultar Cliente por Documento (`GET /cliente/{documento}`)

```http
GET /cliente/52998224725 HTTP/1.1
Host: localhost:8083
```

**Resposta (`200 OK`):**
```json
{
  "id": "e2f1b4c3-8a9d-4e72-b5e1-6d7c8e9f0a1b",
  "nome": "João da Silva",
  "documento": "52998224725",
  "email": "joao.silva@exemplo.com",
  "telefone": "11987654321"
}
```

---

#### 3. Alterar Cliente (`PUT /cliente/{documento}`)

```http
PUT /cliente/52998224725 HTTP/1.1
Host: localhost:8083
Content-Type: application/json

{
  "nome": "João da Silva Sauro",
  "email": "joao.sauro@exemplo.com",
  "telefone": "11999998888"
}
```

**Resposta (`204 No Content`)**

---

#### 4. Desativar Cliente / Soft Delete (`DELETE /cliente/{documento}`)

```http
DELETE /cliente/52998224725 HTTP/1.1
Host: localhost:8083
```

**Resposta (`204 No Content`)**

---

#### 5. Erro de Validação de Campos (`400 Bad Request`)

Quando um campo obrigatório está ausente ou malformatado, o [`GlobalExceptionHandler`](src/main/java/com/mecanica/oficina_customer_service/adapters/web/GlobalExceptionHandler.java) retorna o payload estruturado:

```json
{
  "mensagem": "Erro de validação nos campos",
  "erros": [
    {
      "campo": "email",
      "mensagem": "E-mail inválido"
    },
    {
      "campo": "nome",
      "mensagem": "O nome é obrigatório"
    }
  ]
}
```

---

## 🛡️ Regras de Domínio e Validações Estruturais

O microsserviço garante integridade através de **Value Objects** que validam seus dados na instanciação:

* **`Documento` (Interface Selada)**:
  * Suporta apenas [`Cpf`](src/main/java/com/mecanica/oficina_customer_service/domain/cliente/Cpf.java) e [`Cnpj`](src/main/java/com/mecanica/oficina_customer_service/domain/cliente/Cnpj.java).
  * O método `Documento.parse(valor)` remove caracteres não numéricos e direciona automaticamente pelo tamanho (`11` dígitos para CPF e `14` dígitos para CNPJ).
  * Rejeita valores com todos os dígitos iguais (ex: `111.111.111-11`, `000.000.000-00`).
  * Valida ambos os dígitos verificadores através do cálculo de módulo 11.
* **`Email`**:
  * Validação formal através da expressão regular `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$`.
  * Conversão automática para minúsculas e remoção de espaços em branco nas extremidades.
* **`Telefone`**:
  * Sanitização eliminando caracteres não numéricos.
  * Validação de tamanho mínimo de 10 dígitos (DDD de 2 dígitos + número de 8 ou 9 dígitos).
* **Unicidade de Documento**:
  * Tentativas de cadastrar documento já existente disparam `IllegalStateException`, mapeada para `409 Conflict`.

---

## 🚀 Como Executar

### Pré-requisitos
* **Docker e Docker Compose** instalados.
* **Java 25** (ou JDK 21+) caso deseje executar localmente fora do container.

---

### Opção 1: Via Docker Compose (Recomendado com PostgreSQL)

Para subir o banco de dados PostgreSQL e a aplicação simultaneamente em containers:

```bash
docker compose up --build
```

Para encerrar e remover os containers:
```bash
docker compose down -v
```

---

### Opção 2: Banco em Memória H2 (Desenvolvimento Rápido sem Docker)

Executa a aplicação utilizando o perfil `local` com o banco H2 em memória:

```powershell
# Windows
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"

# Linux / macOS
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

---

### Opção 3: PostgreSQL via Docker + Spring Boot Local na IDE

Caso prefira rodar o PostgreSQL em container e executar o código diretamente na sua IDE ou terminal:

```powershell
# 1. Sobe apenas o banco de dados PostgreSQL
docker compose up -d customer-db

# 2. Executa a aplicação conectando ao banco local (porta 5432)
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=default"
```

---

### Endpoints e Interfaces Úteis

* **Swagger UI (Documentação Interativa OpenAPI):** [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
* **OpenAPI Docs (JSON):** [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs)
* **H2 Web Console (ativo no perfil `local`):** [http://localhost:8083/h2-console](http://localhost:8083/h2-console)
  * *JDBC URL:* `jdbc:h2:mem:customer_db`
  * *User Name:* `sa`
  * *Password:* *(deixar em branco)*
* **Spring Boot Actuator Health:** [http://localhost:8083/actuator/health](http://localhost:8083/actuator/health)
* **Spring Boot Actuator Info:** [http://localhost:8083/actuator/info](http://localhost:8083/actuator/info)

---

### Variáveis de Ambiente Suportadas

| Variável | Descrição | Valor Padrão |
| :--- | :--- | :--- |
| `SERVER_PORT` | Porta HTTP da aplicação | `8083` |
| `APP_PORT` | Porta mapeada no host via Docker Compose | `8084` (ou `8083`) |
| `POSTGRES_PORT` | Porta do PostgreSQL no host | `5432` |
| `POSTGRES_DB` | Nome da base de dados no PostgreSQL | `customer_db` |
| `POSTGRES_USER` | Usuário do PostgreSQL | `postgres` |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL | `123` |
| `SPRING_DATASOURCE_URL` | URL JDBC de conexão | `jdbc:postgresql://localhost:5432/customer_db` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo do Spring Boot | `local` (H2) ou `default` (PostgreSQL) |
| `JWT_SECRET` | Chave secreta de validação JWT compartilhada | *(Chave Base64 padrão do ecossistema)* |

---

## ☁️ Deploy e Infraestrutura Kubernetes (K8s & AWS)

Dentro do ecossistema da oficina na AWS, o serviço é preparado para execução escalável em cluster **Amazon EKS**:

1. **Deployment & Probes de Saúde**:
   * Pods baseados na imagem publicada no Amazon ECR (`oficina-customer-service`).
   * Probes de `/actuator/health` configuradas para `readinessProbe`, `livenessProbe` e `startupProbe`.
2. **Service & Exposição**:
   * Exposição interna via `ClusterIP` ou externa via `LoadBalancer` para consumo roteado através do **AWS API Gateway** (`oficina-auth-gateway`).
3. **Escalabilidade Automática (HPA)**:
   * Autoscaling horizontal de 1 a 5 réplicas com base na utilização de 70% de CPU e 80% de memória.
4. **Banco RDS Dedicado (`customer_db`)**:
   * No provisionamento com AWS RDS compartilhado, a base de dados lógica `customer_db` isola as tabelas do serviço (*Database-per-Service*).

---

## 🧪 Como Executar os Testes

O projeto utiliza o **JUnit 5** e ferramentas de teste do ecossistema Spring Boot.

### Executar Toda a Suíte de Testes:

```powershell
# Windows
.\mvnw.cmd clean test

# Linux / macOS
./mvnw clean test
```

### Executar Testes com Perfil Local:

```powershell
.\mvnw.cmd test -Dspring.profiles.active=local
```

---

## 📄 Licença

Distribuído sob a licença [MIT](LICENSE).

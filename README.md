#  Clinic API — Sistema de Gestão Médica

API REST para gestão de clínicas médicas, cobrindo todo o fluxo de atendimento: cadastro de pacientes, cadastro de profissionais, escala diária de médicos e agendamento de consultas.

Projeto desenvolvido como parte do meu portfólio como estudante de Engenharia de Software, com foco em boas práticas de backend: arquitetura em camadas, autenticação via JWT, validações, tratamento de erros e testes automatizados.

##  Funcionalidades

- **Autenticação e autorização** com JWT (perfis: `ADMIN`, `DOCTOR`, `RECEPTIONIST`)
- **Cadastro de pacientes** (CRUD completo)
- **Cadastro de profissionais** (CRUD + ativação/desativação)
- **Escala diária de profissionais** — define quem está disponível para atendimento em cada dia e em qual horário
- **Agendamento de consultas**, com validações de regra de negócio:
  - Só é possível agendar se o profissional tiver escala cadastrada para o dia
  - O horário da consulta precisa estar dentro do expediente do profissional
  - Não é possível haver duas consultas no mesmo horário para o mesmo profissional
- **Atualização de status de consulta** (agendada, confirmada, cancelada, concluída, faltou)
- **Documentação interativa** via Swagger/OpenAPI

##  Tecnologias

- Java 17
- Spring Boot 3 (Web, Data JPA, Security, Validation)
- JWT (jjwt)
- PostgreSQL (produção) / H2 (desenvolvimento local)
- Maven
- JUnit 5 + Mockito + AssertJ (testes)
- Docker & Docker Compose
- Swagger / OpenAPI

##  Arquitetura

O projeto segue uma arquitetura em camadas:

```
controller  → recebe requisições HTTP, valida entrada
service     → regras de negócio
repository  → acesso a dados (Spring Data JPA)
model       → entidades JPA
dto         → objetos de entrada/saída da API (nunca expõe entidades diretamente)
security    → autenticação JWT
exception   → tratamento centralizado de erros
```

##  Como rodar o projeto

### Opção 1 — Local com H2 (mais rápido, sem instalar nada)

Pré-requisitos: Java 17+ e Maven.

```bash
git clone <url-do-seu-repositorio>
cd clinic-api
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O console do H2 fica disponível em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/clinicdb`).

### Opção 2 — Com Docker (Postgres + API)

Pré-requisitos: Docker e Docker Compose.

```bash
docker-compose up --build
```

### Documentação da API (Swagger)

Após subir a aplicação, acesse:

```
http://localhost:8080/swagger-ui.html
```

##  Principais endpoints

| Método | Endpoint                          | Descrição                                  |
|--------|------------------------------------|---------------------------------------------|
| POST   | `/api/auth/register`              | Cria um novo usuário                        |
| POST   | `/api/auth/login`                 | Autentica e retorna token JWT               |
| POST   | `/api/patients`                   | Cadastra paciente                           |
| GET    | `/api/patients`                   | Lista pacientes                             |
| POST   | `/api/doctors`                    | Cadastra profissional                       |
| GET    | `/api/doctors?onlyActive=true`    | Lista profissionais ativos                  |
| POST   | `/api/schedules`                  | Cadastra escala de um profissional          |
| GET    | `/api/schedules/by-date?date=...` | Lista quem está escalado em uma data        |
| POST   | `/api/appointments`               | Agenda uma consulta                         |
| PATCH  | `/api/appointments/{id}/status`   | Atualiza status da consulta                 |

> Todas as rotas exceto `/api/auth/**` exigem o header `Authorization: Bearer <token>`.

### Exemplo de fluxo de uso

```bash
# 1. Criar usuário
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Admin","email":"admin@clinic.com","password":"123456","role":"ADMIN"}'

# 2. Login (retorna o token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@clinic.com","password":"123456"}'

# 3. Cadastrar profissional (usar o token retornado acima)
curl -X POST http://localhost:8080/api/doctors \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Dra. Ana Costa","crm":"12345-SP","specialty":"Clinico Geral","phone":"11999999999","email":"ana@clinic.com"}'
```

##  Testes

```bash
mvn test
```

Os testes cobrem as principais regras de negócio, como validação de CPF duplicado e verificação de disponibilidade de horário do profissional.

##  Próximos passos (roadmap)

- [ ] Paginação e filtros nos endpoints de listagem
- [ ] Notificação por e-mail ao paciente ao agendar/cancelar consulta
- [ ] Testes de integração com Testcontainers
- [ ] Deploy em ambiente cloud (Render/Railway)

## 👤 Autor

Desenvolvido por Pedro Henrique Rodrigues Pereira — estudante de Engenharia de Software.
[https://www.linkedin.com/in/pedro-henrique-rodrigues-pereira-b59a2a387/?skipRedirect=true] · [https://github.com/Pedrohenriquerodri]

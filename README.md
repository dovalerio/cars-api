# Cars API

![CI](https://github.com/dovalerio/cars-api/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-green)

API backend em Java 21 + Spring Boot 4 para autenticação JWT e operações de veículos.

## Pré-requisitos
- Java 21
- Maven
- Docker + Docker Compose
- Node.js (opcional, para executar `run-contract-test.js`)

## Variáveis de Ambiente

### Aplicação
- `SERVER_PORT`
- `SPRING_APP_NAME`

### Banco (PostgreSQL)
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

### Redis
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`
- `REDIS_TIMEOUT`

### Segurança
- `JWT_SECRET`

### Câmbio/cache
- `CURRENCY_CACHE_TTL`
- `CURRENCY_API_PRIMARY`
- `CURRENCY_API_FALLBACK`

### Logs
- `LOG_LEVEL_ROOT`
- `LOG_LEVEL_SECURITY`
- `LOG_LEVEL_HIBERNATE_SQL`

### JPA
- `JPA_DDL_AUTO`
- `JPA_OPEN_IN_VIEW`
- `JPA_SHOW_SQL`
- `JPA_FORMAT_SQL`

### MVC
- `MVC_PROBLEM_DETAILS_ENABLED`

## Arquivo .env (exemplo)
Use o arquivo `.env.example` como base:

```bash
cp .env.example .env
```

No Windows (PowerShell):

```powershell
Copy-Item .env.example .env
```

## Docker Compose
O `docker-compose.yml` sobe:
- PostgreSQL (`localhost:5432`)
- Redis (`localhost:6379`)

Comandos:

```bash
docker compose up -d
docker compose ps
```

## Scripts de Setup

### Linux/Mac
Arquivo: `setup.sh`

```bash
chmod +x setup.sh
./setup.sh
```

### Windows PowerShell
Arquivo: `setup.ps1`

```powershell
./setup.ps1
```

### Windows CMD
Arquivo: `setup.bat`

```bat
setup.bat
```

Todos os scripts fazem:
1. Configuração de variáveis de ambiente
2. Subida do Docker Compose
3. Execução da aplicação com Maven

## Como Subir o Projeto
1. Suba as dependências:

```bash
docker compose up -d
```

2. Configure as variáveis (via `.env` ou scripts `setup.*`)

3. Rode a aplicação:

```bash
mvn spring-boot:run
```

## Autenticação
Endpoint:

`POST /auth/login`

Request:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Resposta (sucesso):

```json
{
  "token": "<jwt-token>"
}
```

Use o token no header `Authorization: Bearer <jwt-token>`.

## Documentação OpenAPI
- Fonte da documentação: `src/main/resources/static/api-docs.yaml`
- YAML exposto em: `GET /api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- A UI usa o arquivo `/api-docs` como origem única

## Teste Rápido

### 1) Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 2) Acesso ao endpoint protegido
```bash
curl http://localhost:8080/veiculos \
  -H "Authorization: Bearer <jwt-token>"
```

## Observações
- A API usa JWT
- A autenticação é stateless
- Perfis de usuário disponíveis: `ADMIN` e `USER`
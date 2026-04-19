# 💳 Fintech Microservices Platform

Plataforma financeira distribuída construída com arquitetura de microserviços, deployada em produção na AWS EKS com pipeline CI/CD automatizado.

---

## 🏗️ Arquitetura

```
                        ┌─────────────────┐
                        │   API Gateway   │  :8080
                        │  (JWT + Redis)  │
                        └────────┬────────┘
                                 │
          ┌──────────────────────┼──────────────────────┐
          │                      │                      │
  ┌───────▼──────┐      ┌────────▼───────┐    ┌────────▼───────┐
  │ Auth Service │      │Account Service │    │Transfer Service│
  │    :8081     │      │    :8082       │    │    :8083       │
  └───────┬──────┘      └────────┬───────┘    └────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                         ┌───────▼────────┐
                         │  Apache Kafka  │
                         └───────┬────────┘
                                 │
                    ┌────────────▼────────────┐
                    │  Notification Service   │
                    │         :8085           │
                    └─────────────────────────┘
```

### Fluxo de eventos

```
POST /signup
  → Auth Service salva usuário
  → Publica user.created no Kafka
  → Account Service consome → cria conta automaticamente

POST /transfer
  → Gateway valida JWT + blacklist Redis
  → Transfer Service adquire Distributed Lock (Redisson)
  → Salva transferência (PENDING) → publica transfer.created
  → Account Service debita/credita → publica transfer.completed
  → Transfer Service atualiza → COMPLETED
  → Notification Service notifica remetente e destinatário
```

---

## 🧩 Microserviços

| Serviço | Porta | Responsabilidade |
|---------|-------|-----------------|
| **API Gateway** | 8080 | Roteamento, validação JWT, blacklist Redis |
| **Auth Service** | 8081 | Cadastro, login, geração de JWT |
| **Account Service** | 8082 | Criação e gestão de contas bancárias |
| **Transfer Service** | 8083 | Processamento de transferências com Distributed Lock |
| **Notification Service** | 8085 | Notificações via eventos Kafka |

---

## 🛠️ Stack Tecnológica

**Backend**
- Java 21 + Spring Boot 4.x
- Spring Security + JWT (stateless)
- Spring Cloud Gateway (WebFlux)
- MapStruct

**Mensageria**
- Apache Kafka (eventos de domínio)
- Redisson (Distributed Lock)

**Banco de Dados & Cache**
- PostgreSQL + JPA/Hibernate
- Redis (JWT blacklist + Distributed Lock)

**Infraestrutura**
- Docker + Docker Compose
- Kubernetes (Minikube local → AWS EKS produção)
- AWS ECR (registry de imagens)
- GitHub Actions (CI/CD)

**Observabilidade**
- Prometheus (coleta de métricas)
- Grafana (dashboards: Success Rate, Error Rate, Heap Memory, Requests por Endpoint)

---

## ☁️ Infraestrutura AWS

```
GitHub Actions
  ├── CI: build → test → push para ECR
  └── CD: kubectl apply → deploy no EKS

AWS ECR
  ├── fintech/auth-service
  ├── fintech/account-service
  ├── fintech/transfer-service
  ├── fintech/notification-service
  └── fintech/api-gateway

AWS EKS
  └── namespace: fintech
        ├── ConfigMap (variáveis de ambiente)
        ├── Secret (senhas e tokens)
        ├── Deployments (5 serviços + infra)
        └── Services (ClusterIP + NodePort)
```

---

## 🔐 Segurança

- **JWT stateless** — cada request é autenticado pelo token, sem sessão no servidor
- **Redis blacklist** — tokens invalidados no logout são bloqueados em tempo real
- **Distributed Lock** — Redisson garante que transferências simultâneas para a mesma conta não causam inconsistência de saldo
- **Idempotency Key** — prevenção de transferências duplicadas em ambientes distribuídos
- **Gateway centralizado** — validação JWT apenas no Gateway, serviços internos confiam no header `X-User-Id`

---

## 🚀 Rodando localmente

### Pré-requisitos
- Docker + Docker Compose
- Java 21
- Maven

### Subindo a infraestrutura

```bash
cd infra/docker
docker compose up -d
```

Isso sobe: PostgreSQL, Redis, Zookeeper e Kafka.

### Rodando os serviços

```bash
# Em terminais separados
cd authservice && mvn spring-boot:run
cd accountService && mvn spring-boot:run
cd transferService && mvn spring-boot:run
cd notificationService && mvn spring-boot:run
cd gateway && mvn spring-boot:run
```

---

## ☸️ Rodando no Kubernetes (Minikube)

```bash
# Iniciar cluster
minikube start

# Apontar Docker para o Minikube
& minikube -p minikube docker-env --shell powershell | Invoke-Expression

# Build das imagens
docker build -t auth-service:latest ./authservice
docker build -t account-service:latest ./accountService
docker build -t transfer-service:latest ./transferService
docker build -t notification-service:latest ./notificationService
docker build -t api-gateway:latest ./gateway

# Deploy
kubectl apply -f namespace.yml
kubectl apply -f configmap.yml
kubectl apply -f secret.yml
kubectl apply -f postgres-deployment.yml -n fintech
kubectl apply -f redis-deployment.yml -n fintech
kubectl apply -f kafka-deployment.yml -n fintech
kubectl apply -f auth-deployment.yml -n fintech
kubectl apply -f account-deployment.yml -n fintech
kubectl apply -f transfer-deployment.yml -n fintech
kubectl apply -f notification-deployment.yml -n fintech
kubectl apply -f gateway-deployment.yml -n fintech

# Acessar o Gateway
minikube service api-gateway -n fintech --url
```

---

## 📡 Endpoints

### Auth Service
```
POST /api/v1/signup          # Criar conta
POST /api/v1/auth/login      # Login → retorna JWT
POST /api/v1/auth/logout     # Logout → invalida token no Redis
```

### Account Service
```
GET  /api/v1/accounts/user/{userId}   # Buscar conta por usuário
GET  /api/v1/accounts/{id}/balance    # Consultar saldo
```

### Transfer Service
```
POST /api/v1/transfer                 # Realizar transferência
GET  /api/v1/transfer/{id}            # Consultar transferência
```

### Exemplo de transferência

```json
POST /api/v1/transfer
Authorization: Bearer {token}

{
  "sourceAccountId": "uuid-da-conta-origem",
  "destinationAccountId": "uuid-da-conta-destino",
  "amount": 100.00,
  "description": "Pagamento",
  "idempotencyKey": "chave-unica-da-requisicao"
}
```

---

## 📊 Observabilidade

O Grafana expõe dashboards com:

- **Success Rate** — percentual de requests bem-sucedidos
- **Error Rate (5xx)** — erros por endpoint
- **Requests por Endpoint** — volume por rota
- **Requests por Serviço** — distribuição entre microserviços
- **Heap Memory Usage** — consumo de memória por pod

---

## 🔄 CI/CD Pipeline

```yaml
on:
  push:
    branches: [main]

jobs:
  build-and-push:   # CI → build + push para ECR
  deploy:           # CD → kubectl apply no EKS
    needs: build-and-push
```

Cada imagem é tagueada com o `github.sha` do commit, garantindo rastreabilidade total e facilitando rollback.

---

## 📁 Estrutura do Projeto

```
fintech-microservices/
├── authservice/
├── accountService/
├── transferService/
├── notificationService/
├── gateway/
├── infra/
│   └── docker/
│       └── docker-compose.yml
├── k8s/
│   ├── namespace.yml
│   ├── configmap.yml
│   ├── secret.yml
│   ├── postgres-deployment.yml
│   ├── redis-deployment.yml
│   ├── kafka-deployment.yml
│   ├── auth-deployment.yml
│   ├── account-deployment.yml
│   ├── transfer-deployment.yml
│   ├── notification-deployment.yml
│   └── gateway-deployment.yml
└── .github/
    └── workflows/
        └── deploy.yml
```

---

## 👨‍💻 Autor

**Hebert Ferreira Soares**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-hebert--soares-blue)](https://linkedin.com/in/hebert-soares-859084243)
[![GitHub](https://img.shields.io/badge/GitHub-HebertFSoares-black)](https://github.com/HebertFSoares)
[![Portfolio](https://img.shields.io/badge/Portfolio-hebertsoares.vercel.app-green)](https://hebertsoares.vercel.app)

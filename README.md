# Auth Service - Microserviço de Autenticação 🔐

## 📋 Visão Geral

Microserviço de autenticação robusto com:

- ✅ Autenticação JWT (Access + Refresh tokens)
- 🔑 Autorização por roles (ADMIN, USER)
- 🌐 Login social (Google, Facebook)
- 🛡️ Segurança reforçada (BCrypt, CSRF, CORS)
- 🚀 Arquitetura limpa e escalável

## Funcionalidades Principais
### Autenticação

- Registro de novos usuários com validação de e-mail/senha
- Autenticação via JWT (access token + refresh token)
- Autorização baseada em roles (admin, user)
- Logout com revogação de tokens

### OAuth2

- Login via Google
- Login via Facebook
- Integração com provedores externos

### Segurança

- Criptografia de senhas com BCrypt
- Proteção contra ataques CSRF
- Configuração CORS
- Validação de tokens JWT
- Proteção de endpoints sensíveis

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── project/
│   │       └── authservice/
│   │           ├── application/          # Lógica de aplicação
│   │           │   ├── dto/             # Objetos de transferência de dados
│   │           │   └── service/         # Serviços de aplicação
│   │           ├── domain/              # Lógica de domínio
│   │           │   ├── model/           # Entidades
│   │           │   ├── repository/      # Interfaces de repositório
│   │           │   └── event/           # Eventos de domínio
│   │           ├── infrastructure/      # Implementações técnicas
│   │           │   ├── config/          # Configurações
│   │           │   ├── client/          # Clients HTTP
│   │           │   └── repository/      # Implementações de repositório
│   │           ├── adapter/             # Adaptadores
│   │           │   ├── in/              # Controladores
│   │           │   └── out/             # Publicação de eventos
│   │           └── AuthServiceApplication.java
│   └── resources/
│       ├── application.properties       # Configurações
│       └── db/
│           └── migration/              # Scripts Flyway
```

## Configuração
### application.properties
```
# Server
server.port=8081
server.servlet.context-path=/auth

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# JWT
auth.jwt.secret=your-secret-key
auth.jwt.expiration=86400000 # 24h
auth.jwt.refresh-expiration=2592000000 # 30d

# OAuth2 - Google
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8081/auth/oauth2/callback/google
spring.security.oauth2.client.registration.google.scope=email,profile

# OAuth2 - Facebook
spring.security.oauth2.client.registration.facebook.client-id=your-client-id
spring.security.oauth2.client.registration.facebook.client-secret=your-client-secret
spring.security.oauth2.client.registration.facebook.redirect-uri=http://localhost:8081/auth/oauth2/callback/facebook
spring.security.oauth2.client.registration.facebook.scope=email,public_profile
```

## Endpoints Principais
### Autenticação Básica

- ```POST /api/auth/register``` - Registrar novo usuário
- ```POST /api/auth/login``` - Login com e-mail/senha
- ```POST /api/auth/refresh``` - Obter novo access token
- ```POST /api/auth/logout``` - Invalidar tokens

### OAuth2

- ```GET /oauth2/authorize/{provider}``` - Iniciar fluxo OAuth2 (google/facebook)
- ```GET /oauth2/callback/{provider}``` - Callback OAuth2

### Usuários

- ```GET /api/users/me``` - Obter informações do usuário atual

## 🌐 Endpoints
### 🔐 Autenticação
### Registro de Usuário

```
POST /api/auth/register
Content-Type: application/json

{
  "email": "usuario@exemplo.com",
  "password": "Senha@123",
  "confirmPassword": "Senha@123"
}
```
### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "usuario@exemplo.com",
  "password": "Senha@123"
}
```
### Resposta:
```
{
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "eyJhbGciOi..."
}
```
### Refresh Token
```
POST /api/auth/refresh
Content-Type: application/json
Authorization: Bearer <refresh-token>

{
  "refreshToken": "eyJhbGciOi..."
}
```
### 👤 Usuário
### Obter dados do usuário atual
```
GET /api/users/me
Authorization: Bearer <access-token>
```
## 🔄 Fluxo OAuth2
### 1. Iniciar autenticação:
```
GET /oauth2/authorize/google
```
### 2. Redirecionamento após login bem-sucedido:
```
GET /oauth2/callback/google?code=<authorization-code>
```
## 📚 Documentação Completa
Acesse a documentação Swagger em:

```
http://localhost:8081/auth/swagger-ui.html
```


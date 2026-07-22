# 🏥 Sistema de Registro de Pacientes — Clinica Backend

API REST profesional para la gestión de pacientes, historial clínico y usuarios en una clínica médica. Construido con **Java 21 + Spring Boot 3**, seguridad **JWT**, base de datos **PostgreSQL** y documentación con **OpenAPI/Swagger**.

---

## 🚀 Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.3.4 |
| Seguridad | Spring Security + JWT (JJWT 0.12) |
| Persistencia | JPA / Hibernate + PostgreSQL 16 |
| Migraciones | Flyway |
| Documentación | SpringDoc OpenAPI (Swagger UI) |
| Validación | Bean Validation (Jakarta) |
| Testing | JUnit 5 + Mockito + AssertJ |
| Build | Maven |
| Contenedores | Docker + Docker Compose |
| Logging | SLF4J + Logback |

---

## 📁 Estructura del Proyecto

```
clinica-backend/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── src/
    ├── main/
    │   ├── java/com/clinica/
    │   │   ├── ClinicaApplication.java
    │   │   ├── config/          → SecurityConfig, OpenApiConfig
    │   │   ├── controller/      → AuthController, PacienteController, HistorialClinicoController
    │   │   ├── dto/             → Request/Response DTOs (auth, paciente, historial, common)
    │   │   ├── entity/          → Paciente, Usuario, Rol, HistorialClinico, BaseEntity
    │   │   ├── exception/       → GlobalExceptionHandler + excepciones de dominio
    │   │   ├── repository/      → Spring Data JPA Repositories
    │   │   ├── security/        → JwtService, JwtAuthenticationFilter, UserDetailsServiceImpl
    │   │   ├── service/         → Interfaces de servicio
    │   │   │   └── impl/        → Implementaciones
    │   │   └── util/            → RutValidator (RUT chileno)
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       ├── logback-spring.xml
    │       └── db/migration/V1__init.sql
    └── test/
        ├── java/com/clinica/
        │   ├── util/RutValidatorTest.java
        │   └── service/PacienteServiceImplTest.java
        └── resources/application-test.yml
```

---

## ▶️ Cómo Ejecutar

### Opción 1: Docker Compose (recomendado)

```bash
cd clinica-backend
docker-compose up --build
```

Levanta:
- **PostgreSQL** en `localhost:5432`
- **Backend** en `http://localhost:8080/api`

### Opción 2: Local con Maven

```bash
# 1) Levantar PostgreSQL (o usar Docker solo para la DB)
docker run -d --name clinica-pg \
  -e POSTGRES_DB=clinica_db \
  -e POSTGRES_USER=clinica \
  -e POSTGRES_PASSWORD=***REMOVED*** \
  -p 5432:5432 postgres:16-alpine

# 2) Compilar y correr
./mvnw clean package
java -jar target/clinica-backend-1.0.0.jar
```

### Variables de entorno (opcionales)

| Variable | Default | Descripción |
|----------|---------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/clinica_db` | JDBC URL |
| `DB_USER` | `clinica` | Usuario DB |
| `DB_PASSWORD` | `***REMOVED***` | Password DB |
| `JWT_SECRET` | (Base64 dev) | Clave JWT (mín 256 bits) |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | Expiración del token |
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil Spring |

> ⚠️ **Producción:** `JWT_SECRET` DEBE ser una clave Base64 fuerte (generar con `openssl rand -base64 64`).

---

## 🔑 Credenciales por Defecto

El `DataInitializer` crea al arrancar:

| Username | Password | Rol |
|----------|----------|-----|
| `admin` | `***REMOVED***` | ADMIN |

Roles creados: `ADMIN`, `MEDICO`, `RECEPCIONISTA`.

---

## 📚 Documentación Swagger

Una vez levantada la app:

- **Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api/v3/api-docs

---

## 🔐 Endpoints

### Autenticación
| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/auth/login` | público | Login → JWT |
| POST | `/auth/register` | público* | Registro de usuario |

> *En producción, `/auth/register` debe ser `ADMIN` only.

### Pacientes (todos requieren JWT)
| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| GET | `/pacientes` | ADMIN, MEDICO, RECEPCIONISTA | Listar/buscar paginado |
| GET | `/pacientes/{id}` | ADMIN, MEDICO, RECEPCIONISTA | Obtener por ID |
| POST | `/pacientes` | ADMIN, RECEPCIONISTA | Crear |
| PUT | `/pacientes/{id}` | ADMIN, RECEPCIONISTA | Actualizar |
| DELETE | `/pacientes/{id}` | ADMIN | Eliminar |

### Historial Clínico
| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| POST | `/historial` | ADMIN, MEDICO | Crear entrada clínica |
| GET | `/historial/{pacienteId}` | ADMIN, MEDICO, RECEPCIONISTA | Listar historial paginado |

### Parámetros de búsqueda (`GET /pacientes`)
- `rut` (parcial, case-insensitive)
- `nombre` (parcial, case-insensitive)
- `email` (parcial, case-insensitive)
- `page` (default 0)
- `size` (default 10)
- `sort` (default `id,asc`)

---

## 🧪 Ejemplos de Uso (cURL)

### 1) Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "***REMOVED***"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "expiresInSeconds": 86400,
    "username": "admin",
    "email": "admin@clinica.cl",
    "nombreCompleto": "Administrador del Sistema",
    "roles": ["ADMIN"]
  },
  "timestamp": "2026-07-21T23:42:02"
}
```

### 2) Crear paciente
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "rut": "12.345.678-5",
    "nombreCompleto": "María González",
    "fechaNacimiento": "1985-03-15",
    "sexo": "FEMENINO",
    "direccion": "Av. Las Flores 123, Santiago",
    "telefono": "+56987654321",
    "email": "maria.gonzalez@email.cl",
    "historialMedicoBasico": "Sin alergias conocidas. Hipertensión controlada."
  }'
```

### 3) Buscar pacientes
```bash
curl -X GET "http://localhost:8080/api/pacientes?nombre=Maria&page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

### 4) Crear entrada de historial clínico
```bash
curl -X POST http://localhost:8080/api/historial \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "fecha": "2026-07-21",
    "motivoConsulta": "Control de presión arterial",
    "diagnostico": "Hipertensión arterial controlada",
    "tratamiento": "Mantener tratamiento con Losartán 50mg diario",
    "observaciones": "Paciente refiere buena adherencia al tratamiento"
  }'
```

### 5) Obtener historial de un paciente
```bash
curl -X GET "http://localhost:8080/api/historial/1?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🗃️ Modelo Relacional

```
roles (id, nombre, descripcion, created_at, updated_at)
   ↑ N
   │
   M
usuarios (id, username, email, password, nombre_completo, activo, ...)
   ↑ 1
   │
   N
historial_clinico (id, paciente_id, medico_id, fecha, motivo, ...)
                          │ FK
                          ↓
                       pacientes (id, rut UNIQUE, nombre_completo, ...)
```

**Tabla intermedia `usuario_roles`** para la relación N:M entre usuarios y roles.

**Índices:**
- `pacientes(rut)` UNIQUE, `pacientes(email)`, `pacientes(nombre_completo)`
- `historial_clinico(paciente_id)`, `historial_clinico(fecha)`
- `usuario_roles(usuario_id)`, `usuario_roles(rol_id)`

---

## ✅ Buenas Prácticas Aplicadas

### SOLID
- **S** (Single Responsibility): cada clase tiene una única razón para cambiar (controllers HTTP, services lógica, repositories persistencia).
- **O** (Open/Closed): las implementaciones se extienden sin modificar contratos.
- **L** (Liskov Substitution): `CustomUserDetails` es un `UserDetails` válido.
- **I** (Interface Segregation): interfaces de servicio específicas (no "fat interfaces").
- **D** (Dependency Inversion): inyección por constructor (Lombok `@RequiredArgsConstructor`).

### Seguridad
- ✅ **BCrypt** (factor 12) para contraseñas
- ✅ **JWT** stateless con HS256
- ✅ **@PreAuthorize** para control de acceso a nivel método
- ✅ **CORS** configurable
- ✅ **CSRF** deshabilitado (apropiado para API stateless)
- ✅ **Spring Security** filter chain explícita
- ✅ Validación de RUT con algoritmo oficial chileno

### Arquitectura
- ✅ **MVC + Clean Architecture** (controller → service → repository → entity)
- ✅ **DTOs** para desacoplar API de entidad de dominio
- ✅ **Manejo global de excepciones** con respuestas consistentes
- ✅ **Auditoría automática** (`@CreatedDate` / `@LastModifiedDate`)
- ✅ **Paginación** en listados grandes
- ✅ **Flyway** para migraciones reproducibles
- ✅ **Multi-stage Dockerfile** con usuario no-root
- ✅ **Profiles** dev/prod separados

### Testing
- ✅ Tests unitarios con JUnit 5 + Mockito
- ✅ Tests parametrizados para validador de RUT
- ✅ Cobertura de casos felices + casos de error

### Producción
- ✅ `open-in-view: false` (evita N+1 silencioso)
- ✅ HikariCP tuning (max-pool-size, timeouts)
- ✅ Logback con archivo rotativo
- ✅ Actuator con health/info
- ✅ Manejo de contraseñas nunca en logs

---

## 🧑‍💻 Conexión con Frontend (React / Angular)

### URL base
```
http://localhost:8080/api
```

### React (con axios)
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// Interceptor para inyectar JWT
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Login
export const login = (username, password) =>
  api.post('/auth/login', { username, password }).then(r => r.data.data);

// Listar pacientes
export const listPacientes = (params) =>
  api.get('/pacientes', { params }).then(r => r.data.data);
```

### Angular (HttpClient)
```typescript
// auth.service.ts
login(username: string, password: string): Observable<AuthResponse> {
  return this.http.post<ApiResponse<AuthResponse>>(
    `${environment.apiUrl}/auth/login`,
    { username, password }
  ).pipe(map(r => r.data));
}

// auth.interceptor.ts
intercept(req: HttpRequest<any>, next: HttpHandler) {
  const token = localStorage.getItem('token');
  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next.handle(req);
}
```

---

## 🛡️ Datos Sensibles (salud)

Este sistema maneja **datos clínicos sensibles** (Ley 19.628 en Chile, GDPR comparable). Recomendaciones adicionales para producción:

- 🔒 Cifrar columnas críticas en reposo (p.ej. `historial_medico_basico`) con **pgcrypto** o similar.
- 🔒 Habilitar **TLS/SSL** en todas las conexiones.
- 🔒 Implementar **audit log completo** (quién accedió a qué, cuándo).
- 🔒 Configurar **backup encriptado** automatizado.
- 🔒 Política de **retención de datos** y derecho al olvido.
- 🔒 **Rate limiting** en endpoints públicos.
- 🔒 Penetration testing periódico.

---

## 📜 Licencia

Privado / Confidencial — Clínica © 2026

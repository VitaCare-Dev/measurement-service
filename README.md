# measurement-service

Microservicio de **VitaCare** responsable del registro y consulta de mediciones de salud del paciente: glucosa, perfil lipídico y signos vitales (presión arterial, temperatura, peso).

## Tabla de contenidos

- [Arquitectura](#arquitectura)
- [Stack tecnológico](#stack-tecnológico)
- [Configuración](#configuración)
- [Ejecución local](#ejecución-local)
- [Pruebas y cobertura](#pruebas-y-cobertura)
- [Docker](#docker)
- [Endpoints de la API](#endpoints-de-la-api)
- [Estructura del proyecto](#estructura-del-proyecto)

## Arquitectura

Este servicio no se expone directamente a la app móvil: todas las llamadas pasan por [`bff-vitacare`](../bff-vitacare), que resuelve el paciente autenticado antes de reenviar la petición.

```
bff-vitacare ──▶ measurement-service ──▶ Oracle Database
                       │
                       └── las mediciones registradas alimentan a
                           ai-alert-service para la detección de alertas
```

## Stack tecnológico

| Componente | Detalle |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5.15 |
| Persistencia | Spring Data JPA + Oracle (driver `ojdbc11`) |
| Build | Maven, con JaCoCo para cobertura |

## Configuración

| Variable de entorno | Descripción |
|---|---|
| `DB_URL` | URL JDBC de la base de datos Oracle |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |

El servicio escucha en el puerto **8083** (`server.port`, fijo en `application.properties`).

## Ejecución local

```bash
./mvnw spring-boot:run
```

## Pruebas y cobertura

```bash
./mvnw test
```

El reporte de cobertura (JaCoCo) queda en `target/site/jacoco/index.html`.

## Docker

```bash
docker build -t measurement-service .
docker run -p 8083:8083 --env-file .env measurement-service
```

Build multi-stage: compila con `maven:3.9.6-eclipse-temurin-21` y ejecuta sobre `eclipse-temurin:21-jre-alpine`.

## Endpoints de la API

### Signos vitales

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/vitals` | Registra signos vitales |
| `GET` | `/api/vitals/{idControl}` | Busca una medición por id |
| `GET` | `/api/vitals/patient/{idPaciente}` | Historial de signos vitales de un paciente |
| `GET` | `/api/vitals/patient/{idPaciente}/latest` | Última medición de signos vitales |
| `DELETE` | `/api/vitals/{idControl}` | Elimina una medición |

### Glucosa

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/glucose` | Registra una medición de glucosa |
| `GET` | `/api/glucose/{idControl}` | Busca una medición por id |
| `GET` | `/api/glucose/patient/{idPaciente}` | Historial de glucosa de un paciente |
| `GET` | `/api/glucose/patient/{idPaciente}/latest` | Última medición de glucosa |
| `DELETE` | `/api/glucose/{idControl}` | Elimina una medición |

### Perfil lipídico

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/lipids` | Registra un perfil lipídico |
| `GET` | `/api/lipids/{idControl}` | Busca un perfil por id |
| `GET` | `/api/lipids/patient/{idPaciente}` | Historial de perfiles lipídicos de un paciente |
| `GET` | `/api/lipids/patient/{idPaciente}/latest` | Último perfil lipídico |
| `DELETE` | `/api/lipids/{idControl}` | Elimina un perfil |

### Historial combinado

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/controls/patient/{idPaciente}` | Historial combinado de todos los controles de salud del paciente |

## Estructura del proyecto

```
src/main/java/com/grupo10/measurement_service/
├── controller/   # Controladores REST (vitals, glucose, lipids, controls)
├── dto/          # Objetos de transferencia de datos
├── exception/    # Excepciones propias y su manejo
├── model/        # Entidades JPA
├── repository/   # Acceso a datos (Spring Data JPA)
├── service/      # Lógica de negocio
└── utils/        # Utilidades varias
```

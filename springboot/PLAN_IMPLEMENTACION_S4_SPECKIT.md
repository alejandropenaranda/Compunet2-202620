# Plan de Implementación en Clase — Sesión 4 (2 Horas)
## Introducción a Spec-Driven Development (SDD) y GitHub Spec-Kit en Spring Boot (Sin Instalar `uv`)

**Curso:** Computación en Internet II — Universidad Icesi  
**Duración Total:** 120 minutos (2 horas)  
**Modalidad:** Sesión teórico-práctica con flujo real de generación y refinamiento en vivo (5 Micro-Ciclos intercalados)  
**Prerrequisitos:** Proyecto `springboot` con Seguridad Dual (`@Order(1)` API JWT + `@Order(2)` MVC FormLogin) y `UsuarioRestController` implementados en la Sesión 3.

---

## 1. Propósito Pedagógico y Enfoque de la Sesión

En esta sesión los estudiantes pasan del **"Vibe Coding"** (pedirle código a la IA mediante prompts improvisados que rompen la arquitectura o exponen entidades JPA) a **Spec-Driven Development (SDD)** inspirado en **GitHub Spec-Kit** y en la guía metodológica del curso.

### ¿Por qué este enfoque "Zero-Install" con Plantillas y Comandos de Agente?
1. **Cero fricción de infraestructura**: No instalamos Python ni `uv` en los equipos del laboratorio.
2. **Estado Inicial Realista (Solo Plantillas y Memoria)**: Antes de iniciar la clase, el repositorio **NO tiene creada la carpeta `specs/001-cursos-rest-api/`**. Únicamente contiene:
   - La **Constitución** (`specs/00-constitution.md`) y la **Memoria del Proyecto** (`.agents/memory/tech-stack.md` y `.agents/memory/architecture.md`).
   - Las **Plantillas en Blanco** (`specs/templates/01-spec-template.md`, `02-plan-template.md`, `03-tasks-template.md`).
   - Los **Slash Commands (`/speckit.*`)** en `.github/prompts/` (para VS Code / GitHub Copilot) y los **Skills (`speckit-*`)** en `.agents/skills/` (para Antigravity CLI / IDE).
3. **Flujo Real de Desarrollador en Vivo**: Durante la clase, el profesor (y los estudiantes) invocan los comandos con lenguaje natural para que el agente cree `specs/001-cursos-rest-api/` en vivo, **revisan críticamente lo que genera la IA**, usan la fase **`Clarify` (Diálogo Socrático)** para resolver casos borde reales, y ejecutan la implementación tarea por tarea (**Single-Task Rule**).

---

## 2. Comparativa Conceptual: SDLC Tradicional / Vibe Coding vs. Las 6 Fases de SDD

| Fase SDD | Comando Slash / Skill | En el SDLC Tradicional / Vibe Coding | En Spec-Driven Development (SDD) | Artefacto en el Repo |
| :--- | :--- | :--- | :--- | :--- |
| **1. Constitution** | `/speckit.constitution` / `speckit-constitution` | Requisitos informales; el agente desconoce las versiones y convenciones del proyecto. | **Reglas de Oro Innegociables** y memoria permanente de stack y arquitectura. | `specs/00-constitution.md` + `.agents/memory/*.md` |
| **2. Specify** | `/speckit.specify` / `speckit-specify` | Prompts ambiguos (*"hazme el CRUD de cursos"*) mezclando el QUÉ con el CÓMO. | Especificación funcional pura (**QUÉ** y **POR QUÉ**) con criterios **Gherkin (`Dado/Cuando/Entonces`)**. | `specs/001-cursos-rest-api/01-spec.md` |
| **3. Clarify** | `/speckit.clarify` / `speckit-clarify` | El LLM adivina en silencio los casos borde y produce bugs ocultos en producción. | **Interrogatorio Socrático**: el agente formula 3–5 preguntas sobre vacíos y casos borde; el dev decide y refina la spec. | `01-spec.md` *(Refinado)* |
| **4. Plan** | `/speckit.plan` / `speckit-plan` | Codificación prematura; se duplican servicios o se exponen entidades JPA con relaciones `@ManyToMany`. | Auditoría del código existente y diseño técnico (**CÓMO**: DTOs, Mappers, `@PreAuthorize`, códigos HTTP) sin tocar `.java`. | `specs/001-cursos-rest-api/02-plan.md` |
| **5. Tasks** | `/speckit.tasks` / `speckit-tasks` | Generación monolítica de 10 archivos a la vez imposible de depurar. | Desglose en **tareas atómicas secuenciales (`T1..T5`)** con criterio de verificación individual. | `specs/001-cursos-rest-api/03-tasks.md` |
| **6. Implement** | `/speckit.implement` / `speckit-implement` | Pruebas tardías y deuda técnica acumulada. | **Single-Task Rule**: implementar 1 tarea a la vez -> compilar con `./mvnw` -> marcar `[x]` -> validar en Postman. | Clases `.java` + `03-tasks.md` `[x]` |

---

## 3. Cronograma General de los 5 Micro-Ciclos (120 Minutos)

| Micro-Ciclo | Tiempo | Duración | Fases SDD Cubiertas | Actividad Principal |
| :--- | :---: | :---: | :--- | :--- |
| **MC-1** | `00:00 - 00:20` | 20 min | **1. Constitution** | Del Vibe Coding a las 6 fases de SDD. Exploración de `AGENTS.md`, `.agents/memory/`, `00-constitution.md` y plantillas `specs/templates/`. |
| **MC-2** | `00:20 - 00:45` | 25 min | **2. Specify + 3. Clarify** | **En vivo:** Prompt en lenguaje natural con `/speckit.specify` para generar `001-cursos-rest-api/01-spec.md`, seguido de `/speckit.clarify` (Diálogo Socrático) para refinar casos borde. |
| **MC-3** | `00:45 - 01:10` | 25 min | **4. Plan + 5. Tasks** | **En vivo:** Auditoría arquitectónica con `/speckit.plan` (`02-plan.md`), revisión del desarrollador y partición atómica con `/speckit.tasks` (`03-tasks.md`). |
| **MC-4** | `01:10 - 01:40` | 30 min | **6. Implement (T1–T4)** | **En vivo:** Ejecución controlada (`Single-Task Rule`) con `/speckit.implement` para `T1` (DTOs), `T2` (Mapper), `T3` (Service) y `T4` (RestController) validando con `./mvnw test-compile`. |
| **MC-5** | `01:40 - 02:00` | 20 min | **Verificación (T5) y Cierre** | Validación cruzada en Postman / `curl` contra `01-spec.md` (JWT `200`, `201`, `400`, `401`, `403`, `404`) y reto autónomo. |

---

## 4. Desarrollo Paso a Paso por Micro-Ciclo (Guion Docente En Vivo)

---

### MICRO-CICLO 1: Del "Vibe Coding" a la Constitución y Memoria del Proyecto (00:00 – 00:20)

#### 1. Experimento Mental en Clase (5 min)
Preguntar a los estudiantes:
> *"Si abrimos el chat de la IA y le escribimos: `'Crea un controlador REST para Curso en Spring Boot'`, ¿qué errores arquitectónicos creen que cometerá en nuestro proyecto?"*

Mostrar en el código actual (`src/main/java/co/edu/icesi/next/model/Curso.java` y `Profesor.java`) los 3 peligros reales:
1. `Curso` tiene una relación `@ManyToOne` con `Profesor`, y `Profesor` tiene `@OneToOne` con `Usuario` (que contiene `password`) y `@OneToMany(mappedBy = "profesor")` hacia `cursos`. Si el controlador devuelve `Curso` directamente: **recursión infinita JSON (`StackOverflowError`) y fuga del hash BCrypt del password**.
2. El agente no sabrá si usar `/cursos` (que choca con el `CursoController` MVC de Thymeleaf) o `/api/v1/cursos` (protegido por la cadena JWT `@Order(1)`).
3. El agente inventará roles como `ROLE_ADMIN` en lugar de usar las autoridades reales de `data.sql` (`COURSE_READ`, `COURSE_WRITE`).

#### 2. Inspección de la Arquitectura Artesanal SDD en el Repositorio (10 min)
Abrir el explorador de archivos y mostrar que **`specs/001-cursos-rest-api/` todavía NO existe**. Mostrar las 4 piezas base:
1. [`springboot/AGENTS.md`](AGENTS.md): El contrato raíz que obliga al agente a respetar las 6 fases.
2. [`springboot/.agents/memory/tech-stack.md`](.agents/memory/tech-stack.md) y [`springboot/.agents/memory/architecture.md`](.agents/memory/architecture.md): La memoria técnica permanente del proyecto.
3. [`springboot/specs/00-constitution.md`](specs/00-constitution.md): Las 7 Reglas de Oro innegociables.
4. [`springboot/specs/templates/`](specs/templates/): Los 3 moldes en blanco (`01-spec-template.md`, `02-plan-template.md`, `03-tasks-template.md`).

#### 3. Ejecución en Vivo de la Fase 1: `/speckit.constitution` (5 min)
Ejecutar en el chat del agente (VS Code Copilot o Antigravity):

```text
/speckit.constitution Audita la constitución y la memoria del proyecto springboot y confírmame las reglas de oro vigentes antes de iniciar una nueva funcionalidad.
```
*(Si se usa `agy` CLI en terminal: `Usa la skill speckit-constitution para auditar el proyecto springboot`).*

---

### MICRO-CICLO 2: Fase 2 (`Specify`) y Fase 3 (`Clarify` — Diálogo Socrático) en Vivo (00:20 – 00:45)

#### Paso 2.1 — Generar `01-spec.md` desde Lenguaje Natural con `/speckit.specify` (10 min)
Explicar a los estudiantes: *"Como desarrolladores, empezamos dándole nuestra intención funcional en lenguaje natural al comando `/speckit.specify`. El agente tomará la plantilla `01-spec-template.md` y creará por nosotros `specs/001-cursos-rest-api/01-spec.md`."*

**Prompt en vivo para ejecutar `/speckit.specify`:**
```text
/speckit.specify Quiero crear la especificación 001-cursos-rest-api para exponer la gestión de Cursos como API REST en /api/v1/cursos protegida con JWT. Necesitamos listar todos los cursos, filtrar cursos por departamento, obtener el detalle de un curso por ID, crear un curso asociándolo a un profesor existente, actualizar un curso y eliminar un curso.
```

**Qué observar con los estudiantes cuando el agente termina:**
- Se acaba de crear la carpeta `springboot/specs/001-cursos-rest-api/01-spec.md`.
- Abrir `01-spec.md` y revisar las Historias de Usuario (`US-01` a `US-05`) y los escenarios Gherkin (`Dado / Cuando / Entonces`).
- **Punto pedagógico clave**: Preguntar a la clase: *"¿Este primer borrador de `01-spec.md` ya contempla qué pasa si intentamos borrar un curso que tiene estudiantes matriculados en la tabla `Matricula`? ¿O qué campos exactos del `Profesor` se deben mostrar sin exponer su `Usuario`?"* -> **¡Para eso existe la Fase 3: `Clarify`!**

#### Paso 2.2 — Interrogatorio Crítico con `/speckit.clarify` (15 min)
Explicar que en el flujo profesional de SDD nunca pasamos directo de la primera especificación al plan técnico sin antes hacer el **Diálogo Socrático (`Clarify`)**.

**Prompt en vivo para ejecutar `/speckit.clarify` (Modo Preguntas):**
```text
/speckit.clarify Analiza specs/001-cursos-rest-api/01-spec.md contrastándolo contra las entidades Curso, Profesor, Matricula y data.sql, y hazme las preguntas críticas sobre casos borde o ambigüedades antes de pasar al diseño técnico.
```

**Preguntas que el agente formulará al desarrollador (y que discutimos con la clase):**
1. **Integridad referencial al eliminar (`DELETE /api/v1/cursos/{id}`)**: La entidad `Matricula` tiene una relación `@ManyToOne` hacia `Curso`. ¿Qué debe ocurrir si se intenta eliminar un curso con matrículas registradas o qué alcance tendrá el borrado en esta iteración?
2. **Sensibilidad a mayúsculas/minúsculas en el filtro por departamento (`?departamento=`)**: En `CursoRepository` existe `findByDepartamentoIgnoreCase(String)`. ¿El filtro debe ser insensible a mayúsculas/minúsculas y qué debe devolver si ningún curso coincide?
3. **Representación del Profesor en la respuesta de Curso**: `Profesor` tiene relación con `Usuario` (que incluye `password` y `roles`). ¿Qué campos exactos del profesor deben incluirse en el JSON de respuesta del curso?
4. **Unicidad o validación de campos de entrada**: ¿Cuáles son las restricciones exactas para `nombre`, `departamento`, `creditos` y `profesorId` cuando se crea o actualiza un curso?

**Respuesta del Desarrollador (Profesor) en vivo para refinar `01-spec.md`:**
```text
/speckit.clarify Actualiza specs/001-cursos-rest-api/01-spec.md con estas decisiones de diseño:
1. En la respuesta JSON de cada Curso, el Profesor debe representarse aplanado únicamente con profesorId, profesorNombre y profesorEmail (nunca exponer la entidad Profesor ni Usuario).
2. El filtro ?departamento= debe ser insensible a mayúsculas/minúsculas (IgnoreCase) y devolver 200 OK con lista vacía [] si no hay coincidencias.
3. En creación y actualización: nombre y departamento son obligatorios (@NotBlank, máx 100 caracteres), creditos es obligatorio y mayor a 0 (@Positive), y profesorId es obligatorio (@NotNull). Si el profesorId no existe en BD, responder 404 Not Found.
4. Permisos: lectura (GET) requiere autoridad COURSE_READ; escritura (POST, PUT, DELETE) requiere autoridad COURSE_WRITE según data.sql.
5. Para eliminación (DELETE): si el curso existe y no tiene bloqueos se elimina devolviendo 204 No Content; si el ID no existe devuelve 404 Not Found.
```

#### Referencia Docente: Contenido Esperado de `specs/001-cursos-rest-api/01-spec.md` tras `Clarify`

```markdown
# Especificación Funcional: API REST de Gestión de Cursos

- **ID de Funcionalidad**: `001-cursos-rest-api`
- **Fecha**: 2026-10-28
- **Estado**: Clarificado y Aprobado (`Clarify` completado)
- **Constitución Base**: [`specs/00-constitution.md`](../00-constitution.md)

---

## 1. Contexto y Motivación (POR QUÉ)
El Sistema Académico de la Universidad Icesi cuenta actualmente con gestión de cursos acoplada a vistas SSR en Thymeleaf (`CursoController`) y un primer controlador REST para usuarios (`UsuarioRestController`). Se requiere exponer el catálogo y administración de **Cursos** mediante una API RESTful JSON bajo `/api/v1/cursos`, protegida con **JWT Stateless**, para permitir el consumo desde clientes desacoplados respetando los permisos granulares (`COURSE_READ`, `COURSE_WRITE`).

## 2. Historias de Usuario (QUÉ)
- **US-01 (Listar y Filtrar Cursos)**: Como usuario autenticado con permiso `COURSE_READ`, quiero consultar todos los cursos o filtrarlos por departamento (`?departamento=`) sin importar mayúsculas/minúsculas, para visualizar la oferta académica con los datos básicos de su profesor titular.
- **US-02 (Consultar Curso por ID)**: Como usuario autenticado con permiso `COURSE_READ`, quiero obtener el detalle de un curso por su `id`, para conocer sus créditos, departamento y profesor asignado.
- **US-03 (Crear Curso)**: Como administrador con permiso `COURSE_WRITE`, quiero registrar un nuevo curso indicando nombre, departamento, créditos y el `profesorId` titular, para ampliar el catálogo académico.
- **US-04 (Actualizar Curso)**: Como administrador con permiso `COURSE_WRITE`, quiero modificar los datos y el profesor titular de un curso existente por su `id`, para mantener actualizada la programación académica.
- **US-05 (Eliminar Curso)**: Como administrador con permiso `COURSE_WRITE`, quiero eliminar un curso por su `id` cuando ya no se oferte.

## 3. Criterios de Aceptación (Gherkin: Dado / Cuando / Entonces)

### Escenario 1: Listar cursos o filtrar por departamento exitosamente (`US-01`)
- **Dado** un cliente con token JWT válido que posee la autoridad `COURSE_READ`
- **Cuando** envía `GET /api/v1/cursos` o `GET /api/v1/cursos?departamento=sistemas`
- **Entonces** recibe `200 OK` con una lista JSON de cursos donde cada elemento incluye `id`, `nombre`, `departamento`, `creditos`, `profesorId`, `profesorNombre` y `profesorEmail` (o `[]` si ningún curso coincide con el filtro).

### Escenario 2: Consultar curso existente e inexistente (`US-02`)
- **Dado** un cliente con token JWT válido con autoridad `COURSE_READ`
- **Cuando** envía `GET /api/v1/cursos/1`
- **Entonces** recibe `200 OK` con el JSON del curso; y si consulta un ID inexistente (`GET /api/v1/cursos/999`), recibe `404 Not Found`.

### Escenario 3: Crear curso con validaciones y verificación de profesor (`US-03`)
- **Dado** un cliente con token JWT válido con autoridad `COURSE_WRITE`
- **Cuando** envía `POST /api/v1/cursos` con `nombre`, `departamento`, `creditos > 0` y un `profesorId` existente
- **Entonces** el sistema persiste el curso y responde `201 Created` con el DTO creado; si `creditos <= 0` o `nombre` está vacío responde `400 Bad Request`; y si `profesorId` no existe responde `404 Not Found`.

### Escenario 4: Denegación de escritura sin autoridad `COURSE_WRITE` (`US-03`, `US-04`, `US-05`)
- **Dado** un usuario con rol `ESTUDIANTE` o `PROFESOR` (que posee `COURSE_READ` pero no `COURSE_WRITE`)
- **Cuando** intenta enviar `POST`, `PUT` o `DELETE` sobre `/api/v1/cursos`
- **Entonces** Spring Security rechaza la petición con `403 Forbidden`.

## 4. Decisiones Clarificadas y Casos Borde (Resultado de `/speckit.clarify`)
1. **Aplanamiento de Profesor (Seguridad de Datos)**: El JSON de respuesta nunca expondrá la entidad `Profesor` ni su relación `Usuario` (evitando filtrar `password` o provocar ciclos infinitos). Solo expondrá `profesorId`, `profesorNombre` y `profesorEmail`.
2. **Filtro `IgnoreCase`**: El parámetro opcional `departamento` utilizará búsqueda insensible a mayúsculas/minúsculas.
3. **Validación de Entrada**: `nombre` (`@NotBlank`, máx 100), `departamento` (`@NotBlank`, máx 100), `creditos` (`@NotNull`, `@Positive`), `profesorId` (`@NotNull`).

## 5. Fuera de Alcance (Out of Scope)
- Gestión de matrículas de estudiantes (`ENROLLMENT_WRITE`), que pertenecerá a la especificación `002-matriculas-rest-api`.
- Modificación de las vistas HTML/Thymeleaf existentes en `CursoController`.
```

---

### MICRO-CICLO 3: Fase 4 (`Plan`) y Fase 5 (`Tasks`) en Vivo (00:45 – 01:10)

#### Paso 3.1 — Generar `02-plan.md` con `/speckit.plan` (15 min)
Explicar a la clase: *"Ahora que el QUÉ está 100% claro y sin ambigüedades en `01-spec.md`, le pedimos al agente que actúe como Arquitecto con `/speckit.plan`. Él inspeccionará el código que ya tenemos en el repositorio para no reinventar la rueda y diseñará el CÓMO en `02-plan.md` sin escribir todavía una sola línea de Java."*

**Prompt en vivo para ejecutar `/speckit.plan`:**
```text
/speckit.plan Genera el plan técnico 02-plan.md para la especificación specs/001-cursos-rest-api/01-spec.md auditando las clases existentes del proyecto y respetando 00-constitution.md.
```

**Revisión del Desarrollador sobre `02-plan.md` en clase:**
Abrir `springboot/specs/001-cursos-rest-api/02-plan.md` y verificar junto con los estudiantes:
- **Qué descubrió el agente que YA existe**:
  - `Curso.java` y `Profesor.java` en `model/`.
  - `CursoRepository.java` ya tiene `findByDepartamentoIgnoreCase(String departamento)` declarado.
  - `CursoService.java` ya tiene `listarTodos()`, `buscarPorId(Long)`, `guardar(Curso)` y `eliminar(Long)`, y solo requiere exponer `buscarPorDepartamento(String)`.
  - `GlobalExceptionHandler.java` ya maneja `IllegalArgumentException` y `MethodArgumentNotValidException`.
- **Qué componentes nuevos propuso crear**:
  - `dto/CursoRequestDTO.java`
  - `dto/CursoResponseDTO.java`
  - `mapper/CursoMapper.java`
  - `controller/CursoRestController.java`

*(Si el agente propuso devolver `400 Bad Request` para recursos no encontrados o crear un nuevo repositorio, el docente muestra cómo pedirle al agente ajustar `02-plan.md` antes de pasar a las tareas).*

#### Paso 3.2 — Generar `03-tasks.md` con `/speckit.tasks` (10 min)
Una vez aprobado `02-plan.md`, ejecutamos `/speckit.tasks` para dividir el trabajo en pasos atómicos verificables.

**Prompt en vivo para ejecutar `/speckit.tasks`:**
```text
/speckit.tasks Desglosa el plan técnico specs/001-cursos-rest-api/02-plan.md en una lista secuencial de tareas atómicas y verificables en specs/001-cursos-rest-api/03-tasks.md.
```

#### Referencia Docente: Estructura Esperada de `specs/001-cursos-rest-api/03-tasks.md`

```markdown
# Lista de Tareas de Implementación: `001-cursos-rest-api`

- **Especificación**: [`01-spec.md`](01-spec.md)
- **Plan Técnico**: [`02-plan.md`](02-plan.md)

---

## Tareas Secuenciales (Single-Task Rule)

- [ ] **Tarea T1: Crear DTOs de Entrada y Salida (`CursoRequestDTO` y `CursoResponseDTO`)**
  - **Objetivo**: Definir los contratos JSON de entrada (con validaciones Jakarta `@NotBlank`, `@Positive`, `@NotNull`) y salida (con datos aplanados del profesor).
  - **Archivos a crear**:
    - `src/main/java/co/edu/icesi/next/dto/CursoRequestDTO.java`
    - `src/main/java/co/edu/icesi/next/dto/CursoResponseDTO.java`
  - **Dependencias**: Ninguna.
  - **Criterio de Verificación**: `./mvnw test-compile` exitoso.

- [ ] **Tarea T2: Crear el Componente de Mapeo (`CursoMapper`)**
  - **Objetivo**: Implementar el componente `@Component` que convierte entre `Curso`, `Profesor`, `CursoRequestDTO` y `CursoResponseDTO` sin exponer entidades JPA.
  - **Archivos a crear**:
    - `src/main/java/co/edu/icesi/next/mapper/CursoMapper.java`
  - **Dependencias**: Requiere `T1`.
  - **Criterio de Verificación**: `./mvnw test-compile` exitoso.

- [ ] **Tarea T3: Ampliar `CursoService` con Búsqueda por Departamento**
  - **Objetivo**: Agregar el método `buscarPorDepartamento(String departamento)` en `CursoService` delegando en `cursoRepository.findByDepartamentoIgnoreCase(departamento)`.
  - **Archivos a modificar**:
    - `src/main/java/co/edu/icesi/next/service/CursoService.java`
  - **Dependencias**: Ninguna.
  - **Criterio de Verificación**: `./mvnw test-compile` exitoso.

- [ ] **Tarea T4: Implementar `CursoRestController` con Seguridad JWT (`@PreAuthorize`)**
  - **Objetivo**: Crear el controlador REST en `/api/v1/cursos` con los 5 endpoints (`GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`), validaciones `@Valid` y autorización `@PreAuthorize("hasAuthority('COURSE_READ')")` / `COURSE_WRITE`.
  - **Archivos a crear**:
    - `src/main/java/co/edu/icesi/next/controller/CursoRestController.java`
  - **Dependencias**: Requiere `T1`, `T2` y `T3`.
  - **Criterio de Verificación**: `./mvnw test-compile` exitoso.

- [ ] **Tarea T5: Validación Cruzada End-to-End de Escenarios Gherkin**
  - **Objetivo**: Verificar con peticiones HTTP reales (Postman / `curl`) los escenarios de `01-spec.md` (`200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`).
  - **Dependencias**: Requiere `T4`.
  - **Criterio de Verificación**: Todos los escenarios Gherkin responden con los códigos y contratos JSON especificados.
```

---

### MICRO-CICLO 4: Fase 6 (`Implement` — Single-Task Rule) en Vivo (01:10 – 01:40)

Explicar la **Regla de Oro de la Fase 6 (`Single-Task Rule`)**:
> *"Nunca le decimos al agente 'implementa todo'. Ejecutamos `/speckit.implement` tarea por tarea (`T1`, luego `T2`, luego `T3`, luego `T4`). En cada paso el agente escribe máximo 1–2 archivos, compila con `./mvnw test-compile`, marca `[x]` en `03-tasks.md` y nosotros revisamos el diff."*

#### Ejecución de T1 (DTOs)
```text
/speckit.implement Ejecuta únicamente la Tarea T1 de specs/001-cursos-rest-api/03-tasks.md.
```

**Código de Referencia para T1 (`CursoRequestDTO.java` y `CursoResponseDTO.java`):**

```java
// src/main/java/co/edu/icesi/next/dto/CursoRequestDTO.java
package co.edu.icesi.next.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoRequestDTO {

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 100, message = "El departamento no puede exceder 100 caracteres")
    private String departamento;

    @NotNull(message = "Los créditos son obligatorios")
    @Positive(message = "Los créditos deben ser un número positivo mayor a 0")
    private Integer creditos;

    @NotNull(message = "El ID del profesor titular es obligatorio")
    private Long profesorId;
}
```

```java
// src/main/java/co/edu/icesi/next/dto/CursoResponseDTO.java
package co.edu.icesi.next.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoResponseDTO {
    private Long id;
    private String nombre;
    private String departamento;
    private Integer creditos;
    private Long profesorId;
    private String profesorNombre;
    private String profesorEmail;
}
```

#### Ejecución de T2 (Mapper)
```text
/speckit.implement Ejecuta únicamente la Tarea T2 de specs/001-cursos-rest-api/03-tasks.md.
```

**Código de Referencia para T2 (`CursoMapper.java`):**

```java
// src/main/java/co/edu/icesi/next/mapper/CursoMapper.java
package co.edu.icesi.next.mapper;

import co.edu.icesi.next.dto.CursoRequestDTO;
import co.edu.icesi.next.dto.CursoResponseDTO;
import co.edu.icesi.next.model.Curso;
import co.edu.icesi.next.model.Profesor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CursoMapper {

    public CursoResponseDTO toResponseDTO(Curso curso) {
        if (curso == null) {
            return null;
        }
        Profesor profesor = curso.getProfesor();
        return new CursoResponseDTO(
                curso.getId(),
                curso.getNombre(),
                curso.getDepartamento(),
                curso.getCreditos(),
                profesor != null ? profesor.getId() : null,
                profesor != null ? profesor.getNombre() : null,
                profesor != null ? profesor.getEmail() : null
        );
    }

    public List<CursoResponseDTO> toResponseDTOList(List<Curso> cursos) {
        return cursos.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Curso toEntity(CursoRequestDTO dto, Profesor profesor) {
        Curso curso = new Curso();
        curso.setNombre(dto.getNombre());
        curso.setDepartamento(dto.getDepartamento());
        curso.setCreditos(dto.getCreditos());
        curso.setProfesor(profesor);
        return curso;
    }

    public void updateEntity(Curso curso, CursoRequestDTO dto, Profesor profesor) {
        curso.setNombre(dto.getNombre());
        curso.setDepartamento(dto.getDepartamento());
        curso.setCreditos(dto.getCreditos());
        curso.setProfesor(profesor);
    }
}
```

#### Ejecución de T3 (Ampliación de `CursoService`)
```text
/speckit.implement Ejecuta únicamente la Tarea T3 de specs/001-cursos-rest-api/03-tasks.md.
```

**Método agregado en `CursoService.java`:**
```java
    public List<Curso> buscarPorDepartamento(String departamento) {
        return cursoRepository.findByDepartamentoIgnoreCase(departamento);
    }
```

#### Ejecución de T4 (`CursoRestController`)
```text
/speckit.implement Ejecuta únicamente la Tarea T4 de specs/001-cursos-rest-api/03-tasks.md.
```

**Código de Referencia para T4 (`CursoRestController.java`):**

```java
// src/main/java/co/edu/icesi/next/controller/CursoRestController.java
package co.edu.icesi.next.controller;

import co.edu.icesi.next.dto.CursoRequestDTO;
import co.edu.icesi.next.dto.CursoResponseDTO;
import co.edu.icesi.next.mapper.CursoMapper;
import co.edu.icesi.next.model.Curso;
import co.edu.icesi.next.model.Profesor;
import co.edu.icesi.next.service.CursoService;
import co.edu.icesi.next.service.ProfesorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cursos")
public class CursoRestController {

    private final CursoService cursoService;
    private final ProfesorService profesorService;
    private final CursoMapper cursoMapper;

    public CursoRestController(CursoService cursoService,
                               ProfesorService profesorService,
                               CursoMapper cursoMapper) {
        this.cursoService = cursoService;
        this.profesorService = profesorService;
        this.cursoMapper = cursoMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ResponseEntity<List<CursoResponseDTO>> listarCursos(
            @RequestParam(required = false) String departamento) {
        List<Curso> cursos = (departamento != null && !departamento.isBlank())
                ? cursoService.buscarPorDepartamento(departamento.trim())
                : cursoService.listarTodos();
        return ResponseEntity.ok(cursoMapper.toResponseDTOList(cursos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_READ')")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<Curso> cursoOpt = cursoService.buscarPorId(id);
        if (cursoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Curso no encontrado con ID: " + id));
        }
        return ResponseEntity.ok(cursoMapper.toResponseDTO(cursoOpt.get()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COURSE_WRITE')")
    public ResponseEntity<?> crearCurso(@Valid @RequestBody CursoRequestDTO dto) {
        Optional<Profesor> profesorOpt = profesorService.buscarPorId(dto.getProfesorId());
        if (profesorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profesor no encontrado con ID: " + dto.getProfesorId()));
        }
        Curso nuevoCurso = cursoMapper.toEntity(dto, profesorOpt.get());
        Curso guardado = cursoService.guardar(nuevoCurso);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cursoMapper.toResponseDTO(guardado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_WRITE')")
    public ResponseEntity<?> actualizarCurso(@PathVariable Long id,
                                             @Valid @RequestBody CursoRequestDTO dto) {
        Optional<Curso> cursoOpt = cursoService.buscarPorId(id);
        if (cursoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Curso no encontrado con ID: " + id));
        }
        Optional<Profesor> profesorOpt = profesorService.buscarPorId(dto.getProfesorId());
        if (profesorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Profesor no encontrado con ID: " + dto.getProfesorId()));
        }
        Curso existente = cursoOpt.get();
        cursoMapper.updateEntity(existente, dto, profesorOpt.get());
        Curso actualizado = cursoService.guardar(existente);
        return ResponseEntity.ok(cursoMapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_WRITE')")
    public ResponseEntity<?> eliminarCurso(@PathVariable Long id) {
        Optional<Curso> cursoOpt = cursoService.buscarPorId(id);
        if (cursoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Curso no encontrado con ID: " + id));
        }
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### MICRO-CICLO 5: Validación Cruzada en Postman / `curl` (Tarea T5) y Reto Autónomo (01:40 – 02:00)

#### 1. Levantar la aplicación y probar los Escenarios Gherkin de `01-spec.md` (12 min)
Iniciar el servidor desde `springboot/`:
```powershell
./mvnw spring-boot:run
```

Ejecutar las pruebas de aceptación contrastando cada respuesta con `01-spec.md`:

1. **Autenticación JWT (`POST /api/v1/auth/login`)**:
   - Login como Admin (`admin@icesi.edu.co` / `password123`) -> obtener `$TOKEN_ADMIN`.
   - Login como Estudiante (`estudiante1@icesi.edu.co` / `password123`) -> obtener `$TOKEN_EST`.
2. **Escenario 1 (`GET /api/v1/cursos?departamento=sistemas`)**:
   - Con `$TOKEN_EST` -> Responde `200 OK` con lista JSON aplanada (`profesorId`, `profesorNombre`, `profesorEmail`), sin exponer `Usuario` ni `password`.
3. **Escenario 3 (`POST /api/v1/cursos` con `$TOKEN_ADMIN`)**:
   - Enviar JSON válido -> Responde `201 Created`.
   - Enviar `creditos: -3` -> Responde `400 Bad Request`.
   - Enviar `profesorId: 999` -> Responde `404 Not Found`.
4. **Escenario 4 (Seguridad `403 Forbidden`)**:
   - Enviar `POST /api/v1/cursos` con `$TOKEN_EST` -> Responde `403 Forbidden`.

Finalmente, pedir al agente que marque `T5` como `[x]` en `specs/001-cursos-rest-api/03-tasks.md`.

#### 2. Reto Autónomo para los Estudiantes (8 min)
En parejas, los estudiantes deben usar el flujo completo de 6 fases (`/speckit.specify` -> `/speckit.clarify` -> `/speckit.plan` -> `/speckit.tasks` -> `/speckit.implement`) para crear la funcionalidad **`002-profesores-rest-api`** (`/api/v1/profesores`) o **`002-matriculas-rest-api`** (`/api/v1/matriculas`), verificando que el agente tome los templates en blanco y los interrogue en la fase `Clarify`.

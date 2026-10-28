# 00. Constitución del Proyecto (`constitution.md`) — Sistema Académico Icesi

> **Estado:** Vigente (Inmutable)  
> **Proyecto:** `com.compunet.springboot` — Sistema de Gestión Académica (Universidad Icesi)  
> **Propósito en Spec-Kit:** Este documento define los principios arquitectónicos, el stack tecnológico y las restricciones **no negociables** del repositorio. Todo plan técnico (`02-plan.md`), lista de tareas (`03-tasks.md`) y fragmento de código generado por IA **DEBE** cumplir el 100% de estas cláusulas.

---

## Artículo 1. Stack Tecnológico Oficial (Inalterable)

1. **Lenguaje y Runtime:** Java 17 LTS (configurado en `pom.xml` y entorno de ejecución).
2. **Framework Base:** Spring Boot `4.0.8` (Spring Framework 7, gestión de dependencias con Maven `./mvnw`).
3. **Persistencia:** Spring Data JPA / Hibernate 7:
   - **Desarrollo / Producción (`application.properties`):** PostgreSQL (`jdbc:postgresql://localhost:5432/sistema_academico_own`).
   - **Pruebas Automatizadas (`application-test.properties`):** Base de datos en memoria H2 (`jdbc:h2:mem:testdb`).
4. **Seguridad:** Spring Security 6 / 7 con arquitectura **Dual `SecurityFilterChain`**:
   - `@Order(1)` para `/api/**`: **100% Stateless** autenticado mediante **JWT (JJWT `0.12.6`)** y `JwtAuthenticationFilter`.
   - `@Order(2)` para rutas web (`/**`): **Stateful** con sesiones HTTP y vistas SSR en Thymeleaf.
5. **Productividad y Mapeo:**
   - **Lombok** para reducción de código repetitivo (`@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`).
   - **MapStruct (`1.5.5.Final`)** + `lombok-mapstruct-binding (0.2.0)` para generación de mappers en tiempo de compilación (`javac`).

---

## Artículo 2. Arquitectura en Capas y Estructura de Paquetes

Todo código nuevo debe ubicarse estrictamente dentro del paquete raíz `com.compunet.springboot` respetando la separación de responsabilidades:

| Paquete | Responsabilidad Permitida | Prohibición Estricta |
| :--- | :--- | :--- |
| `controller.mvc` | Controladores `@Controller` que retornan vistas Thymeleaf. | Prohibido retornar entidades o JSON crudo. |
| `controller.rest` | Controladores `@RestController` bajo el prefijo `/api/v1/...`. | **Prohibido** inyectar `@Repository` directamente o exponer clases `@Entity` en firmas de métodos. |
| `dto.<Modulo>` | Objetos de transferencia divididos en `*RequestDTO` (entrada) y `*ResponseDTO` (salida). | Prohibido incluir anotaciones JPA (`@Entity`, `@ManyToOne`) dentro de los DTOs. |
| `mapper` | Interfaces `@Mapper(componentModel = "spring")` de MapStruct. | Prohibido usar librerías de reflexión en runtime como `ModelMapper` o mapeo manual repetitivo en el controlador. |
| `service` | Lógica de negocio, validaciones de dominio y orquestación de repositorios. | Prohibido acoplar clases web (`HttpServletRequest`, `ResponseEntity`) dentro de la capa de servicio. |
| `repository` | Interfaces que extienden `JpaRepository<T, ID>`. | Prohibido escribir lógica de negocio en repositorios. |
| `model` | Entidades JPA (`@Entity`) mapeadas al esquema relacional. | Prohibido devolverlas directamente al cliente HTTP sin pasar por un DTO. |

---

## Artículo 3. Estándares de Codificación e Inyección de Dependencias

1. **Inyección por Constructor Obligatoria:**
   - **PROHIBIDO** usar `@Autowired` sobre atributos (`field injection`).
   - Todas las dependencias en `@Service`, `@RestController`, `@Controller` y `@Component` deben declararse como `private final` y ser inyectadas mediante `@RequiredArgsConstructor` de Lombok.
2. **Aislamiento con Patrón DTO:**
   - Ningún método en `controller.rest` puede recibir ni retornar una entidad del paquete `model` (`Usuario`, `Curso`, `Profesor`, `Matricula`, `Rol`, `Permiso`).
   - Siempre debe existir separación explícita entre el payload de entrada (`*RequestDTO`) y el contrato de salida (`*ResponseDTO`).
3. **Semántica RESTful y `ResponseEntity<T>`:**
   - Las URIs deben usar **sustantivos en plural** y en minúsculas (`/api/v1/usuarios`, `/api/v1/cursos`). **Prohibido** usar verbos RPC en la URL (`/api/v1/crearCurso`).
   - Respuestas de creación (`POST`) deben retornar `201 Created` incluyendo el header `Location` construido con `ServletUriComponentsBuilder`.
   - Respuestas de eliminación (`DELETE`) deben retornar `204 No Content` sin cuerpo (`ResponseEntity<Void>`).
   - Recursos inexistentes o violaciones de negocio (`IllegalArgumentException`) deben traducirse a códigos HTTP semánticos (`404 Not Found`, `400 Bad Request` o `409 Conflict`), nunca arrojar trazas `500 Internal Server Error` no controladas al cliente.

---

## Artículo 4. Reglas de Seguridad y Autorización Declarativa (RBAC)

1. **Protección con `@PreAuthorize`:**
   - Todo endpoint dentro de `controller.rest` (excepto `/api/v1/auth/**`) debe declarar explícitamente su regla de acceso mediante `@PreAuthorize` utilizando `hasRole(...)`, `hasAnyRole(...)` o `hasAuthority(...)`.
2. **Catálogo Oficial de Roles y Autoridades (según `data.sql`):**
   - **Roles:** `ADMINISTRADOR`, `PROFESOR`, `ESTUDIANTE`.
   - **Permisos (Authorities):** `USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`, `COURSE_READ`, `COURSE_WRITE`, `ENROLLMENT_WRITE`.
3. **Preservación de la Cadena Dual:**
   - Ninguna modificación puede alterar el orden `@Order(1)` (`/api/**`) ni `@Order(2)` (MVC) en `SecurityConfig.java`, ni reemplazar los manejadores JSON de `401 Unauthorized` y `403 Forbidden`.

---

## Artículo 5. Criterio de Aceptación y Verificación Continua

Ninguna tarea de implementación se considera completada hasta que:
1. El proyecto compile sin errores de procesamiento de anotaciones (Lombok + MapStruct): `./mvnw clean compile`.
2. Las pruebas automatizadas existentes en `src/test/java` sigan pasando en verde: `./mvnw test`.

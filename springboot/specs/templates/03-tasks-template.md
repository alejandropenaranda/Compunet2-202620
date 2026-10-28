# 03. Lista de Tareas Atómicas (`03-tasks.md`) — [ID-FEATURE: Nombre de la Funcionalidad]

> **Regla de Oro de Spec-Kit:** Divide `02-plan.md` en unidades de trabajo **pequeñas, secuenciales y verificables**. El agente de IA **SOLO** debe ejecutar una tarea a la vez y detenerse hasta verificar que el proyecto compila.

---

## Checklist de Progreso
- [ ] **Tarea T1:** Creación de DTOs de Entrada y Salida (`RequestDTO` y `ResponseDTO`).
- [ ] **Tarea T2:** Creación del Mapper con MapStruct y verificación de código generado en `target/generated-sources`.
- [ ] **Tarea T3:** Adaptación o extensión de la capa de Servicio (si aplica).
- [ ] **Tarea T4:** Implementación del Controlador REST con `ResponseEntity<T>` y seguridad `@PreAuthorize`.
- [ ] **Tarea T5:** Pruebas de verificación end-to-end (Compilación, Tests y Postman).

---

## Detalle de Tareas

### Tarea T1: Creación de DTOs
- **Archivos a crear/modificar:**
  - `src/main/java/com/compunet/springboot/dto/...`
- **Criterio de completitud:** Clases creadas con Lombok sin referencias a entidades JPA.
- **Comando de verificación:** `./mvnw test-compile`

### Tarea T2: Creación del Mapper MapStruct
- **Archivos a crear/modificar:**
  - `src/main/java/com/compunet/springboot/mapper/...`
- **Criterio de completitud:** Interfaz anotada con `@Mapper(componentModel = "spring")` que compila y genera su `*MapperImpl.java`.
- **Comando de verificación:** `./mvnw clean compile`

### Tarea T3: Implementación del Controlador REST Seguro
- **Archivos a crear/modificar:**
  - `src/main/java/com/compunet/springboot/controller/rest/...`
- **Criterio de completitud:** Endpoints implementados con `@RequiredArgsConstructor`, `ResponseEntity` y `@PreAuthorize`.
- **Comando de verificación:** `./mvnw test`

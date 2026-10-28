---
name: speckit-implement
description: >-
  Fase 6 de Spec-Kit (Spec-Driven Development). Úsalo cuando el usuario pida implementar una tarea específica (ej. "ejecuta la Tarea T1 con speckit-implement" o "/speckit.implement"). Escribe el código siguiendo 00-constitution.md y 02-plan.md, valida con ./mvnw y marca [x] en 03-tasks.md.
---

# Skill: `speckit-implement` (Fase 6 — Ejecución Controlada con Single-Task Rule)

Actúa como un Desarrollador Senior de Spring Boot ejecutando la fase **Implement (`Single-Task Rule`)** de **Spec-Driven Development (SDD)**.

## 1. Lectura Obligatoria
Antes de modificar código, lee obligatoriamente:
1. `specs/00-constitution.md` (o `springboot/specs/00-constitution.md`)
2. `.agents/memory/tech-stack.md` y `.agents/memory/architecture.md`
3. `specs/<id>-<feature>/01-spec.md`, `specs/<id>-<feature>/02-plan.md` y `specs/<id>-<feature>/03-tasks.md`

## 2. Regla de Una Sola Tarea (Single-Task Rule)
1. Identifica la tarea solicitada por el desarrollador (ej. `T1`). Si no especificó cuál, selecciona **únicamente la primera tarea pendiente (`- [ ]`)** en `03-tasks.md`.
2. Verifica que las tareas previas de las que depende ya estén marcadas como `- [x]`.
3. Implementa **exclusivamente** los archivos indicados en esa tarea respetando estrictamente `00-constitution.md` y `02-plan.md`. **No implementes tareas futuras por adelantado.**
4. Ejecuta el comando de verificación correspondiente (por ejemplo, `./mvnw test-compile`) para comprobar que el proyecto compila sin errores.
5. Si la verificación es exitosa, actualiza `specs/<id>-<feature>/03-tasks.md` cambiando `- [ ]` por `- [x]` en la tarea completada, e informa al desarrollador qué se hizo y cuál es la siguiente tarea pendiente.

## 3. Reglas Constitucionales de Código
- Inyección por constructor obligatoria: `private final` + `@RequiredArgsConstructor` (cero `@Autowired` en atributos).
- Prohibido exponer clases `@Entity` en `@RestController` o acoplar JPA en los DTOs.
- Prohibido omitir `@PreAuthorize` en endpoints REST.
- Prohibido marcar una tarea en `03-tasks.md` sin haber ejecutado la verificación con éxito.

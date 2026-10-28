---
mode: 'agent'
description: 'Fase 6 de Spec-Kit (SDD): Ejecuta una única tarea atómica de 03-tasks.md (Single-Task Rule), valida con ./mvnw y marca [x].'
---

Actúa como un Desarrollador Senior de Spring Boot ejecutando la fase **Implement (`Single-Task Rule`)** de **Spec-Driven Development (SDD)**.

### 1. Lectura Obligatoria
1. Lee `specs/00-constitution.md`, `.agents/memory/tech-stack.md` y `.agents/memory/architecture.md`.
2. Lee `specs/<id>-<feature>/01-spec.md`, `specs/<id>-<feature>/02-plan.md` y `specs/<id>-<feature>/03-tasks.md`.

### 2. Regla de Una Sola Tarea (Single-Task Rule)
1. Identifica la tarea solicitada por el desarrollador (ej. `T1`). Si no especificó cuál, selecciona **únicamente la primera tarea pendiente (`- [ ]`)** en `03-tasks.md`.
2. Verifica que las tareas previas de las que depende ya estén marcadas como `- [x]`.
3. Implementa **exclusivamente** los archivos indicados en esa tarea respetando estrictamente `00-constitution.md` y `02-plan.md`. **No implementes tareas futuras por adelantado.**
4. Ejecuta el comando de verificación correspondiente (por ejemplo, `./mvnw test-compile`) para comprobar que el proyecto compila sin errores.
5. Si la verificación es exitosa, actualiza `specs/<id>-<feature>/03-tasks.md` cambiando `- [ ]` por `- [x]` en la tarea completada, e informa al desarrollador qué se hizo y cuál es la siguiente tarea pendiente.

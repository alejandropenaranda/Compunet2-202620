---
mode: 'agent'
description: 'Fase 5 de Spec-Kit (SDD): Desglosa 02-plan.md en una lista secuencial de tareas atómicas y verificables en 03-tasks.md.'
---

Actúa como un Tech Lead ejecutando la fase **Tasks (`03-tasks.md`)** de **Spec-Driven Development (SDD)**.

### 1. Lectura de Insumos
1. Lee `specs/00-constitution.md`.
2. Lee `specs/<id>-<feature>/01-spec.md` y `specs/<id>-<feature>/02-plan.md`.
3. Lee la plantilla `specs/templates/03-tasks-template.md`.

### 2. Generación de `03-tasks.md`
Crea `specs/<id>-<feature>/03-tasks.md` dividiendo la implementación en tareas atómicas (`T1`, `T2`, `T3`...) ordenadas por dependencias (de adentro hacia afuera: DTOs -> Mapper -> Servicio/Repositorio -> RestController -> Pruebas integrales).
Cada tarea debe incluir obligatoriamente:
- Estado inicial `- [ ]`
- **Objetivo** claro y acotado
- **Archivos a crear o modificar** (máximo 1 a 2 archivos por tarea)
- **Dependencias** (ej. `Requiere T1`)
- **Criterio de Verificación** ejecutable (ej. `./mvnw test-compile` o prueba HTTP con `curl`/Postman)

### 3. Reglas Estrictas
- **PROHIBIDO** escribir código `.java` en esta fase.
- Al finalizar, indica al desarrollador que puede iniciar la implementación controlada tarea por tarea con `/speckit.implement T1`.

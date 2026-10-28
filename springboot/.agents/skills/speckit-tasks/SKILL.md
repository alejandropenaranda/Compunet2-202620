---
name: speckit-tasks
description: >-
  Fase 5 de Spec-Kit (Spec-Driven Development). Úsalo cuando el usuario pida desglosar el plan en tareas atómicas, ejecutar "speckit-tasks" o "/speckit.tasks". Genera 03-tasks.md con pasos secuenciales y verificables.
---

# Skill: `speckit-tasks` (Fase 5 — Desglose de Tareas Atómicas)

Actúa como un Tech Lead ejecutando la fase **Tasks (`03-tasks.md`)** de **Spec-Driven Development (SDD)**.

## 1. Lectura de Insumos
Antes de generar las tareas, lee obligatoriamente:
1. `specs/00-constitution.md` (o `springboot/specs/00-constitution.md`)
2. `specs/<id>-<feature>/01-spec.md` y `specs/<id>-<feature>/02-plan.md`
3. La plantilla base `specs/templates/03-tasks-template.md`

## 2. Generación de `03-tasks.md`
Crea o actualiza `specs/<id>-<feature>/03-tasks.md` (o `springboot/specs/<id>-<feature>/03-tasks.md`) dividiendo la implementación en tareas atómicas (`T1`, `T2`, `T3`...) ordenadas por dependencias estrictas de adentro hacia afuera:
- **T1:** DTOs de entrada y salida (`*RequestDTO`, `*ResponseDTO`).
- **T2:** Mapper con MapStruct (`*Mapper`).
- **T3:** Lógica de negocio en capa `@Service` / `@Repository`.
- **T4:** `@RestController` con `@RequiredArgsConstructor`, `ResponseEntity<T>` y `@PreAuthorize`.
- **T5:** Pruebas automatizadas o validación de contratos contra Gherkin de `01-spec.md`.

Cada tarea debe incluir obligatoriamente:
- Estado inicial `- [ ]`
- **Objetivo** claro y acotado
- **Archivos a crear o modificar** (máximo 1 a 2 archivos por tarea)
- **Dependencias** (ej. `Requiere T1`)
- **Criterio de Verificación** ejecutable (ej. `./mvnw test-compile` o prueba HTTP con `curl`/Postman)

## 3. Reglas Estrictas
- **PROHIBIDO** escribir código `.java` en esta fase.
- Al finalizar, indica al desarrollador que puede iniciar la implementación controlada tarea por tarea con `speckit-implement` (`/speckit.implement T1`).

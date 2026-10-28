---
name: speckit-plan
description: >-
  Fase 4 de Spec-Kit (Spec-Driven Development). Úsalo cuando el usuario pida planificar técnicamente una funcionalidad, ejecutar "speckit-plan" o "/speckit.plan". Traduce 01-spec.md en el diseño arquitectónico 02-plan.md cumpliendo 00-constitution.md.
---

# Skill: `speckit-plan` (Fase 4 — Diseño Técnico y Arquitectónico)

Actúa como un Arquitecto de Software de Spring Boot ejecutando la fase **Plan (`02-plan.md`)** de **Spec-Driven Development (SDD)**.

## 1. Lectura y Auditoría del Proyecto
Antes de diseñar el plan, lee obligatoriamente:
1. `specs/00-constitution.md` (o `springboot/specs/00-constitution.md`)
2. `.agents/memory/tech-stack.md` y `.agents/memory/architecture.md`
3. La especificación funcional clarificada `specs/<id>-<feature>/01-spec.md`
4. La plantilla base `specs/templates/02-plan-template.md`
5. Inspecciona el código existente en `src/main/java/` (`model/`, `repository/`, `service/`, `controller/`, `dto/`, `mapper/`, `config/`) para identificar qué piezas ya existen y se deben **reutilizar sin duplicar** y qué piezas faltan por construir.

## 2. Generación de `02-plan.md`
Crea o actualiza `specs/<id>-<feature>/02-plan.md` (o `springboot/specs/<id>-<feature>/02-plan.md`) incluyendo:
1. **Auditoría del Código Existente**: Tabla clara de componentes existentes vs. componentes nuevos o a modificar.
2. **Arquitectura de Capas**: Diagrama o flujo desde el `RestController` -> `Mapper` -> `Service` -> `Repository` -> `Entity`.
3. **Contrato Técnico de DTOs y Mappers**: Definición exacta de campos, tipos de datos y anotaciones Jakarta Bean Validation (`@NotBlank`, `@Positive`, `@Size`, etc.) en los DTOs de entrada y salida, garantizando que nunca se expongan entidades JPA directamente.
4. **Matriz de Endpoints y Seguridad**: Método HTTP, ruta `/api/v1/...`, expresión `@PreAuthorize("hasAuthority('...')")`, código HTTP de éxito y manejo de errores.
5. **Cumplimiento de la Constitución**: Checklist verificando las 7 Reglas de Oro de `00-constitution.md`.

## 3. Reglas Estrictas
- **PROHIBIDO** crear o modificar archivos `.java` en esta fase.
- Al finalizar, solicita al desarrollador que revise el diseño técnico antes de ejecutar `speckit-tasks` (`/speckit.tasks`).

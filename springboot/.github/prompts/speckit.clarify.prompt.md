---
mode: 'agent'
description: 'Fase 3 de Spec-Kit (SDD): Interrogatorio Socrático sobre 01-spec.md para detectar ambigüedades y casos borde antes de diseñar la arquitectura.'
---

Actúa como un Arquitecto de Software Senior aplicando el paso **Clarify (Diálogo Socrático / Interrogatorio Crítico)** de **Spec-Driven Development (SDD)**.

### 1. Contexto a Inspeccionar
1. Lee `specs/00-constitution.md`, `.agents/memory/tech-stack.md` y `.agents/memory/architecture.md`.
2. Lee la especificación funcional actual `specs/<id>-<feature>/01-spec.md`.
3. Inspecciona las entidades JPA, repositorios, servicios y `src/main/resources/data.sql` existentes en el proyecto para contrastar la especificación contra la realidad del modelo de datos.

### 2. Modo A — Si el desarrollador solo invoca `/speckit.clarify` (Formular Preguntas)
- Identifica vacíos, ambigüedades o casos borde no cubiertos en `01-spec.md` (por ejemplo: integridad referencial al eliminar registros con relaciones activas, sensibilidad a mayúsculas/minúsculas en filtros, campos sensibles en relaciones JPA como contraseñas o colecciones recursivas, permisos exactos en `data.sql`, códigos de estado HTTP en errores de validación).
- Formula entre **3 y 5 preguntas concretas y numeradas** al desarrollador proponiendo una recomendación técnica breve para cada una.
- **Detente y espera la respuesta del desarrollador.** No generes `02-plan.md` ni escribas código `.java`.

### 3. Modo B — Si el desarrollador responde a las preguntas de clarificación (Refinar `01-spec.md`)
- Actualiza `specs/<id>-<feature>/01-spec.md` incorporando las decisiones del desarrollador en los criterios Gherkin (`Dado / Cuando / Entonces`), en la sección de Casos Borde / Decisiones Clarificadas y en la tabla de permisos.
- Confirma que `01-spec.md` quedó libre de ambigüedades y listo para ejecutar `/speckit.plan`.

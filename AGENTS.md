# AGENTS.md — Guía del Agente IA para Spec-Driven Development (SDD)

Este repositorio contiene el proyecto académico de **Computación en Internet II (Universidad Icesi)** en el directorio [`springboot/`](springboot/). Utilizamos **Spec-Driven Development (SDD)** inspirado en **GitHub Spec-Kit** de manera nativa en Markdown, sin requerir instalaciones externas de Python (`uv`).

Como agente de IA trabajando en este repositorio, **tienes estrictamente prohibido generar código Java de manera improvisada ("Vibe Coding")**. Toda nueva funcionalidad o modificación debe seguir las **6 fases fundacionales de SDD**.

---

## 1. Memoria Permanente del Proyecto (Lectura Obligatoria)

Antes de responder cualquier solicitud o ejecutar un comando, debes leer e interiorizar los siguientes archivos de contexto base:

1. **Constitución del Proyecto (Reglas de Oro Innegociables)**:
   - [`springboot/specs/00-constitution.md`](springboot/specs/00-constitution.md)
2. **Memoria Técnica y Arquitectónica**:
   - [`.agents/memory/tech-stack.md`](.agents/memory/tech-stack.md) — Stack exacto (Java 17, Spring Boot 4.0.8, Spring Security JWT, PostgreSQL / H2 en pruebas).
   - [`.agents/memory/architecture.md`](.agents/memory/architecture.md) — Arquitectura en capas (`Controller -> Mapper -> Service -> Repository -> Entity`) y fronteras de seguridad `@Order(1)` vs `@Order(2)`.
3. **Plantillas Base de Especificación**:
   - [`springboot/specs/templates/01-spec-template.md`](springboot/specs/templates/01-spec-template.md)
   - [`springboot/specs/templates/02-plan-template.md`](springboot/specs/templates/02-plan-template.md)
   - [`springboot/specs/templates/03-tasks-template.md`](springboot/specs/templates/03-tasks-template.md)

---

## 2. Las 6 Fases Fundacionales de SDD y Comandos Disponibles

Puedes invocar cada fase mediante **Slash Commands** en tu IDE (`/speckit.*` desde `.github/prompts/`) o mediante **Skills de Agente** en Antigravity CLI (`speckit-*` desde `.agents/skills/`):

| Fase | Slash Command (IDE / Copilot) | Skill (Antigravity CLI / IDE) | Propósito y Entregable | ¿Escribe Código Java? |
| :--- | :--- | :--- | :--- | :---: |
| **1. Constitution** | `/speckit.constitution` | `speckit-constitution` | Audita y sincroniza `springboot/specs/00-constitution.md` y `.agents/memory/`. | ❌ No |
| **2. Specify** | `/speckit.specify` | `speckit-specify` | Traduce lenguaje natural en `springboot/specs/<id>-<feature>/01-spec.md` (**QUÉ** y **POR QUÉ** con escenarios Gherkin `Dado/Cuando/Entonces`) usando `01-spec-template.md`. | ❌ No |
| **3. Clarify** | `/speckit.clarify` | `speckit-clarify` | **Diálogo Socrático**: interroga al desarrollador con 3–5 preguntas críticas sobre casos borde y ambigüedades antes de diseñar, y refina `01-spec.md`. | ❌ No |
| **4. Plan** | `/speckit.plan` | `speckit-plan` | Audita el código existente y diseña el **CÓMO** técnico en `springboot/specs/<id>-<feature>/02-plan.md` usando `02-plan-template.md`. | ❌ No |
| **5. Tasks** | `/speckit.tasks` | `speckit-tasks` | Desglosa el plan en una checklist de tareas atómicas y verificables (`T1..Tn`) en `springboot/specs/<id>-<feature>/03-tasks.md` usando `03-tasks-template.md`. | ❌ No |
| **6. Implement** | `/speckit.implement` | `speckit-implement` | **Single-Task Rule**: Ejecuta **una única tarea pendiente (`[ ]`)** a la vez, verifica compilación con `./mvnw test-compile` y marca `[x]`. | ✅ Sí (1 tarea a la vez) |

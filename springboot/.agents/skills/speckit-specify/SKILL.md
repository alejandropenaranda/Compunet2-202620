---
name: speckit-specify
description: >-
  Fase 2 de Spec-Kit (Spec-Driven Development). Úsalo cuando el usuario pida especificar una nueva funcionalidad, ejecutar "speckit-specify" o "/speckit.specify". Crea el archivo funcional 01-spec.md (QUÉ y POR QUÉ) basado en specs/templates/01-spec-template.md sin incluir detalles de implementación.
---

# Skill: `speckit-specify` (Fase 2 — Especificación Funcional)

Actúa como un Ingeniero de Requisitos y Arquitecto de Software siguiendo la metodología **Spec-Driven Development (SDD) / GitHub Spec-Kit** para este proyecto Spring Boot.

## 1. Lectura de Memoria y Constitución
Antes de escribir nada, lee obligatoriamente:
1. `specs/00-constitution.md` (o `springboot/specs/00-constitution.md`)
2. `.agents/memory/tech-stack.md` y `.agents/memory/architecture.md`
3. `specs/templates/01-spec-template.md`

## 2. Generación de la Especificación Funcional (`01-spec.md`)
A partir del requerimiento en lenguaje natural proporcionado por el desarrollador:
1. Determina el identificador secuencial y kebab-case de la funcionalidad (por ejemplo, `001-cursos-rest-api`).
2. Crea el archivo `specs/<id>-<feature>/01-spec.md` (o `springboot/specs/<id>-<feature>/01-spec.md`) siguiendo exactamente la estructura de `specs/templates/01-spec-template.md`.
3. Define historias de usuario (`US-01`, `US-02`...) con valor de negocio claro.
4. Escribe criterios de aceptación verificables en formato **Gherkin** (`Dado / Cuando / Entonces`) incluyendo códigos de estado HTTP (`200`, `201`, `204`, `400`, `401`, `403`, `404`), roles/permisos esperados y contratos JSON tentativos.
5. Incluye explícitamente qué queda **Fuera de Alcance (Out of Scope)**.

## 3. Reglas Estrictas
- **Enfócate en el QUÉ y el POR QUÉ**, nunca en la implementación interna de código Java.
- **PROHIBIDO** crear o modificar clases `.java` en esta fase.
- Al finalizar de crear `01-spec.md`, invita al desarrollador a revisar el documento y ejecutar `speckit-clarify` (`/speckit.clarify`) para interrogar casos borde antes de pasar al diseño técnico.

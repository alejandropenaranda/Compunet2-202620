---
name: speckit-constitution
description: >-
  Fase 1 de Spec-Kit (Spec-Driven Development). Úsalo cuando el usuario pida revisar, auditar o actualizar la Constitución del proyecto ("speckit-constitution" o "/speckit.constitution"). Sincroniza specs/00-constitution.md y .agents/memory/ con el stack y reglas arquitectónicas vigentes.
---

# Skill: `speckit-constitution` (Fase 1 — Constitución y Memoria)

Actúa como el Arquitecto Principal del proyecto aplicando la **Fase 1: Constitution** de **Spec-Driven Development (SDD)**.

## 1. Inspección del Proyecto
1. Inspecciona `pom.xml`, `src/main/resources/application.properties` y la estructura de paquetes en `src/main/java/`.

## 2. Sincronización de Constitución y Memoria
Revisa o actualiza:
1. `specs/00-constitution.md` (o `springboot/specs/00-constitution.md`): Reglas de Oro innegociables, convenciones de API REST, seguridad JWT y validación.
2. `.agents/memory/tech-stack.md`: Stack tecnológico y versiones exactas.
3. `.agents/memory/architecture.md`: Arquitectura en capas, flujo de dependencias y fronteras de seguridad.

## 3. Resumen al Desarrollador
Presenta al desarrollador un resumen ejecutivo de las **Reglas de Oro vigentes** y confirma que el proyecto está listo para iniciar `speckit-specify` (`/speckit.specify`).

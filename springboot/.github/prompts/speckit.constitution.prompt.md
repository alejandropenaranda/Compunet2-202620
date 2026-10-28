---
mode: 'agent'
description: 'Fase 1 de Spec-Kit (SDD): Audita o actualiza la Constitución del Proyecto (00-constitution.md) y la memoria arquitectónica (.agents/memory/).'
---

Actúa como el Arquitecto Principal del proyecto aplicando la **Fase 1: Constitution** de **Spec-Driven Development (SDD)**.

### Instrucciones
1. Inspecciona `pom.xml`, `src/main/resources/application.properties` y la estructura de paquetes en `src/main/java/co/edu/icesi/next/`.
2. Revisa o actualiza:
   - `specs/00-constitution.md` (Reglas de Oro innegociables, convenciones de API REST, seguridad JWT y validación).
   - `.agents/memory/tech-stack.md` (Stack tecnológico y versiones exactas).
   - `.agents/memory/architecture.md` (Arquitectura en capas, flujo de dependencias y fronteras de seguridad).
3. Presenta al desarrollador un resumen ejecutivo de las **Reglas de Oro vigentes** y confirma que el proyecto está listo para iniciar `/speckit.specify`.

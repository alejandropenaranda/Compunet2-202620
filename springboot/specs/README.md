# Guía Práctica de Spec-Driven Development (SDD) — Sin Instalar `uv`

En este proyecto aplicamos la metodología **Spec-Driven Development (SDD)** inspirada en **GitHub Spec-Kit** usando archivos Markdown puros, memoria arquitectónica y comandos de agente (`/speckit.*` y skills `speckit-*`), sin necesidad de instalar herramientas externas de Python (`uv`).

---

## 1. Arquitectura de Archivos SDD en el Repositorio

Al iniciar una nueva funcionalidad, el repositorio cuenta **únicamente con la Constitución, la Memoria del Proyecto, las Plantillas Base y los Comandos del Agente**. Las carpetas de cada funcionalidad (`specs/001-...`) se **generan en vivo** conversando con el agente:

```text
springboot/
├── AGENTS.md                                  # Punto de entrada y reglas del Agente IA
├── .agents/
│   ├── memory/                                # Memoria permanente del proyecto
│   │   ├── tech-stack.md                      # Versiones exactas (Java 21, Spring Boot 4.0.2, JWT)
│   │   └── architecture.md                    # Reglas de capas y seguridad dual @Order(1)/@Order(2)
│   └── skills/                                # Skills nativos para Antigravity CLI / IDE
│       ├── speckit-constitution/SKILL.md
│       ├── speckit-specify/SKILL.md
│       ├── speckit-clarify/SKILL.md
│       ├── speckit-plan/SKILL.md
│       ├── speckit-tasks/SKILL.md
│       └── speckit-implement/SKILL.md
├── .github/
│   └── prompts/                               # Slash commands (/speckit.*) para VS Code / Copilot
│       ├── speckit.constitution.prompt.md
│       ├── speckit.specify.prompt.md
│       ├── speckit.clarify.prompt.md
│       ├── speckit.plan.prompt.md
│       ├── speckit.tasks.prompt.md
│       └── speckit.implement.prompt.md
└── specs/
    ├── 00-constitution.md                     # Fase 1: Reglas de Oro innegociables
    ├── README.md                              # Esta guía rápida
    ├── templates/                             # Plantillas en blanco que el Agente toma como molde
    │   ├── 01-spec-template.md
    │   ├── 02-plan-template.md
    │   └── 03-tasks-template.md
    └── 001-cursos-rest-api/                   # <-- SE GENERA EN VIVO CON EL AGENTE
        ├── 01-spec.md                         # Fase 2 y 3: QUÉ funcional + Clarify (Gherkin)
        ├── 02-plan.md                         # Fase 4: CÓMO técnico (DTOs, Mappers, Endpoints)
        └── 03-tasks.md                        # Fase 5 y 6: Checklist atómico [ ] -> [x]
```

---

## 2. Flujo de Trabajo Real en 6 Fases (Paso a Paso)

### Fase 1: Constitution (`/speckit.constitution`)
Verifica las reglas innegociables en `specs/00-constitution.md` y la memoria en `.agents/memory/`.

### Fase 2: Specify (`/speckit.specify`) — *Del Lenguaje Natural al `01-spec.md`*
Le das al agente tu requerimiento en lenguaje natural y él crea automáticamente `specs/001-cursos-rest-api/01-spec.md` basándose en `specs/templates/01-spec-template.md`:

```text
/speckit.specify Quiero crear la especificación 001-cursos-rest-api para exponer un CRUD REST de Cursos en /api/v1/cursos protegido con JWT. Debe permitir listar cursos (con filtro opcional por departamento), consultar por ID, crear curso asociando profesor existente, actualizar y eliminar.
```

### Fase 3: Clarify (`/speckit.clarify`) — *Diálogo Socrático y Casos Borde*
Antes de diseñar la arquitectura, le pides al agente que audite `01-spec.md` contra las entidades reales del proyecto y te haga preguntas críticas sobre casos borde:

```text
/speckit.clarify Revisa specs/001-cursos-rest-api/01-spec.md y hazme las preguntas de clarificación sobre casos borde antes de pasar al plan.
```
El desarrollador responde las preguntas (ej. qué pasa al borrar un curso con matrículas activas, filtro `IgnoreCase`, qué campos del `Profesor` incluir sin exponer `Usuario.password`) y el agente actualiza `01-spec.md`.

### Fase 4: Plan (`/speckit.plan`) — *Diseño Arquitectónico en `02-plan.md`*
Con la especificación libre de ambigüedades, el agente inspecciona el código existente (`Curso.java`, `CursoRepository.java`, `CursoService.java`, `UsuarioRestController.java`) y genera `specs/001-cursos-rest-api/02-plan.md`:

```text
/speckit.plan Genera el plan técnico 02-plan.md para specs/001-cursos-rest-api reutilizando CursoService y CursoRepository existentes.
```

### Fase 5: Tasks (`/speckit.tasks`) — *Checklist Atómico en `03-tasks.md`*
El agente divide el plan técnico en tareas pequeñas, secuenciales y verificables (`T1..T5`) en `specs/001-cursos-rest-api/03-tasks.md`:

```text
/speckit.tasks Genera la lista de tareas atómicas 03-tasks.md para specs/001-cursos-rest-api.
```

### Fase 6: Implement (`/speckit.implement`) — *Single-Task Rule + Verificación*
El desarrollador ordena implementar **una sola tarea a la vez**. El agente escribe el código Java de esa tarea, ejecuta `./mvnw test-compile` y marca `[x]` en `03-tasks.md`:

```text
/speckit.implement Ejecuta únicamente la Tarea T1 de specs/001-cursos-rest-api/03-tasks.md.
```

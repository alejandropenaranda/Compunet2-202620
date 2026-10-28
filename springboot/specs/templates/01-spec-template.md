# 01. Especificación Funcional (`01-spec.md`) — [ID-FEATURE: Nombre de la Funcionalidad]

> **Regla de Oro de Spec-Kit:** En este documento definimos exclusivamente el **QUÉ** y el **POR QUÉ** desde la perspectiva del dominio y del usuario. **Evita prescribir código Java o detalles internos de implementación** (eso pertenece a `02-plan.md`).

---

## 1. Contexto y Objetivo de Negocio
- **Problema actual:** [Describe qué falta o qué limitación existe hoy en el sistema].
- **Valor esperado:** [Describe qué lograrán los actores del sistema una vez implementada esta funcionalidad].

---

## 2. Historias de Usuario (User Stories)

- **HU-01:** Como **[Rol: Estudiante / Profesor / Administrador]**, quiero **[acción de dominio]**, para **[beneficio de negocio]**.
- **HU-02:** Como **[Rol]**, quiero **[acción de dominio]**, para **[beneficio de negocio]**.

---

## 3. Reglas de Negocio y Matriz de Acceso (RBAC)

| Operación de Dominio | Roles / Permisos Autorizados | Restricción / Regla de Negocio |
| :--- | :--- | :--- |
| **[Ej. Consultar recursos]** | `COURSE_READ` (Todos los roles autenticados) | Permitir filtrado opcional por atributo clave. |
| **[Ej. Registrar / Actualizar]** | `COURSE_WRITE` (`ADMINISTRADOR`, `PROFESOR`) | Validar que las entidades relacionadas existan previamente. |
| **[Ej. Eliminar recurso]** | Rol `ADMINISTRADOR` exclusivamente | Un profesor o estudiante no puede eliminar registros maestros. |

---

## 4. Escenarios de Aceptación (Given / When / Then)

### Escenario 1: [Camino Feliz - Consulta o Creación exitosa]
- **DADO (Given):** [Estado inicial del sistema y actor autenticado].
- **CUANDO (When):** [Acción solicitada].
- **ENTONCES (Then):** [Resultado observable esperado y estado final].

### Escenario 2: [Caso Borde / Error de Dominio - Recurso relacionado no existe]
- **DADO (Given):** [Estado inicial].
- **CUANDO (When):** [Se intenta operar con un identificador inexistente].
- **ENTONCES (Then):** [El sistema rechaza la operación informando que el recurso no fue encontrado].

### Escenario 3: [Seguridad - Acceso denegado por rol insuficiente]
- **DADO (Given):** [Un usuario autenticado sin el permiso requerido].
- **CUANDO (When):** [Intenta ejecutar una operación restringida].
- **ENTONCES (Then):** [El sistema bloquea la operación con respuesta de prohibición `403 Forbidden`].

---

## 5. Fuera de Alcance (Out of Scope)
- [Lista explícita de lo que NO debe tocar ni inventar el agente de IA en esta iteración].

# Modelo Relacional - Sistema Académico

```mermaid
erDiagram
    USUARIO ||--o{ USUARIO_ROL : tiene
    ROL ||--o{ USUARIO_ROL : asignado_a
    ROL ||--o{ ROL_PERMISO : agrupa
    PERMISO ||--o{ ROL_PERMISO : otorgado_a
    USUARIO ||--o| PROFESOR : perfil_docente
    PROFESOR ||--o{ CURSO : dicta
    USUARIO ||--o{ MATRICULA : realiza
    CURSO ||--o{ MATRICULA : contiene

    USUARIO {
        bigint id PK
        string nombre
        string apellido
        string correo_institucional UK
        string password
        boolean active
    }

    ROL {
        bigint id PK
        string nombre UK
        string descripcion
    }

    PERMISO {
        bigint id PK
        string nombre UK
        string descripcion
    }

    USUARIO_ROL {
        bigint usuario_id PK,FK
        bigint rol_id PK,FK
    }

    ROL_PERMISO {
        bigint rol_id PK,FK
        bigint permiso_id PK,FK
    }

    PROFESOR {
        bigint id PK
        bigint usuario_id FK,UK
        string especialidad
        string departamento
    }

    CURSO {
        bigint id PK
        string nombre
        int creditos
        string departamento
        bigint profesor_id FK
    }

    MATRICULA {
        bigint usuario_id PK,FK
        bigint curso_id PK,FK
    }
```

---

## Descripción de Entidades y Relaciones

| Entidad / Tabla | Descripción | Llave Primaria (PK) | Llaves Foráneas (FK) |
| :--- | :--- | :--- | :--- |
| **`USUARIO`** | Almacena los usuarios del sistema (estudiantes, docentes, administradores). | `id` | - |
| **`ROL`** | Catálogo de roles del sistema de seguridad (`ADMINISTRADOR`, `PROFESOR`, `ESTUDIANTE`). | `id` | - |
| **`PERMISO`** | Permisos atómicos del sistema RBAC (`USER_CREATE`, `COURSE_READ`, etc.). | `id` | - |
| **`USUARIO_ROL`** | Relación muchos a muchos ($M:N$) entre Usuarios y Roles. | `(usuario_id, rol_id)` | `usuario_id` &rarr; `USUARIO(id)`<br>`rol_id` &rarr; `ROL(id)` |
| **`ROL_PERMISO`** | Relación muchos a muchos ($M:N$) entre Roles y Permisos. | `(rol_id, permiso_id)` | `rol_id` &rarr; `ROL(id)`<br>`permiso_id` &rarr; `PERMISO(id)` |
| **`PROFESOR`** | Perfil docente vinculado a un Usuario mediante relación uno a uno ($1:1$). | `id` | `usuario_id` &rarr; `USUARIO(id)` (UK) |
| **`CURSO`** | Asignaturas académicas dictadas por un Profesor ($1:N$). | `id` | `profesor_id` &rarr; `PROFESOR(id)` |
| **`MATRICULA`** | Registro de inscripción de un Estudiante (`Usuario`) en un `Curso` ($M:N$). | `(usuario_id, curso_id)` | `usuario_id` &rarr; `USUARIO(id)`<br>`curso_id` &rarr; `CURSO(id)` |

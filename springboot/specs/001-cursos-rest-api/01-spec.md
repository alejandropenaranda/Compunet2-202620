# 01. Especificación Funcional (`01-spec.md`) — 001-cursos-rest-api: API REST de Gestión de Cursos

> **Regla de Oro de Spec-Kit:** En este documento definimos exclusivamente el **QUÉ** y el **POR QUÉ** desde la perspectiva del dominio y del consumidor de la API. **No se prescribe código Java ni detalles internos de implementación** (estos corresponden a `02-plan.md`).

---

## 1. Contexto y Objetivo de Negocio

- **Problema actual:**  
  Actualmente, la gestión de cursos solo está disponible mediante vistas web renderizadas en el servidor (Thymeleaf en [`CursoController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/mvc/CursoController.java)). Los clientes externos, aplicaciones móviles o servicios desacoplados no cuentan con una interfaz programática RESTful segura bajo el prefijo `/api/v1/cursos` para consultar o administrar la oferta académica.
- **Valor esperado:**  
  Proporcionar una API RESTful moderna, estandarizada y segura protegida mediante JSON Web Tokens (JWT) que permita a los roles autorizados (Administradores, Profesores y Estudiantes) consultar cursos, filtrar flexiblemente por departamento, auditar el detalle de una asignatura con datos del docente titular, registrar nuevos cursos asociando docentes válidos, y actualizar o remover cursos del catálogo académico protegiendo el historial de matrículas.

---

## 2. Historias de Usuario (User Stories)

- **HU-01 (Listado General de Cursos):**  
  Como **Estudiante, Profesor o Administrador autenticado**, quiero listar todos los cursos registrados en el catálogo institucional, para conocer la oferta académica vigente.
- **HU-02 (Filtrado Flexible por Departamento):**  
  Como **Estudiante, Profesor o Administrador**, quiero filtrar cursos indicando de forma parcial o total el nombre del departamento (sin distinción de mayúsculas o minúsculas), para localizar rápidamente asignaturas de un área disciplinar (ej. `"sistemas"` o `"ciencias"`).
- **HU-03 (Consulta de Detalle por Identificador):**  
  Como **Usuario autenticado**, quiero consultar la información completa de un curso específico mediante su ID numérico, para visualizar su nombre, créditos, departamento y los datos principales del docente titular a cargo de forma aplanada y segura.
- **HU-04 (Creación de Curso con Docente Asociado):**  
  Como **Administrador del sistema**, quiero registrar un nuevo curso suministrando nombre, créditos, departamento y el identificador de un docente existente, para habilitar la asignatura dentro de la oferta académica.
- **HU-05 (Actualización de Curso):**  
  Como **Administrador del sistema**, quiero actualizar los datos básicos de un curso existente y/o reasignar el docente titular, para mantener actualizada la información curricular en caso de cambios departamentales.
- **HU-06 (Eliminación Segura de Curso):**  
  Como **Administrador del sistema**, quiero dar de baja un curso por su ID siempre y cuando no tenga matrículas registradas, para retirar asignaturas descontinuadas sin vulnerar el historial académico de los estudiantes.

---

## 3. Decisiones de Clarificación Acordadas (Fase 3: Clarify)

A partir del diálogo socrático entre el arquitecto y el desarrollador, se definen las siguientes reglas de diseño definitivas:

1. **Integridad Referencial en Eliminación (Decisión 1-A):**  
   Si un curso posee al menos una matrícula registrada (`matriculas.size() > 0`), el sistema **rechazará la eliminación física** retornando un código semántico `409 Conflict` (con mensaje: *"No se puede eliminar el curso porque cuenta con estudiantes matriculados"*). No se permite borrado en cascada de matrículas.
2. **Matriz RBAC para Creación y Edición (Decisión 2-B):**  
   Las operaciones de escritura (`POST /api/v1/cursos` y `PUT /api/v1/cursos/{id}`) quedan **restringidas estrictamente al rol `ADMINISTRADOR`** (`@PreAuthorize("hasRole('ADMINISTRADOR')")`). Ni profesores ni estudiantes pueden crear o alterar asignaturas.
3. **Búsqueda Flexible por Departamento (Decisión 3-A):**  
   El parámetro de consulta `?departamento=...` operará bajo coincidencia parcial e insensible a mayúsculas/minúsculas (`ContainingIgnoreCase`). Si el parámetro se omite o viaja en blanco, se retornará el catálogo completo.
4. **Contrato de Salida Aplanado (Decisión 4-A):**  
   [`CursoResponseDTO`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/specs/001-cursos-rest-api/01-spec.md#L83-L94) contendrá atributos planos del docente (`profesorId`, `profesorNombreCompleto`, `profesorCorreo`, `profesorEspecialidad`), sin exponer hashes, credenciales de `Usuario` ni estructuras circulares.

---

## 4. Reglas de Negocio y Matriz de Acceso (RBAC)

### 4.1. Reglas de Negocio de Dominio

1. **Obligatoriedad de Docente:** Todo curso debe tener asignado un docente titular válido (`profesorId`). No se permiten cursos sin profesor.
2. **Validación de Créditos:** La cantidad de créditos debe ser un número entero estrictamente positivo ($> 0$).
3. **Completitud y Validación de Cadenas:** `nombre` y `departamento` son obligatorios (no nulos ni vacíos).
4. **Protección de Matrículas:** No se puede eliminar ningún curso con matrículas activas (`409 Conflict`).
5. **Autenticación Obligatoria:** Todo acceso a `/api/v1/cursos` requiere cabecera HTTP `Authorization: Bearer <token_jwt_valido>`. Peticiones anónimas o con token expirado recibirán `401 Unauthorized`.

### 4.2. Matriz de Autorización (RBAC)

| Operación HTTP | Endpoint | Regla de Autorización | Roles Permitidos | Código de Éxito | Códigos de Error Posibles |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **GET** | `/api/v1/cursos` | `hasAuthority('COURSE_READ')` | `ADMINISTRADOR`, `PROFESOR`, `ESTUDIANTE` | `200 OK` | `401` |
| **GET** | `/api/v1/cursos/{id}` | `hasAuthority('COURSE_READ')` | `ADMINISTRADOR`, `PROFESOR`, `ESTUDIANTE` | `200 OK` | `401`, `404` |
| **POST** | `/api/v1/cursos` | `hasRole('ADMINISTRADOR')` | `ADMINISTRADOR` | `201 Created` | `400`, `401`, `403`, `404` |
| **PUT** | `/api/v1/cursos/{id}` | `hasRole('ADMINISTRADOR')` | `ADMINISTRADOR` | `200 OK` | `400`, `401`, `403`, `404` |
| **DELETE** | `/api/v1/cursos/{id}` | `hasRole('ADMINISTRADOR')` | `ADMINISTRADOR` | `204 No Content` | `401`, `403`, `404`, `409` |

---

## 5. Contratos de Datos Tentativos (JSON Schema)

### 5.1. Payload de Entrada: Registro y Actualización (`CursoRequestDTO`)
```json
{
  "nombre": "Computación en Internet II",
  "creditos": 3,
  "departamento": "Computación y Sistemas Inteligentes",
  "profesorId": 4
}
```

### 5.2. Contrato de Salida: Consulta y Detalle (`CursoResponseDTO`)
```json
{
  "id": 1,
  "nombre": "Computación en Internet II",
  "creditos": 3,
  "departamento": "Computación y Sistemas Inteligentes",
  "profesorId": 4,
  "profesorNombreCompleto": "Kevin Rodriguez",
  "profesorCorreo": "krodriguez@icesi.edu.co",
  "profesorEspecialidad": "Desarrollo de Software y Cloud Computing"
}
```

---

## 6. Escenarios de Aceptación (Gherkin: Dado / Cuando / Entonces)

### Escenario 1: Listado general exitoso de cursos
- **DADO:** Un usuario autenticado con permiso `COURSE_READ`.
- **CUANDO:** Envía una solicitud `GET /api/v1/cursos`.
- **ENTONCES:** El sistema responde `200 OK` y una lista JSON con todos los cursos y sus datos aplanados de profesor.

### Escenario 2: Filtrado por departamento con coincidencia parcial e insensible
- **DADO:** Un usuario autenticado con permiso `COURSE_READ`.
- **CUANDO:** Envía `GET /api/v1/cursos?departamento=sistemas`.
- **ENTONCES:** El sistema responde `200 OK` y retorna todos los cursos cuyo departamento contiene `"sistemas"` (ej. `"Computación y Sistemas Inteligentes"`), ignorando mayúsculas/minúsculas.

### Escenario 3: Consulta de curso por ID existente
- **DADO:** Un usuario autenticado con permiso `COURSE_READ` y existe el curso con ID `2`.
- **CUANDO:** Envía `GET /api/v1/cursos/2`.
- **ENTONCES:** El sistema responde `200 OK` con el JSON de detalle del curso y los datos de su docente titular.

### Escenario 4: Consulta de curso por ID inexistente
- **DADO:** Un usuario autenticado con permiso `COURSE_READ` y no existe curso con ID `999`.
- **CUANDO:** Envía `GET /api/v1/cursos/999`.
- **ENTONCES:** El sistema responde con código HTTP `404 Not Found` informando que el curso no fue encontrado.

### Escenario 5: Creación exitosa de curso por Administrador
- **DADO:** Un usuario autenticado con rol `ADMINISTRADOR` y existe un docente con ID `4`.
- **CUANDO:** Envía `POST /api/v1/cursos` con un payload válido:
  ```json
  {
    "nombre": "Seguridad en Redes",
    "creditos": 3,
    "departamento": "Computación y Sistemas Inteligentes",
    "profesorId": 4
  }
  ```
- **ENTONCES:** El sistema persiste la asignatura, responde `201 Created`, incluye el header `Location: .../api/v1/cursos/{id}` y retorna el objeto creado en el cuerpo de la respuesta.

### Escenario 6: Rechazo de creación por docente inexistente
- **DADO:** Un usuario con rol `ADMINISTRADOR`.
- **CUANDO:** Envía `POST /api/v1/cursos` con un `profesorId: 999` que no existe en el sistema.
- **ENTONCES:** El sistema responde `404 Not Found` (o `400 Bad Request`) informando que el docente con ID 999 no existe.

### Escenario 7: Rechazo de creación o actualización por datos inválidos
- **DADO:** Un usuario con rol `ADMINISTRADOR`.
- **CUANDO:** Envía `POST /api/v1/cursos` con créditos en cero/negativos (`creditos: 0`) o nombre en blanco (`nombre: ""`).
- **ENTONCES:** El sistema responde `400 Bad Request` con el detalle de las violaciones de validación.

### Escenario 8: Actualización exitosa de curso por Administrador
- **DADO:** Un usuario con rol `ADMINISTRADOR` y un curso existente con ID `1`.
- **CUANDO:** Envía `PUT /api/v1/cursos/1` con nuevos datos válidos y un `profesorId` existente.
- **ENTONCES:** El sistema actualiza el curso y responde `200 OK` con la entidad actualizada.

### Escenario 9: Eliminación exitosa de curso sin matrículas
- **DADO:** Un usuario con rol `ADMINISTRADOR` y un curso existente con ID `10` que no tiene ningún estudiante matriculado.
- **CUANDO:** Envía `DELETE /api/v1/cursos/10`.
- **ENTONCES:** El sistema elimina el curso y responde `204 No Content` con cuerpo vacío.

### Escenario 10: Rechazo de eliminación por integridad referencial (conflicto con matrículas)
- **DADO:** Un usuario con rol `ADMINISTRADOR` y un curso con ID `2` que tiene estudiantes matriculados en la tabla `matricula`.
- **CUANDO:** Envía `DELETE /api/v1/cursos/2`.
- **ENTONCES:** El sistema bloquea la eliminación y responde con código HTTP `409 Conflict` informando que no es posible eliminar un curso con matrículas activas.

### Escenario 11: Acceso denegado a Docente o Estudiante en operaciones de escritura
- **DADO:** Un usuario autenticado con rol `PROFESOR` o `ESTUDIANTE` (sin rol `ADMINISTRADOR`).
- **CUANDO:** Intenta ejecutar `POST /api/v1/cursos`, `PUT /api/v1/cursos/{id}` o `DELETE /api/v1/cursos/{id}`.
- **ENTONCES:** Spring Security intercepta la petición y retorna código HTTP `403 Forbidden`.

### Escenario 12: Petición anónima o token JWT inválido
- **DADO:** Un cliente HTTP sin cabecera de autenticación o con token expirado/inválido.
- **CUANDO:** Envía cualquier petición a `/api/v1/cursos/**`.
- **ENTONCES:** El sistema responde inmediatamente con código HTTP `401 Unauthorized`.

---

## 7. Fuera de Alcance (Out of Scope)

1. **Gestión de Inscripción/Matrícula:** Asociar o retirar estudiantes de un curso se realiza exclusivamente en `/api/v1/matriculas`.
2. **Creación en cascada de Profesores:** El endpoint `POST /api/v1/cursos` no crea nuevos profesores; deben existir previamente.
3. **Vistas Web Thymeleaf:** No se tocan controladores ni plantillas HTML SSR en este requerimiento.
4. **Paginación avanzada multi-columna:** El listado actual soporta catálogo completo con filtro por departamento.

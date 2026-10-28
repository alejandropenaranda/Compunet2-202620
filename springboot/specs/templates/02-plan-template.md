# 02. Plan Técnico de Implementación (`02-plan.md`) — [ID-FEATURE: Nombre de la Funcionalidad]

> **Regla de Oro de Spec-Kit:** Este documento traduce los requerimientos de `01-spec.md` en el **CÓMO** técnico, respetando el 100% de las cláusulas de `00-constitution.md`.

---

## 1. Análisis de Componentes Existentes (Reutilización)
Antes de crear clases nuevas, identifica qué piezas del proyecto `com.compunet.springboot` se reutilizarán sin duplicarlas:
- **Entidades (`model`):** `[Ej. Curso.java, Profesor.java]`
- **Repositorios (`repository`):** `[Ej. CursoRepository.java]`
- **Servicios (`service`):** `[Ej. CursoService.java — listar métodos existentes y si requiere algún método adicional]`

---

## 2. Diseño de Contratos de Datos (DTOs)

### 2.1. DTO de Entrada (`dto/<Modulo>/<Recurso>RequestDTO.java`)
- **Anotaciones Lombok:** `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`.
- **Campos y Tipos:**
  - `[campo1]`: `[Tipo]` — `[Descripción]`
- **Ejemplo JSON de Entrada:**
```json
{
  "campo": "valor"
}
```

### 2.2. DTO de Salida (`dto/<Modulo>/<Recurso>ResponseDTO.java`)
- **Anotaciones Lombok:** `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`.
- **Campos Aplanados (Flattening para evitar ciclos JPA):**
  - `[campo1]`: `[Tipo]`
- **Ejemplo JSON de Salida:**
```json
{
  "id": 1,
  "campo": "valor"
}
```

---

## 3. Estrategia de Mapeo (`mapper/<Recurso>Mapper.java`)
- **Configuración:** `@Mapper(componentModel = "spring")`.
- **Reglas `@Mapping`:**
  - `toResponseDTO(Entidad e)`: Especificar cómo se aplanan las relaciones `@ManyToOne` o `@OneToMany`.
  - `toEntity(RequestDTO dto)`: Especificar qué campos se ignoran (`ignore = true`) porque los resuelve la capa `@Service`.

---

## 4. Contrato de Endpoints REST (`controller/rest/<Recurso>RestController.java`)
- **Ruta Base:** `@RequestMapping("/api/v1/...")`

| Método HTTP | URI | `@PreAuthorize` | Request | Response Exitosa | Códigos de Error |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/...` | `hasAuthority('...')` | `@RequestParam` opcional | `200 OK` (`List<ResponseDTO>`) | `401`, `403` |
| `GET` | `/api/v1/.../{id}` | `hasAuthority('...')` | `@PathVariable Long id` | `200 OK` (`ResponseDTO`) | `404 Not Found` |
| `POST` | `/api/v1/...` | `hasAuthority('...')` | `@RequestBody RequestDTO` | `201 Created` + Header `Location` | `404` / `400` |
| `PUT` | `/api/v1/.../{id}` | `hasAuthority('...')` | `@PathVariable`, `@RequestBody` | `200 OK` (`ResponseDTO`) | `404 Not Found` |
| `DELETE` | `/api/v1/.../{id}` | `hasRole('...')` | `@PathVariable Long id` | `204 No Content` | `404 Not Found` |

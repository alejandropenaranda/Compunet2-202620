# Memoria Arquitectónica: Convenciones y Capas (`architecture.md`)

> **Propósito:** Establecer las reglas de diseño en capas, nomenclatura y seguridad RBAC del Sistema Académico Icesi.

## 1. Arquitectura en Capas Estricta
`Controller -> Service -> Repository -> Entity`
- **Prohibición 1:** Está terminantemente prohibido inyectar interfaces `@Repository` directamente en los controladores (`controller.rest` o `controller.mvc`).
- **Prohibición 2:** Está prohibido exponer entidades JPA (`@Entity` del paquete `model`) en las firmas de los métodos de `controller.rest`.

## 2. Inyección de Dependencias y DTOs
- **Inyección por Constructor:** Prohibido usar `@Autowired` en atributos. Usar siempre `private final` + `@RequiredArgsConstructor`.
- **Aislamiento DTO y Flattening:** Separar siempre payloads de entrada (`dto.<Modulo>.*RequestDTO`) y respuestas (`dto.<Modulo>.*ResponseDTO`). Aplanar relaciones JPA (`@ManyToOne`, `@OneToMany`) mediante MapStruct en el paquete `mapper` para evitar referencias circulares y nunca filtrar `Usuario.password`.

## 3. Catálogo Oficial RBAC (`data.sql`)
Todo endpoint en `controller.rest` (excepto `/api/v1/auth/**`) debe protegerse con `@PreAuthorize` empleando exclusivamente el catálogo real de la base de datos:
- **Roles (`hasRole`):** `ADMINISTRADOR`, `PROFESOR`, `ESTUDIANTE`.
- **Permisos (`hasAuthority`):** `USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`, `COURSE_READ`, `COURSE_WRITE`, `ENROLLMENT_WRITE`.

# Memoria Técnica: Restricciones del Stack (`tech-stack.md`)

> **Propósito:** Definir las versiones e infraestructura inmutables del proyecto `com.compunet.springboot` para evitar que el agente de IA alucine librerías incompatibles o configuraciones obsoletas.

- **Lenguaje:** Java 17 LTS (definido en `pom.xml` y JDK del entorno).
- **Framework Base:** Spring Boot `4.0.8` (Spring Framework 7, gestionado con Maven Wrapper `./mvnw`).
- **Persistencia:** Spring Data JPA / Hibernate 7:
  - Runtime / Principal: PostgreSQL (`jdbc:postgresql://localhost:5432/sistema_academico_own`) configurado en `src/main/resources/application.properties`.
  - Pruebas (`@ActiveProfiles("test")`): H2 en memoria (`jdbc:h2:mem:testdb`) en `src/test/resources/application-test.properties`.
- **Seguridad:** Spring Security con arquitectura **Dual `SecurityFilterChain`**:
  - `@Order(1)` para `/api/**`: **100% Stateless** con tokens **JWT (JJWT `0.12.6`)** y `JwtAuthenticationFilter`.
  - `@Order(2)` para `/**`: **Stateful** con sesiones HTTP y plantillas SSR en Thymeleaf.
- **Mapeo y Productividad:**
  - **Lombok** (`@Data`, `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`).
  - **MapStruct `1.5.5.Final`** (`@Mapper(componentModel = "spring")`) + `lombok-mapstruct-binding 0.2.0` para generación de código en compilación (`javac`).
- **Pruebas:** JUnit 5, Mockito y pruebas de integración con H2.

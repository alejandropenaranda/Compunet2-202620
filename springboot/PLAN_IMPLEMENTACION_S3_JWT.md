# Guía Docente y Plan de Implementación en Vivo — Sesión 3: Cierre de Seguridad REST & JWT: Ensamble Dual, Autorización RBAC (@PreAuthorize), Excepciones JSON y Laboratorio Forense

**Curso:** Computación en Internet II — Universidad Icesi  
**Sesión:** Semana 11 — Sesión 3 (Duración: 2.0 Horas / 120 min)  
**Presentación Asociada:** `slides/spring-REST-JWT/spring-jwt-s3/spring-jwt-s3.html` (20 Diapositivas)  
**Repositorio Base:** `2026-2-G5` (rama `spring-REST-JWT`) / `Code - AP - Compunet2-202620`  
**Modalidad Pedagógica:** **Micro-Ciclos Intercalados (Diagnóstico Conceptual $\leftrightarrow$ Live Coding $\leftrightarrow$ Verificación Forense en Postman)**  
**Objetivo Arquitectónico:** Culminar la integración de seguridad iniciada en la Sesión 2, resolviendo los puntos de quiebre de la cadena dual de filtros, blindando los endpoints REST con autorización declarativa (`@PreAuthorize`), implementando respuestas limpias en formato JSON (`401`/`403`) y ejecutando la matriz de pruebas forenses de integridad criptográfica.

---

## 1. Cronograma de la Sesión 3 (5 Micro-Ciclos)

| Micro-Ciclo | Tiempo | Bloque Teórico (`spring-jwt-s3`) | Práctica en Vivo (`springboot`) |
| :--- | :--- | :--- | :--- |
| **Ciclo 1** | `00:00 - 00:20` (20 min) | **Slides 1–5:** Recapitulación y Diagnóstico de Quiebre: ¿Por qué la cadena `@Order(1)` sin `.securityMatcher()` secuestra el tráfico MVC? Anatomía de `OncePerRequestFilter` y posicionamiento relativo (`addFilterBefore`). | **Paso 3.1:** Demostración del fallo en caliente. Inyección de `JwtAuthenticationFilter` en `SecurityConfig.java` y ajuste del principal en el token (`UserDetails`). |
| **Ciclo 2** | `00:20 - 00:45` (25 min) | **Slides 6–9:** Convivencia Dual y Manejo de Errores REST: Diferencia entre flujo web (redirección a login HTML) vs API REST (código HTTP y payload JSON). Contratos de `AuthenticationEntryPoint` (`401`) y `AccessDeniedHandler` (`403`). | **Paso 3.2:** Configuración definitiva de `apiSecurityFilterChain` con `.securityMatcher("/api/**")`, regla `/api/v1/auth/**`, registro del filtro y lambdas de manejo de excepciones JSON. |
| **Ciclo 3** | `00:45 - 01:10` (25 min) | **Slides 10–13:** Del "Quién eres" al "¿Qué puedes hacer?": Autenticación vs Autorización basada en Roles (RBAC) y Permisos (Authorities). Uso de `@EnableMethodSecurity` y SpEL (`hasRole`, `hasAuthority`, `hasAnyRole`). | **Paso 3.3:** Aseguramiento de `UsuarioRestController.java`: protección granular de `GET`, `POST`, `PUT`, `PATCH` y `DELETE` según roles `ADMINISTRADOR`, `PROFESOR` y autoridad `USER_READ`. |
| **Ciclo 4** | `01:10 - 01:45` (35 min) | **Slides 14–17:** Protocolo de Verificación Forense en Postman: Estrategia de pruebas de caja negra, decodificación en `jwt.io` y prueba de alteración de integridad criptográfica (sabotaje de HMAC-SHA256). | **Paso 3.4:** Ejecución de la matriz de 5 pruebas en Postman: petición sin token (`401`), login estudiante y decodificación, acceso no autorizado (`403`), CRUD admin completo (`200`/`201`/`204`), y mutación de firma (`401`). |
| **Ciclo 5** | `01:45 - 02:00` (15 min) | **Slides 18–20:** Desafíos Críticos en Producción: Fuga y revocación de tokens, expiración corta, el dilema de los *Stale Claims* (claims obsoletos en BD vs Stateless) y transición a Refresh Tokens. | **Paso 3.5:** Verificación final de la convivencia: prueba del login MVC Thymeleaf en navegador para constatar que ninguna vista web se rompió. |

---

## 2. Contexto de Entrada de la Sesión 3

Al iniciar esta sesión, el repositorio `2026-2-G5` cuenta con:
1. `pom.xml` con las 3 dependencias de `JJWT 0.12.6`.
2. `application.properties` con clave secreta y tiempo de expiración (1 hora).
3. `LoginRequestDTO` y `TokenResponseDTO` en `dtos/Auth/`.
4. `IJwtService.java` y `JwtServiceImpl.java` con generación y validación de tokens.
5. `AuthRestController.java` con `POST /api/v1/auth/login`.
6. `JwtAuthenticationFilter.java` creado como `@Component`.
7. `SecurityConfig.java` con las dos cadenas desdobladas, **pero con los siguientes 4 problemas que impiden el funcionamiento:**
   - Falta `.securityMatcher("/api/**")` en `@Order(1)` (intercepta y rompe rutas web).
   - Falta inyectar y registrar `JwtAuthenticationFilter` con `addFilterBefore`.
   - La regla pública dice `/api/v1/auth` en vez de `/api/v1/auth/**`.
   - No hay control de errores JSON: las fallas de autenticación devuelven redirects o páginas HTML por defecto.
   - `UsuarioRestController` sigue sin `@PreAuthorize`.

---

## Micro-Ciclo 1: Diagnóstico de Estado y Ajustes del Filtro (20 min)

### 1.A. Guía Teórica (Slides 1 a 5 — 10 min)
- **El problema del secuestro de rutas (Slide 4):**
  Cuando Spring Security evalúa una petición HTTP entrante, recorre la lista de `SecurityFilterChain` en orden ascendente según su `@Order`:
  - Si `@Order(1)` no tiene `.securityMatcher("/api/**")`, asume por omisión el patrón comodín `/**`.
  - Consecuencia: una petición a `/login` o `/usuarios` (Thymeleaf) es capturada por la cadena REST. Como la sesión es `STATELESS` y no hay formulario web configurado en esa cadena, el usuario recibe un `403` o un error genérico y la cadena `@Order(2)` jamás se evalúa.
- **Posicionamiento del Filtro (Slide 5):**
  `JwtAuthenticationFilter` debe ejecutarse **antes** de `UsernamePasswordAuthenticationFilter`. Si el token es válido, inyecta la autenticación en el `SecurityContextHolder`. Cuando los siguientes filtros de autorización evalúan la petición, el usuario ya está plenamente autenticado.

### 1.B. Práctica en Vivo — Paso 3.1: Ajuste del Principal en `JwtAuthenticationFilter.java` (10 min)
Verificar en `src/main/java/com/compunet/springboot/security/jwt/JwtAuthenticationFilter.java` que al construir el `UsernamePasswordAuthenticationToken`, el primer parámetro sea el objeto `userDetails` completo y no solo el `userEmail`:

```java
// Correcto: pasar userDetails como principal para que downstream code y @AuthenticationPrincipal funcionen
UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
    userDetails, 
    null,
    userDetails.getAuthorities()
);
authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
SecurityContextHolder.getContext().setAuthentication(authToken);
```

---

## Micro-Ciclo 2: Ensamble Definitivo de `SecurityConfig.java` (25 min)

### 2.A. Guía Teórica (Slides 6 a 9 — 10 min)
- **¿Por qué `AuthenticationEntryPoint` y `AccessDeniedHandler`? (Slide 8):**
  - En una aplicación tradicional MVC con sesiones, cuando un usuario anónimo intenta acceder a una ruta protegida, el framework responde con un `HTTP 302 Found` redirigiéndolo a `/login`.
  - En una **API REST**, los clientes son aplicaciones móviles o SPAs en JavaScript/TypeScript (React, Angular, Flutter). Una redirección HTTP `302` rompe el cliente o causa errores de CORS.
  - El servidor REST debe responder estrictamente con códigos de estado HTTP estándar (`401 Unauthorized` si no presentó token o es inválido; `403 Forbidden` si está autenticado pero carece de permisos) y un payload JSON explicativo.

### 2.B. Práctica en Vivo — Paso 3.2: Completar `SecurityConfig.java` (15 min)
Actualizar `src/main/java/com/compunet/springboot/config/SecurityConfig.java`:

```java
package com.compunet.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.compunet.springboot.security.jwt.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CADENA 1: Exclusiva para la API REST (/api/**) — 100% Stateless con JWT
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"No autenticado: Token JWT ausente, invalido o expirado\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Acceso denegado: No tienes permisos suficientes para este recurso\"}");
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /**
     * CADENA 2: Exclusiva para la aplicación Web MVC tradicional (Thymeleaf) con sesiones en RAM
     */
    @Bean 
    @Order(2)
    public SecurityFilterChain mvcsecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/public/**").permitAll()
                .requestMatchers("/login", "/access-denied").permitAll()
                .requestMatchers("/roles-mvc/**", "/permisos-mvc/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/profesores-mvc/**").hasAnyRole("ADMINISTRADOR", "PROFESOR")
                .requestMatchers("/usuarios/**", "/cursos-mvc/**", "/matriculas-mvc/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/usuarios", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .build();
    }
}
```

---

## Micro-Ciclo 3: Autorización Granular con `@PreAuthorize` (25 min)

### 3.A. Guía Teórica (Slides 10 a 13 — 10 min)
- **Roles vs. Authorities (Slide 11):**
  - Un **Rol** representa un grupo jerárquico amplio (ej: `ROLE_ADMINISTRADOR`, `ROLE_ESTUDIANTE`). En Spring Security, `hasRole("ADMIN")` busca internamente la autoridad con prefijo `"ROLE_ADMIN"`.
  - Un **Authority/Permiso** representa una acción atómica (ej: `USER_READ`, `USER_CREATE`, `USER_DELETE`). Se evalúa con `hasAuthority("USER_READ")`.
- **Habilitación de Seguridad por Métodos (Slide 12):**
  Gracias a la anotación `@EnableMethodSecurity` en `SecurityConfig`, podemos delegar la seguridad directamente en los métodos de los controladores usando expresiones SpEL (*Spring Expression Language*). Esto mantiene la lógica de autorización junto al endpoint y evita reglas monolíticas interminables en `SecurityConfig`.

### 3.B. Práctica en Vivo — Paso 3.3: Anotar `UsuarioRestController.java` (15 min)
Añadir el control de acceso en `src/main/java/com/compunet/springboot/controller/api/UsuarioRestController.java`:

```java
import org.springframework.security.access.prepost.PreAuthorize;

// Lectura: Permitida a Administradores, Profesores o cualquier usuario con permiso USER_READ
@GetMapping
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR') or hasAuthority('USER_READ')")
public List<UsuarioResponseDTO> obtenerUsuarios(@RequestParam(name = "activo", required = false) Boolean active) {
    List<Usuario> users = usuarioService.listarUsuarios(active);
    return usuarioMapper.toListUsuarioResponseDTO(users);
}

@GetMapping("/{id}")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'PROFESOR') or hasAuthority('USER_READ')")
public ResponseEntity<UsuarioResponseDTO> obtnerUsuarioPorId(@PathVariable Long id) {
    // ...
}

// Mutación: Restringida exclusivamente al Administrador
@PostMapping
@PreAuthorize("hasRole('ADMINISTRADOR')")
public ResponseEntity<UsuarioResponseDTO> crearUsurio(@RequestBody UsuarioRequestDTO requestDTO) {
    // ...
}

@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
        @PathVariable Long id,
        @RequestBody UsuarioRequestDTO requestDTO) {
    // ...
}

@PatchMapping("/{id}/estado")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public ResponseEntity<UsuarioResponseDTO> alternarEstado(@PathVariable Long id) {
    // ...
}

@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
    // ...
}
```

---

## Micro-Ciclo 4: Protocolo de Verificación Forense en Postman (35 min)

### 4.A. Guía Teórica (Slides 14 a 17 — 10 min)
- **Pruebas de Integridad Criptográfica (Slide 16):**
  Demostrar a los estudiantes cómo funciona el mecanismo de no-repudio e integridad:
  - Un token JWT se compone de `Header.Payload.Signature`.
  - Si un atacante intercepta el token y cambia el payload en Base64 (por ejemplo, cambiando `"sub": "estudiante@icesi.edu.co"` por `"sub": "admin@icesi.edu.co"`), el servidor recalcula:
    $$\text{HMAC-SHA256}(\text{Header} + \text{"."} + \text{PayloadModificado}, \text{secretKey})$$
  - La firma resultante no coincidirá con la firma enviada. JJWT arroja `SignatureException` y la petición es abortada inmediatamente con `401 Unauthorized`.

### 4.B. Práctica en Vivo — Paso 3.4: Matriz de 5 Pruebas en Postman (25 min)

#### Prueba 1: Petición anónima a recurso protegido
* **Método y URL:** `GET http://localhost:8080/springboot-api/api/v1/usuarios`
* **Headers:** *Ninguno*
* **Resultado Esperado:** `401 Unauthorized`
* **Body:**
  ```json
  {
    "error": "No autenticado: Token JWT ausente, invalido o expirado"
  }
  ```

#### Prueba 2: Login con Estudiante y Decodificación en `jwt.io`
* **Método y URL:** `POST http://localhost:8080/springboot-api/api/v1/auth/login`
* **Headers:** `Content-Type: application/json`
* **Body:**
  ```json
  {
    "correoInstitucional": "estudiante@icesi.edu.co",
    "password": "user123"
  }
  ```
* **Resultado Esperado:** `200 OK` con JSON conteniendo el token.
* **Inspección en jwt.io:** Copiar el token en [https://jwt.io](https://jwt.io). Constatar que en `authorities` aparece `["ROLE_ESTUDIANTE"]` y no tiene permisos de administrador.

#### Prueba 3: Estudiante intenta invocar endpoint restringido
* **Método y URL:** `DELETE http://localhost:8080/springboot-api/api/v1/usuarios/1`
* **Headers:** `Authorization: Bearer <TOKEN_ESTUDIANTE>`
* **Resultado Esperado:** `403 Forbidden`
* **Body:**
  ```json
  {
    "error": "Acceso denegado: No tienes permisos suficientes para este recurso"
  }
  ```

#### Prueba 4: Login de Administrador y Operación Exitosa
* **Método y URL:** `POST http://localhost:8080/springboot-api/api/v1/auth/login`
* **Body:**
  ```json
  {
    "correoInstitucional": "admin@icesi.edu.co",
    "password": "admin123"
  }
  ```
* **Resultado Esperado:** `200 OK` con token de administrador.
* **Consumo:** `GET http://localhost:8080/springboot-api/api/v1/usuarios` con `Authorization: Bearer <TOKEN_ADMIN>`.
* **Resultado:** `200 OK` con el listado completo de usuarios en JSON.

#### Prueba 5: Sabotaje Criptográfico de Firma
* **Método y URL:** `GET http://localhost:8080/springboot-api/api/v1/usuarios`
* **Acción:** Tomar el `<TOKEN_ADMIN>`, ir a los últimos 4 caracteres de la firma y cambiarlos por `xxxx`.
* **Headers:** `Authorization: Bearer <TOKEN_ALTERADO>`
* **Resultado Esperado:** `401 Unauthorized`.
* **Inspección en consola de Spring Boot:** Se observa la captura silenciosa de `JwtException` y limpieza del contexto de seguridad.

---

## Micro-Ciclo 5: Escenarios Críticos en Producción, Hidratación desde BD y Cierre (15 min)

### 5.A. Guía Teórica (Slides 18 a 21 — 10 min)
- **Los 3 Escenarios Críticos (Slide 19):**
  1. *Robo de Token Vigente:* Mitigado con Access Tokens de vida corta y cookies `HttpOnly` para Refresh Tokens.
  2. *Alteración del Payload:* Bloqueado matemáticamente por la firma HMAC-SHA256 (`SignatureException` ➔ `401 Unauthorized`).
  3. *Stale Claims (Datos Viejos):* Desfase temporal entre un cambio de roles/estado en BD y la expiración `exp` del token.

- **Decisión Arquitectónica: Hidratación de UserDetails desde BD vs. 100% Claims del Token (Slide 20):**
  | Dimensión Arquitectónica | Enfoque A: Hidratación desde BD (`UserDetailsService`) | Enfoque B: 100% Stateless (Claims del JWT) |
  | :--- | :--- | :--- |
  | **Costo en Base de Datos** | 1 consulta SQL rápida por clave única (`loadUserByUsername`) | **0 consultas SQL** (Verificación solo en CPU) |
  | **Revocación de Permisos** | **Efecto Inmediato** en la siguiente petición HTTP | Diferido hasta que el token expire (`exp`) |
  | **Desactivación de Cuenta** | **Bloqueo Inmediato** (`active = false` evaluado en BD) | El usuario sigue operando hasta que expire su JWT |
  | **Caso de Uso Recomendado** | **Sistemas académicos, financieros y empresariales críticos** | Microservicios masivos de alta concurrencia con tokens de 5 min |

- **Balance de Ingeniería:**
  Incluso cuando `JwtAuthenticationFilter` consulta `UserDetailsService` en cada petición para hidratar los roles frescos de la base de datos, la arquitectura sigue siendo **100% Stateless en la capa web**, ya que el contenedor Servlet **no almacena ningún objeto `HttpSession` en memoria RAM** entre peticiones.

- **Estrategias Complementarias para 100% Stateless (Slide 21):**
  1. *Refresh Tokens:* Token de acceso efímero (5–15 min) + Refresh Token revocable en BD.
  2. *Blacklist en Redis:* Invalidación de `jti` con TTL igual al tiempo remanente del JWT.

### 5.B. Práctica en Vivo — Paso 3.5: Demostración de Convivencia Web MVC (5 min)
* Abrir el navegador en `http://localhost:8080/springboot-api/usuarios`.
* Comprobar que Spring Security redirige a `http://localhost:8080/springboot-api/login` (formulario HTML de Thymeleaf).
* Iniciar sesión como `admin@icesi.edu.co` con contraseña `admin123`.
* Constatar que la sesión por cookies `JSESSIONID` funciona de manera impecable y totalmente aislada de la API REST gracias a la arquitectura `@Order(1)` vs `@Order(2)`.

---

## 3. Resumen de Archivos Modificados en la Sesión

1. `src/main/java/com/compunet/springboot/security/jwt/JwtAuthenticationFilter.java` (Ajuste del principal `userDetails`).
2. `src/main/java/com/compunet/springboot/config/SecurityConfig.java` (`securityMatcher`, `addFilterBefore`, manejo de errores 401/403).
3. `src/main/java/com/compunet/springboot/controller/api/UsuarioRestController.java` (Anotaciones `@PreAuthorize`).

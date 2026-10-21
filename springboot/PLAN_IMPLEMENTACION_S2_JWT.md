# Guía Docente y Plan de Implementación en Vivo — Sesión 2: Filtros, `SecurityFilterChain` Dual, `OncePerRequestFilter` y Autenticación Stateless con JWT

**Curso:** Computación en Internet II — Universidad Icesi  
**Sesión:** Semana 11 — Sesión 2 (Duración: 2.0 Horas / 120 min)  
**Presentación Asociada:** [spring-jwt-s2.html](../slides/spring-REST-JWT/spring-jwt-s2/spring-jwt-s2.html) / [spring-jwt-s2.pdf](../slides/spring-REST-JWT/spring-jwt-s2/spring-jwt-s2.pdf) (24 Diapositivas)  
**Repositorio Base:** `springboot/` (`context-path: /springboot-api`)  
**Modalidad Pedagógica:** **Micro-Ciclos Intercalados (Teoría en Slides $\leftrightarrow$ Implementación en Vivo)**  
**Objetivo Arquitectónico:** Asegurar los endpoints REST creados en la [Sesión 1 (`PLAN_IMPLEMENTACION_S1_REST.md`)](./PLAN_IMPLEMENTACION_S1_REST.md) bajo `/api/**` de manera **100% Stateless** usando JWT sin romper las vistas preexistentes Thymeleaf MVC (`/**`).

---

## 1. Cronograma de la Sesión 2 (4 Micro-Ciclos Intercalados)

| Micro-Ciclo | Tiempo | Bloque Teórico (`spring-jwt-s2` — 24 Diapositivas) | Práctica en Vivo (`springboot` — Seguridad `/api/**`) |
| :--- | :--- | :--- | :--- |
| **Ciclo 1** | `00:00 - 00:30` (30 min) | **Slides 1–8:** Contraste Arquitectónico: Sesiones Stateful (`JSESSIONID`) vs. Tokens Stateless (`JWT`), Estándar RFC 7519 (`Header.Payload.Signature`), Base64URL vs Encriptación, Algoritmos de Firma (HS256 vs RS256). | **Paso 2.1:** Adición de dependencias `JJWT 0.12.6` en `pom.xml` y parametrización de `jwt.secret` (Base64 $\ge$ 256 bits) y `jwt.expiration` en `application.properties`. |
| **Ciclo 2** | `00:30 - 01:00` (30 min) | **Slides 9–12:** Flujo de Autenticación REST: Secuencia completa de `POST /api/v1/auth/login`, delegación en `AuthenticationManager` + `DaoAuthenticationProvider`, emisión del token firmado con `JwtService.generateToken()`, respuesta en `TokenResponseDTO` e inspección en `jwt.io`. | **Paso 2.2:** Creación de `LoginRequestDTO`, `TokenResponseDTO`, `IJwtService.java` y `JwtServiceImpl.generateToken()`, `AuthRestController.java`, exposición del bean `AuthenticationManager` en `SecurityConfig` y primera prueba en Postman con decodificación en `jwt.io`. |
| **Ciclo 3** | `01:00 - 01:35` (35 min) | **Slides 13–19:** Flujo de Autorización: ¿Cómo se consume el token emitido? Cadena de filtros, ¿Por qué extender `OncePerRequestFilter`?, Verificación criptográfica con `parseSignedClaims`, Algoritmo de intercepción de `JwtAuthenticationFilter`, y Coexistencia Arquitectónica Dual (`@Order(1)` stateless para `/api/**` vs `@Order(2)` stateful para `/**`). | **Paso 2.3:** Implementación de métodos de verificación en `JwtServiceImpl.java` (`extractUsername`, `isTokenValid`), codificación de `JwtAuthenticationFilter.java` y configuración de la cadena dual `@Order(1)` en `SecurityConfig.java`. |
| **Ciclo 4** | `01:35 - 02:00` (25 min) | **Slides 20–24:** Análisis de 3 Escenarios Críticos en Producción (Robo de Token Vigente, Falsificación de Payload / Firma rota `401`, *Stale Claims* y Decisión Arquitectónica de Hidratación BD vs 100% Stateless Claims). | **Paso 2.4:** Ejecución del laboratorio integral de 5 pruebas en Postman (Petición sin token `401`, Login Estudiante en `jwt.io`, Acceso Estudiante `403`, Admin CRUD completo `200`/`201`, Sabotaje de firma `401`). |

---

## 2. Contexto de Entrada de la Sesión 2

Al iniciar esta clase, el proyecto ya cuenta con los contratos y controladores REST construidos durante la Sesión 1:
- `UsuarioRequestDTO.java` y `UsuarioResponseDTO.java` en `dto/Usuarios/`.
- `UsuarioMapper.java` en `mapper/`.
- `UsuarioRestController.java` en `controller/rest/` mapeado a `/api/v1/usuarios`.
- La configuración de seguridad actual permite llamar a `/api/v1/**` sin autenticación porque en la Sesión 1 añadimos `.requestMatchers("/api/v1/**").permitAll()` y desactivamos CSRF en `/api/**`.
- **El reto de la Sesión 2:** Reemplazar ese `permitAll()` temporal por una arquitectura de seguridad real basada en tokens JWT firmados, con roles y permisos finos (`@PreAuthorize`), cerrando la API a clientes anónimos sin alterar el login web HTML existente en Thymeleaf.

---

## Micro-Ciclo 1: Arquitectura Stateless y Anatomía Criptográfica de JWT (RFC 7519)

### 1.A. Guía Teórica (Slides 1 a 8 — 18 min)
- **Contraste Stateful vs Stateless (Slides 4 y 6):**
  - **Stateful (MVC):** Tomcat almacena el `SecurityContext` en memoria RAM indexado por la cookie `JSESSIONID`. Implica alto consumo de memoria por usuario y dificulta el escalamiento horizontal entre múltiples réplicas detrás de un balanceador.
  - **Stateless (REST):** El servidor **no crea sesiones HTTP en RAM** (`SessionCreationPolicy.STATELESS`). La identidad viaja en cada solicitud contenida en un token autoportante firmado en el encabezado `Authorization: Bearer <token>`.
- **Anatomía de un JSON Web Token (RFC 7519 — Slide 5):**
  1. **Header (Metadatos):** Algoritmo (`alg`: HS256) y tipo (`typ`: JWT) codificado en Base64URL.
  2. **Payload (Claims):** Atributos de identidad (`sub`: subject/email), timestamps (`iat`: issued-at, `exp`: expiration) y reclamos personalizados (`authorities`). Codificado en Base64URL.
  3. **Signature (Integridad criptográfica):**
     $$\text{HMAC-SHA256}(\text{Base64URL}(H) + \text{"."} + \text{Base64URL}(P), \text{secretKey})$$
- **Regla Fundamental:** Un token JWS estándar está **firmado, no encriptado**. Es 100% legible al decodificar Base64URL (demostrable en `jwt.io`). La firma no oculta la información, sino que certifica que el contenido no ha sido alterado.
- **Algoritmos de Firma (Slide 7):** Diferencia entre HS256 (simétrico, clave secreta compartida única, ideal para monolitos y servicios autocontenidos) vs RS256 (asimétrico, clave privada para firma y clave pública para verificación en microservicios y OAuth2/OIDC).

### 1.B. Práctica en Vivo — Paso 2.1: Dependencias JJWT 0.12.6 y Propiedades (Slide 8 — 12 min)

#### 1. Añadir dependencias en `springboot/pom.xml`
Añade la propiedad `<jjwt.version>0.12.6</jjwt.version>` en `<properties>` y las 3 dependencias de JJWT:

```xml
<properties>
    <!-- ... otras propiedades ... -->
    <jjwt.version>0.12.6</jjwt.version>
</properties>
```

Dentro de `<dependencies>`:
```xml
<!-- 1. JJWT API: Interfaces públicas para compilación -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>${jjwt.version}</version>
</dependency>

<!-- 2. JJWT Impl: Implementación criptográfica oficial (Runtime) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>${jjwt.version}</version>
    <scope>runtime</scope>
</dependency>

<!-- 3. JJWT Jackson: Serializador JSON para claims (Runtime) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>${jjwt.version}</version>
    <scope>runtime</scope>
</dependency>
```

#### 2. Configurar `src/main/resources/application.properties`
Añade las propiedades del token criptográfico:

```properties
# ==========================================
# CONFIGURACIÓN CRIPTOGRÁFICA JWT (HMAC-SHA256)
# ==========================================
# Clave secreta codificada en Base64 (mínimo 256 bits / 32 bytes requeridos por RFC 7519)
jwt.secret=dGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JKV1RBdXRoZW50aWNhdGlvbkluU3ByaW5nQm9vdDIwMjY=
# Tiempo de expiración del token en milisegundos (3600000 ms = 1 hora)
jwt.expiration=3600000
```

---

## Micro-Ciclo 2: Flujo de Autenticación, Emisión de Tokens y Login REST (`POST /api/v1/auth/login`)

### 2.A. Guía Teórica (Slides 9 y 10 — 12 min)
- **¿Cómo Nace un Token JWT? (Slide 10):**
  Antes de hablar de filtros o de cómo proteger endpoints, el estudiante debe entender cómo se genera el primer token:
  1. El cliente (Postman o frontend SPA) envía `POST /api/v1/auth/login` con credenciales JSON (`correoInstitucional`, `password`).
  2. `AuthRestController` no valida contraseñas manualmente; delega en el bean central `AuthenticationManager` de Spring Security mediante `authManager.authenticate(new UsernamePasswordAuthenticationToken(email, pass))`.
  3. El `DaoAuthenticationProvider` llama a `UserDetailsService.loadUserByUsername(email)` para consultar el usuario en la BD (recuperando el hash BCrypt y la colección de roles/permisos).
  4. Compara la contraseña en texto plano recibida con el hash BCrypt usando `passwordEncoder.matches()`. Si no coincide, lanza `BadCredentialsException` (retorna `401 Unauthorized`).
  5. Si las credenciales son válidas, retorna un objeto `Authentication` autenticado cuyo `principal` es el `UserDetails`.
  6. `AuthRestController` pasa ese `UserDetails` a `jwtService.generateToken(userDetails)`:
     - Extrae la lista de authorities usando Java Stream: `.map(GrantedAuthority::getAuthority).toList()`.
     - Inyecta los claims (`subject: email`, `iat: now`, `exp: now + 1h`, `authorities: [...]`).
     - Firma criptográficamente con `Jwts.SIG.HS256` y la clave secreta `jwt.secret`.
  7. Retorna `200 OK` con `TokenResponseDTO` conteniendo el token JWT, el prefijo `"Bearer"` y el tiempo de expiración.
- **Punto Clave Pedagógico:** Este endpoint debe ser público (`permitAll()`), ya que es el punto de partida donde el cliente aún no posee ningún token.

### 2.B. Práctica en Vivo — Paso 2.2: DTOs, Emisión en `JwtService`, `AuthRestController` y Verificación en Postman (Slides 11 y 12 — 18 min)

#### Archivo 1: `src/main/java/com/compunet/springboot/dto/Auth/LoginRequestDTO.java`
```java
package com.compunet.springboot.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    private String correoInstitucional;
    private String password;
}
```

#### Archivo 2: `src/main/java/com/compunet/springboot/dto/Auth/TokenResponseDTO.java`
```java
package com.compunet.springboot.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {
    private String token;
    private String type;       // "Bearer"
    private long expiresIn;    // 3600000
    private String correoInstitucional;
}
```

#### Archivo 3: Interfaz `src/main/java/com/compunet/springboot/security/jwt/IJwtService.java`
```java
package com.compunet.springboot.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    long getExpirationTime();
}
```

#### Archivo 4: `src/main/java/com/compunet/springboot/security/jwt/JwtServiceImpl.java` (Parte de Emisión)
```java
package com.compunet.springboot.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements IJwtService {

    @Value("${jwt.secret}")
    private String secretKeyBase64;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Extraer lista de roles/permisos usando Java Streams API
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        extraClaims.put("authorities", authorities);

        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public long getExpirationTime() {
        return jwtExpirationMs;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Los métodos de verificación (extractUsername, isTokenValid) se completarán en el Micro-Ciclo 3
    @Override
    public String extractUsername(String token) { return null; }
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) { return false; }
}
```

#### Archivo 5: Exponer `AuthenticationManager` en `src/main/java/com/compunet/springboot/config/SecurityConfig.java`
Para poder inyectar `AuthenticationManager` en `AuthRestController`, añade este `@Bean` en `SecurityConfig`:

```java
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

// Dentro de public class SecurityConfig:
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

#### Archivo 6: `src/main/java/com/compunet/springboot/controller/rest/AuthRestController.java`
```java
package com.compunet.springboot.controller.rest;

import com.compunet.springboot.dto.Auth.LoginRequestDTO;
import com.compunet.springboot.dto.Auth.TokenResponseDTO;
import com.compunet.springboot.security.jwt.IJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCorreoInstitucional(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = jwtService.generateToken(userDetails);

            TokenResponseDTO response = TokenResponseDTO.builder()
                    .token(jwtToken)
                    .type("Bearer")
                    .expiresIn(jwtService.getExpirationTime())
                    .correoInstitucional(userDetails.getUsername())
                    .build();

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
```

#### 7. Verificación Inmediata en Vivo (Slide 12)
1. Iniciar la aplicación Spring Boot.
2. Abrir Postman y crear la petición:
   - **Método:** `POST`
   - **URL:** `http://localhost:8080/springboot-api/api/v1/auth/login`
   - **Headers:** `Content-Type: application/json`
   - **Body (raw JSON):**
     ```json
     {
       "correoInstitucional": "admin@icesi.edu.co",
       "password": "admin123"
     }
     ```
3. Ejecutar: El servidor responde `200 OK` con el token generado.
4. **Inspección en `jwt.io`:** Copiar el token recibido y pegarlo en [https://jwt.io](https://jwt.io). Mostrar a los estudiantes cómo se decodifica en vivo:
   - **Header:** `{"alg": "HS256", "typ": "JWT"}`
   - **Payload:** `sub: "admin@icesi.edu.co"`, `authorities: ["ROLE_ADMINISTRADOR", "USER_READ", "USER_CREATE", ...]`, `iat`, `exp`.
   - **Signature:** Verificada cuando se ingresa la clave secreta.

---

## Micro-Ciclo 3: Flujo de Autorización: `OncePerRequestFilter`, Verificación y `SecurityFilterChain` Dual

### 3.A. Guía Teórica (Slides 13 a 16, 18 — 15 min)
- **La Gran Pregunta (Slide 13):**
  *"Ya tenemos un token en Postman. Ahora, ¿cómo lo consume el cliente para acceder a endpoints protegidos como `/api/v1/usuarios` y cómo hace el servidor para verificar que no haya sido alterado ni haya expirado?"*
- **Pipeline de Intercepción y `OncePerRequestFilter` (Slides 14 y 15):**
  - Antes de llegar al `DispatcherServlet`, cada petición HTTP pasa por la cadena de filtros.
  - Intercalamos `JwtAuthenticationFilter` **antes** de `UsernamePasswordAuthenticationFilter`.
  - ¿Por qué extender `OncePerRequestFilter`? Un filtro estándar de Jakarta Servlet puede ejecutarse varias veces en despachos internos (`RequestDispatcher.forward` o errores `/error`). `OncePerRequestFilter` garantiza **estrictamente una sola ejecución por petición HTTP**.
  - **Regla de Continuidad:** Si no hay header `Authorization: Bearer`, el filtro **no bloquea**: invoca `chain.doFilter(req, res)` para que rutas públicas como `/api/v1/auth/login` pasen libremente.
- **Verificación Criptográfica en JJWT 0.12+ (Slide 16):**
  - `parseSignedClaims(token)` recalculando el HMAC con `getSigningKey()`. Si un atacante modificó un solo carácter del payload, se dispara `SignatureException`. Si expiró, se dispara `ExpiredJwtException`.
- **Coexistencia Arquitectónica Dual (Slide 18):**
  - `@Order(1) apiSecurityFilterChain`: Exclusivo para `/api/**`. `STATELESS`, CSRF deshabilitado, maneja errores 401/403 en JSON.
  - `@Order(2) mvcSecurityFilterChain`: Captura el resto de rutas (`/**`). Mantiene sesiones `JSESSIONID`, protección CSRF para formularios Thymeleaf y redirecciones HTML al `/login`.

### 3.B. Práctica en Vivo — Paso 2.3: Verificación Criptográfica, Filtro y Dual Chain (Slides 16, 17, 19 — 20 min)

#### 1. Completar métodos de verificación en `JwtServiceImpl.java`
Añade los métodos en `src/main/java/com/compunet/springboot/security/jwt/JwtServiceImpl.java`:

```java
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
```

#### 2. Crear `src/main/java/com/compunet/springboot/security/jwt/JwtAuthenticationFilter.java`
```java
package com.compunet.springboot.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Paso 1: Si no hay header Authorization o no comienza con Bearer, continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Paso 2: Extraer el token (eliminar el prefijo "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // Paso 3: Extraer username (correo) verificando firma y vigencia criptográfica
            final String userEmail = jwtService.extractUsername(jwt);

            // Paso 4: Si se extrajo el usuario y el hilo actual no tiene autenticación previa
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // Paso 5: Validar correspondencia e inyectar en el contexto de seguridad
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            // Si el token es inválido, manipulado o expiró, garantizamos que el contexto quede limpio
            SecurityContextHolder.clearContext();
        }

        // Paso 6: Continuar la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
```

#### 3. Refactorizar `src/main/java/com/compunet/springboot/config/SecurityConfig.java` con Dos Cadenas
Actualiza `SecurityConfig` para declarar las dos cadenas independientes con `@Order`:

```java
package com.compunet.springboot.config;

import com.compunet.springboot.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
     * CADENA 1 (@Order(1)): Seguridad Stateless con JWT exclusiva para la API REST (/api/**)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
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
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CADENA 2 (@Order(2)): Seguridad Stateful con Cookies JSESSIONID para vistas Thymeleaf MVC
     */
    @Bean
    @Order(2)
    public SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/public/**").permitAll()
                .requestMatchers("/login", "/access-denied").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/roles-mvc/**", "/permisos-mvc/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/profesores-mvc/**").hasAnyRole("ADMINISTRADOR", "PROFESOR")
                .requestMatchers("/usuarios/**", "/cursos-mvc/**", "/matriculas-mvc/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/usuarios", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
```

---

## Micro-Ciclo 4: Escenarios Críticos en Producción y Laboratorio Integral

### 4.A. Guía Teórica (Slides 20 a 22 — 15 min)
- **Análisis de 3 Escenarios Críticos en Producción (Slide 21):**
  1. **Robo de Token Vigente (El Riesgo Stateless):** Si un atacante roba un JWT válido (mediante un ataque XSS en el frontend), el servidor lo aceptará hasta que llegue su fecha `exp`. **Mitigación:** Emitir *Access Tokens* de vida corta (5–15 min) + *Refresh Tokens* en cookies `HttpOnly` o listas de revocación en Redis.
  2. **Manipulación del Payload (Escalación de Privilegios):** Un estudiante decodifica su JWT en Base64, cambia `"ROLE_ESTUDIANTE"` por `"ROLE_ADMINISTRADOR"` y lo reenvía. **Defensa:** Al no conocer la `jwt.secret`, el HMAC-SHA256 calculado fallará inmediatamente (`SignatureException` $\rightarrow$ `401 Unauthorized`).
  3. **Stale Claims e Hidratación BD (Slide 22):** Si desactivamos una cuenta (`active = false`) o revocamos un permiso en la BD mientras el usuario tiene un token con 45 min restantes de vida:
     - Si la aplicación confiara 100% en los claims del token sin consultar BD, el usuario seguiría operando.
     - En nuestro diseño, al consultar `loadUserByUsername(email)` en `JwtAuthenticationFilter`, obtenemos el estado y authorities frescos de la BD en cada petición, bloqueando de inmediato al usuario sin perder el carácter stateless web.

### 4.B. Práctica en Vivo — Paso 2.4: Laboratorio Final de 5 Pruebas en Postman (Slide 23 — 15 min)

Ejecuta estas 5 pruebas en vivo para demostrar el funcionamiento integral de la arquitectura:

1. **Prueba 1 — Petición sin Token (`401 Unauthorized` en JSON):**
   - Enviar `GET http://localhost:8080/springboot-api/api/v1/usuarios` sin encabezado `Authorization`.
   - **Resultado:** Código HTTP `401` y cuerpo:
     ```json
     {"error": "No autenticado: Token JWT ausente, invalido o expirado"}
     ```
     *(Verifica que ya no ocurre redirección HTML a `/login`).*

2. **Prueba 2 — Autenticación como Estudiante e Inspección de Claims en `jwt.io`:**
   - Enviar `POST http://localhost:8080/springboot-api/api/v1/auth/login` con body:
     ```json
     {
       "correoInstitucional": "apaez@icesi.edu.co",
       "password": "estudiante123"
     }
     ```
   - **Resultado:** Código HTTP `200 OK` con `TokenResponseDTO`.
   - Copiar el token recibido e inspeccionarlo en [https://jwt.io](https://jwt.io). Demostrar el `sub` y las `authorities: ["ROLE_ESTUDIANTE", "COURSE_READ", "ENROLLMENT_READ", "ENROLLMENT_WRITE"]`.

3. **Prueba 3 — Control de Acceso: 403 Forbidden por Permisos Insuficientes:**
   - Usar el token del estudiante como cabecera `Authorization: Bearer <token_estudiante>`.
   - Enviar `GET http://localhost:8080/springboot-api/api/v1/usuarios`.
   - **Resultado:** Código HTTP `403 Forbidden` (`{"error": "Acceso denegado: No tienes permisos suficientes para este recurso"}`), debido a que `UsuarioRestController` exige `@PreAuthorize("hasAuthority('USER_READ')")`.

4. **Prueba 4 — Autenticación de Administrador y Operación Completa de la API:**
   - Enviar `POST http://localhost:8080/springboot-api/api/v1/auth/login` con `admin@icesi.edu.co` / `admin123`.
   - Usar el nuevo token (`Authorization: Bearer <token_admin>`):
     - `GET http://localhost:8080/springboot-api/api/v1/usuarios?active=true` $\rightarrow$ `200 OK` (JSON con lista de `UsuarioResponseDTO`, sin campo `password`).
     - `POST http://localhost:8080/springboot-api/api/v1/usuarios` $\rightarrow$ `201 Created` con cabecera `Location`.
     - `PATCH http://localhost:8080/springboot-api/api/v1/usuarios/3/estado?active=false` $\rightarrow$ `200 OK`.

5. **Prueba 5 — Demostración de Sabotaje de Token (Firma Alterada $\rightarrow$ `401 Unauthorized`):**
   - Tomar el token válido de Administrador y alterar un solo carácter en la sección de Payload antes de enviarlo.
   - **Resultado:** JJWT detecta la discrepancia en el HMAC-SHA256, lanza `SignatureException` en `JwtAuthenticationFilter`, limpia el contexto y la API responde `401 Unauthorized`.

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
| **Ciclo 2** | `00:30 - 00:55` (25 min) | **Slides 9–11:** Arquitectura del servicio criptográfico `JwtService`: Generación de firma HMAC-SHA256 con `Keys.hmacShaKeyFor`, inyección de Claims personalizados (`authorities`) y validación de expiración/firma con `parseSignedClaims`. | **Paso 2.2:** Creación de la interfaz `IJwtService.java` y su implementación `JwtServiceImpl.java` basada en el contrato estándar `UserDetails`. |
| **Ciclo 3** | `00:55 - 01:30` (35 min) | **Slides 12–17:** Cadena de Filtros de Spring Security, ¿Por qué extender `OncePerRequestFilter`?, Algoritmo de intercepción de `JwtAuthenticationFilter` y Coexistencia Arquitectónica Dual (`@Order(1)` para `/api/**` vs `@Order(2)` para `/**`). | **Paso 2.3:** Implementación de `JwtAuthenticationFilter.java` y refactorización de `SecurityConfig.java` con dos cadenas `SecurityFilterChain` independientes. |
| **Ciclo 4** | `01:30 - 02:00` (30 min) | **Slides 18–24:** Flujo del ciclo de vida de autenticación `POST /api/v1/auth/login`, delegación en `AuthenticationManager`, y Análisis de 3 Escenarios Críticos en Producción (Robo de Token, Falsificación de Firma `401`, *Stale Claims* e Hidratación BD). | **Paso 2.4:** Implementación de `LoginRequestDTO`, `TokenResponseDTO`, `AuthRestController` y ejecución del laboratorio en vivo de 5 pruebas en Postman + `jwt.io`. |

---

## 2. Contexto de Entrada de la Sesión 2

Al iniciar esta clase, el proyecto ya cuenta con los contratos y controladores REST construidos durante la Sesión 1:
- `UsuarioRequestDTO.java` y `UsuarioResponseDTO.java` en `dto/`.
- `UsuarioMapper.java` en `mapper/`.
- `UsuarioRestController.java` en `controller/rest/` mapeado a `/api/v1/usuarios`.
- La configuración de seguridad actual redirige cualquier petición no autenticada hacia `/login` (HTML), generando una experiencia incompatible con clientes API REST (móviles, SPAs o Postman).

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
jwt.secret=NTRmZjg5YzI4NzRkNDllY2I5YzA3ZjM4ZTVhMWI2YzRkOGU5ZjBhMWIyYzNkNGU1ZjZhN2I4YzlkMGUxZjJhMw==
# Tiempo de expiración del token en milisegundos (3600000 ms = 1 hora)
jwt.expiration=3600000
```

---

## Micro-Ciclo 2: Servicio Criptográfico `JwtService` (JJWT 0.12+)

### 2.A. Guía Teórica (Slides 9 a 11 — 10 min)
- **Responsabilidad Única:** Encapsular la construcción, firma, decodificación y validación matemática de tokens.
- **Emisión con Fluent API:** `Jwts.builder().claims(...).subject(...).issuedAt(...).expiration(...).signWith(key, Jwts.SIG.HS256).compact()`.
- **Verificación Criptográfica:** `Jwts.parser().verifyWith(key).build().parseSignedClaims(token)`.
  - Si un atacante altera un solo carácter del payload, se dispara `SignatureException`.
  - Si la hora actual supera `exp`, se dispara `ExpiredJwtException`.
- **Extracción Funcional:** Empleo de referencias a métodos y `Function<Claims, T>` para resolver claims de manera tipada (`Claims::getSubject`, `Claims::getExpiration`).

### 2.B. Práctica en Vivo — Paso 2.2: Implementación de `IJwtService` y `JwtServiceImpl` (Slide 10 y 11 — 15 min)

#### Archivo 1: `src/main/java/co/edu/icesi/next/springboot/security/jwt/IJwtService.java`
Crea el paquete `security.jwt` y define la interfaz desacoplada:

```java
package co.edu.icesi.next.springboot.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface IJwtService {
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    long getExpirationTime();
}
```

#### Archivo 2: `src/main/java/co/edu/icesi/next/springboot/security/jwt/JwtServiceImpl.java`
Implementa el servicio usando la API moderna de JJWT 0.12+:

```java
package co.edu.icesi.next.springboot.security.jwt;

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
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public long getExpirationTime() {
        return jwtExpirationMs;
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

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

## Micro-Ciclo 3: Filtros, `OncePerRequestFilter` y `SecurityFilterChain` Dual

### 3.A. Guía Teórica (Slides 12 a 16 — 15 min)
- **Pipeline de Intercepción (Slide 13):** Antes de ingresar al `DispatcherServlet`, la petición HTTP transita por la cadena de filtros de Spring Security (`SecurityFilterChain`).
- **Garantía de Ejecución Única (`OncePerRequestFilter` — Slide 14):**
  - Un filtro Jakarta Servlet estándar puede invocarse varias veces en despachos internos (`RequestDispatcher.forward` o redirecciones de manejo de error `/error`).
  - `OncePerRequestFilter` marca un atributo en la solicitud HTTP para garantizar que `doFilterInternal()` corra **estrictamente una sola vez por petición entrante**.
  - Si la petición no contiene encabezado Bearer, no se aborta la ejecución: se invoca `chain.doFilter(request, response)` para permitir el paso a rutas públicas como login.
- **Coexistencia Arquitectónica Dual (Slide 16):**
  - `@Bean @Order(1) apiSecurityFilterChain`: Aplica exclusivamente a `/api/**` mediante `.securityMatcher("/api/**")`. Configura `STATELESS`, deshabilita CSRF (inútil al no existir cookies de sesión) e intercala `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`. Responde en JSON con `401 Unauthorized` y `403 Forbidden`.
  - `@Bean @Order(2) mvcSecurityFilterChain`: Aplica al resto de rutas web (`/**`). Mantiene sesiones `JSESSIONID`, protección CSRF para formularios Thymeleaf y redirecciones HTML al login `/login`.

### 3.B. Práctica en Vivo — Paso 2.3: `JwtAuthenticationFilter` y `SecurityConfig` Dual (Slides 15 y 17 — 20 min)

#### Archivo 1: `src/main/java/co/edu/icesi/next/springboot/security/jwt/JwtAuthenticationFilter.java`
Implementa el filtro de intercepción:

```java
package co.edu.icesi.next.springboot.security.jwt;

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
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Paso 1: Si no hay header Authorization o no comienza con Bearer, continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Paso 2: Extraer el token
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
            // Si el token es inválido o expiró, garantizamos que el contexto quede limpio
            SecurityContextHolder.clearContext();
        }

        // Paso 6: Continuar la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
```

#### Archivo 2: Actualizar `src/main/java/co/edu/icesi/next/springboot/config/SecurityConfig.java`
Configura las dos cadenas con orden de precedencia:

```java
package co.edu.icesi.next.springboot.config;

import co.edu.icesi.next.springboot.security.jwt.JwtAuthenticationFilter;
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
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/403")
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

## Micro-Ciclo 4: Endpoint de Login (`/api/v1/auth/login`) y Escenarios de Seguridad en Producción

### 4.A. Guía Teórica (Slides 18 a 22 — 15 min)
- **Flujo de Login (Slides 19 y 20):**
  1. Cliente envía credenciales en `LoginRequestDTO`.
  2. `AuthenticationManager` valida contra la BD mediante `DaoAuthenticationProvider` y `BCryptPasswordEncoder`.
  3. `JwtService` emite el token firmado con `expiresIn` en milisegundos y lo devuelve en `TokenResponseDTO`.
- **Análisis de 3 Escenarios Críticos en Producción (Slides 21–22):**
  1. **Robo de Token Vigente:** En caso de filtración de un token, el atacante tiene acceso hasta su expiración. **Mitigación:** Access Tokens con tiempo de expiración corto (15 min) + Refresh Tokens en cookies HttpOnly o listas negras en Redis.
  2. **Manipulación del Payload (Escalación de Privilegios):** Si un atacante modifica `"ROLE_ESTUDIANTE"` por `"ROLE_ADMINISTRADOR"`, la firma no cuadrará sin conocer `jwt.secret` y la petición será rechazada con `401`.
  3. **Stale Claims e Hidratación BD (Slide 22):** Si un usuario es desactivado o sus permisos cambian en la BD, la rehidratación en `loadUserByUsername(email)` dentro del filtro garantiza revocación inmediata de privilegios sin esperar a que el token expire.

### 4.B. Práctica en Vivo — Paso 2.4: DTOs, Controlador y Laboratorio de Pruebas (Slides 20 y 23 — 15 min)

#### Archivo 1: `src/main/java/co/edu/icesi/next/springboot/dto/LoginRequestDTO.java`
```java
package co.edu.icesi.next.springboot.dto;

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

#### Archivo 2: `src/main/java/co/edu/icesi/next/springboot/dto/TokenResponseDTO.java`
```java
package co.edu.icesi.next.springboot.dto;

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
    private String type;
    private long expiresIn;
    private String correoInstitucional;
}
```

#### Archivo 3: `src/main/java/co/edu/icesi/next/springboot/controller/rest/AuthRestController.java`
```java
package co.edu.icesi.next.springboot.controller.rest;

import co.edu.icesi.next.springboot.dto.LoginRequestDTO;
import co.edu.icesi.next.springboot.dto.TokenResponseDTO;
import co.edu.icesi.next.springboot.security.jwt.IJwtService;
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

---

## 3. Laboratorio Final en Vivo: 5 Pruebas en Postman y `jwt.io` (Slide 23)

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

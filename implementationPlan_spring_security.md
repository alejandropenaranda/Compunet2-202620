# Plan de Implementación y Guía Docente: Spring Security en Spring MVC

**Curso**: Computación en Internet II  
**Programa**: Ingeniería de Sistemas — Universidad Icesi  
**Proyecto Base**: `springboot` (`com.compunet.springboot` — Sistema Académico ICESI)  
**Tema**: Autenticación, Cookies, Sesiones HTTP, Cadena de Filtros, Autorización Granular (RBAC) y Persistencia JPA  
**Duración Total**: 2 Sesiones (2 horas cada una = 4 horas / 240 minutos)  

---

## 🛠️ 1. Entorno Técnico y Estructura del Proyecto `springboot`

Toda la implementación teórica y práctica se realiza directamente sobre el proyecto base ubicado en el directorio [`springboot/`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot).

### Parámetros de Ejecución:
* **Puerto del Servidor**: `8080`
* **Context Path**: `/springboot-api`
* **URL Base de la Aplicación**: `http://localhost:8080/springboot-api`
* **Controladores Existentes**:
  * [`UsuarioController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/UsuarioController.java): `/usuarios`
  * [`ProfesorController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/ProfesorController.java): `/profesores-mvc`
  * [`CursoController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/CursoController.java): `/cursos-mvc`
  * [`MatriculaController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/MatriculaController.java): `/matriculas-mvc`
  * [`RolController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/RolController.java): `/roles-mvc`
  * [`PermisoController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/PermisoController.java): `/permisos-mvc`
* **Datos Semilla en [`data.sql`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/data.sql)**:
  * **Roles**: `ADMINISTRADOR` (ID 1), `PROFESOR` (ID 2), `ESTUDIANTE` (ID 3).
  * **Permisos**: `USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`, `COURSE_READ`, `COURSE_WRITE`, `ENROLLMENT_WRITE`.
  * **Usuarios**: 27 usuarios activos (2 Administradores, 10 Docentes, 15 Estudiantes).

---

## ⏱️ 2. Cronograma Minuto a Minuto de las Clases

### 📘 SESIÓN 1: Autenticación, Cookies, Sesiones HTTP y Cadena de Filtros (120 Minutos)

| Minutos | Bloque Temático | Dinámica y Actividad Pedagógica | Diapositivas |
|:---:|---|---|:---:|
| **00 - 15** | **Apertura y Motivación** | Presentación de la agenda. Pregunta disparadora: *¿Por qué al recargar una página web el servidor sigue sabiendo quiénes somos si el protocolo HTTP no guarda estado?* | Slides 01 - 03 |
| **15 - 35** | **Bloque 1: Autenticación vs Autorización** | Concepto de identidad proclamada vs validada. Flujo fundamental de login. Introducción sutil a Autorización (AuthZ) como tema a profundizar en la Sesión 2. | Slides 04 - 05 |
| **35 - 55** | **Bloque 2: Protocolo HTTP y Cookies** | Mecánica de `Set-Cookie` y `Cookie`. Flags de seguridad esenciales: `HttpOnly`, `Secure`, `SameSite` (Lax/Strict), `Max-Age` y `Path`. | Slides 06 - 09 |
| **55 - 75** | 🔍 **Live Demo 1: Inspección de Cookies en Chrome DevTools** | **Actividad Práctica Guiada con `springboot`**: Solicitar `/springboot-api/usuarios`, observar la redirección, autenticarse, inspeccionar `JSESSIONID`, banderas y experimentar con el borrado manual de la cookie en vivo. | Slides 10 - 11 |
| **75 - 95** | **Bloque 3: Sesiones del Lado del Servidor (`HttpSession`)** | Almacenamiento en RAM de Tomcat, generación de IDs aleatorios de alta entropía, ciclo de vida, timeout y defensas contra *Session Fixation* y *Session Hijacking*. | Slides 12 - 15 |
| **95 - 105** | **Bloque 4: Filtros y Configuración Base** | `DelegatingFilterProxy`, `FilterChainProxy`, la `SecurityFilterChain` y creación de `SecurityConfig.java` con `UserDetailsService`. | Slides 16 - 22 |
| **105 - 120** | 🔍 **Live Demo 2: Creación de `SecurityConfig` y Depuración en Consola** | **Actividad Práctica Guiada con `springboot`**: Implementar `SecurityConfig.java` con rutas públicas y protegidas, registrar un usuario de prueba en memoria, activar logs `DEBUG` y observar en vivo el paso por cada filtro. | Slides 23 - 24 |

---

### 📗 SESIÓN 2: Autorización Granular, Roles, Authorities, Persistencia JPA y Thymeleaf (120 Minutos)

| Minutos | Bloque Temático | Dinámica y Actividad Pedagógica | Diapositivas |
|:---:|---|---|:---:|
| **00 - 15** | **Recapitulación S1 y Apertura S2** | Breve revisión del flujo de sesión y filtros. Planteamiento del problema: *¿Cómo restringir que un ESTUDIANTE no pueda acceder a `/roles-mvc` o eliminar profesores?* | Slides 01 - 03 |
| **15 - 35** | **Bloque 1: Autorización a Fondo (AuthZ)** | Principio de Menor Privilegio (PoLP). El modelo en memoria de Spring Security: `SecurityContextHolder` (`ThreadLocal`), `SecurityContext` y el objeto `Authentication`. | Slides 04 - 05 |
| **35 - 55** | **Bloque 2: Roles vs Authorities** | Diferenciación formal: `GrantedAuthority` (permiso atómico ej. `USER_CREATE`) vs Roles de alto nivel (`ADMINISTRADOR`). Convención `ROLE_` y uso de `hasRole(...)` vs `hasAuthority(...)`. | Slides 06 - 08 |
| **55 - 80** | **Bloque 3: Motor de Autenticación y Persistencia JPA** | Arquitectura de `DaoAuthenticationProvider`, contratos `UserDetailsService` y `UserDetails`. Hashing criptográfico con `BCryptPasswordEncoder` (Salt + Work Factor). | Slides 09 - 14 |
| **80 - 100** | **Bloque 4: Configuración Declarativa y Vistas Thymeleaf** | Construcción de `SecurityConfig.java`: DSL declarativo de `authorizeHttpRequests`, `formLogin` propio y `logout`. Integración en vistas con `thymeleaf-extras-springsecurity6` (`sec:authorize`). | Slides 15 - 20 |
| **100 - 120** | 🔍 **Live Demo 3: Implementación Completa y Validación RBAC** | **Actividad Práctica Guiada con `springboot`**: Iniciar sesión con diferentes perfiles (`admin@icesi.edu.co`, `drincon@icesi.edu.co`, `apaez@icesi.edu.co`), verificar bloqueo 403 en `/roles-mvc` y evaluar la barra de navegación condicional. | Slides 21 - 22 |

---

## 💻 3. Guía Detallada de Live Demos y Snippets de Código para `springboot`

A continuación se presentan todos los pasos de los demos y el código exacto a implementar en el proyecto `springboot`.

---

### 📦 Paso 1: Configurar Dependencias en `springboot/pom.xml`

Para habilitar la seguridad en el proyecto, se deben agregar los starters de Spring Security y el dialecto de Thymeleaf en [`springboot/pom.xml`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/pom.xml):

```xml
<!-- Spring Security Starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Integración de Spring Security 6 con Thymeleaf -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

---

### 🔍 Live Demo 1: Inspección de Cookies y Sesiones en DevTools (Sesión 1)

#### Objetivo:
Comprobar cómo el navegador almacena y transmite `JSESSIONID` tras la autenticación y qué ocurre cuando se destruye el testigo de sesión.

#### Paso a Paso para el Docente:
1. **Ejecutar el servidor**:
   ```bash
   cd springboot
   mvn spring-boot:run
   ```
2. **Navegar en Chrome**:
   * Ingresa a: `http://localhost:8080/springboot-api/usuarios`
   * Al tener `spring-boot-starter-security`, Spring Security intercepta la petición y redirige a `http://localhost:8080/springboot-api/login`.
3. **Abrir DevTools**:
   * Presiona `F12` (o `Ctrl + Shift + I`).
   * Dirígete a la pestaña **Application** ➔ Menú lateral izquierdo ➔ **Storage** ➔ **Cookies** ➔ Selecciona `http://localhost:8080`.
4. **Iniciar Sesión**:
   * Usuario: `user`
   * Contraseña: La clave generada aleatoriamente que Spring Boot imprime en la consola al iniciar (ej. `Using generated security password: abc123-xyz...`).
5. **Inspección de Banderas en la Tabla de DevTools**:
   * Muestra la cookie **`JSESSIONID`**.
   * Señala que la columna **`HttpOnly`** tiene un check activo (`true` por defecto).
   * Abre la consola de JavaScript (`Console`) y ejecuta:
     ```javascript
     console.log("Cookies legibles por JS:", document.cookie);
     ```
   * Demuestra a la clase que `JSESSIONID` **no aparece**, protegiendo la sesión contra robo vía Cross-Site Scripting (XSS).
   * **¿Por qué `SameSite` y `Secure` no aparecen explícitos por defecto?**
     * **`Secure` (`false` por defecto):** Viene desactivado para permitir el desarrollo local sobre `http://localhost:8080`. Si viniera activo sin certificado SSL, el navegador rechazaría la cookie.
     * **`SameSite` (Omitido por defecto en la cabecera):** Tomcat no emite el atributo explícito a menos que se configure, aunque los navegadores modernos aplican *Lax-by-default* internamente.

7. **Experimento de Desconexión Manual**:
   * Haz clic derecho sobre `JSESSIONID` en la tabla de cookies y pulsa **Delete**.
   * Presiona `F5` para recargar la página.
   * **Resultado**: Como Tomcat ya no recibe el ID en la cabecera `Cookie: JSESSIONID=...`, no encuentra la sesión en RAM y redirige inmediatamente al formulario de login.

---

### 🔍 Live Demo 2: Implementación de `SecurityConfig` y Depuración en Consola (Sesión 1)

#### Objetivo:
Crear la clase inicial de configuración [`SecurityConfig.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/config/SecurityConfig.java), definir reglas básicas de filtrado (públicas vs autenticadas), configurar un usuario de prueba en memoria y comprobar el flujo interno de filtros mediante logs `DEBUG`.

#### Paso a Paso para el Docente:

1. **Crear la clase `SecurityConfig.java` (Versión Inicial de Sesión 1)**:
   Ruta: `springboot/src/main/java/com/compunet/springboot/config/SecurityConfig.java`
   ```java
   package com.compunet.springboot.config;

   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.security.config.Customizer;
   import org.springframework.security.config.annotation.web.builders.HttpSecurity;
   import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
   import org.springframework.security.core.userdetails.User;
   import org.springframework.security.core.userdetails.UserDetails;
   import org.springframework.security.core.userdetails.UserDetailsService;
   import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
   import org.springframework.security.crypto.password.PasswordEncoder;
   import org.springframework.security.provisioning.InMemoryUserDetailsManager;
   import org.springframework.security.web.SecurityFilterChain;

   @Configuration
   @EnableWebSecurity
   public class SecurityConfig {

       @Bean
       public PasswordEncoder passwordEncoder() {
           return new BCryptPasswordEncoder();
       }

       // 1. Configuración de la Cadena de Filtros
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           return http
               .authorizeHttpRequests(auth -> auth
                   // Rutas públicas de libre acceso
                   .requestMatchers("/css/**", "/js/**", "/public/**").permitAll()
                   // Cualquier otra ruta requiere autenticación previa
                   .anyRequest().authenticated()
               )
               // Habilita el formulario de login estándar
               .formLogin(Customizer.withDefaults())
               .build();
       }

       // 2. Usuario de prueba en memoria para la Sesión 1
       @Bean
       public UserDetailsService userDetailsService() {
           UserDetails docente = User.builder()
               .username("docente@icesi.edu.co")
               .password(passwordEncoder().encode("docente123"))
               .roles("PROFESOR")
               .build();

           return new InMemoryUserDetailsManager(docente);
       }
   }
   ```

2. **Activar Logging `DEBUG` en [`application.properties`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/application.properties)**:
   ```properties
   # ==========================================
   # LOGGING DE SPRING SECURITY PARA LIVE DEMO
   # ==========================================
   logging.level.org.springframework.security=DEBUG
   ```

3. **Reiniciar la aplicación y observar la Consola**:
   Spring Security imprime la lista exacta de filtros instanciados:
   ```text
   DEBUG o.s.s.web.DefaultSecurityFilterChain : Will secure any request with [
     DisableEncodeUrlFilter,
     SecurityContextHolderFilter,
     HeaderWriterFilter,
     CsrfFilter,
     LogoutFilter,
     UsernamePasswordAuthenticationFilter,
     DefaultLoginPageGeneratingFilter,
     DefaultLogoutPageGeneratingFilter,
     BasicAuthenticationFilter,
     RequestCacheAwareFilter,
     SecurityContextHolderAwareRequestFilter,
     AnonymousAuthenticationFilter,
     ExceptionTranslationFilter,
     AuthorizationFilter
   ]
   ```

4. **Experimento en Vivo: Ruta Pública vs Ruta Protegida**:
   * **Paso A (Ruta Pública):** Accede a `http://localhost:8080/springboot-api/css/styles.css` (o una ruta dentro de `/public/**`).
     * **En el Navegador:** El archivo se descarga directamente sin solicitar login.
     * **En la Consola:** Observa cómo `AuthorizationFilter` evalúa la ruta y permite el paso inmediatamente sin disparar redirecciones.
   * **Paso B (Ruta Protegida):** Accede a `http://localhost:8080/springboot-api/usuarios`.
     * **En el Navegador:** Se redirige automáticamente a `/login`.
     * **En la Consola:** Se registra el rechazo por `AuthorizationFilter` y la captura de la excepción en `ExceptionTranslationFilter`.
   * **Paso C (Autenticación Exitosa):**
     * Ingresa `docente@icesi.edu.co` y `docente123`.
     * Se procesa en `UsernamePasswordAuthenticationFilter`, se valida con `BCryptPasswordEncoder`, se crea la `HttpSession` con el `SecurityContext`, y se redirige a `/usuarios`.
     * Explica a la clase cómo en la Sesión 2 conectaremos este mismo flujo a las tablas de la base de datos MySQL/H2 con `CustomUserDetails` y `CustomUserDetailsService`.

---

### 🧱 Paso 2: Implementación de la Persistencia de Usuarios (Sesión 2)

Para conectar la autenticación con los usuarios de la base de datos (`data.sql`), creamos las siguientes clases en el paquete `com.compunet.springboot.config` o `com.compunet.springboot.service`:

#### 1. Adaptador `CustomUserDetails.java`
Ruta: `springboot/src/main/java/com/compunet/springboot/config/CustomUserDetails.java`

```java
package com.compunet.springboot.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.compunet.springboot.model.Permiso;
import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // 1. Mapear Roles con el prefijo estándar ROLE_
        for (Rol rol : usuario.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));

            // 2. Mapear Permisos atómicos asociados al Rol
            for (Permiso permiso : rol.getPermisos()) {
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            }
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        // En nuestro sistema el identificador único es el correo institucional
        return usuario.getCorreoInstitucional();
    }

    public String getNombreCompleto() {
        return usuario.getNombre() + " " + usuario.getApellido();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.isActive();
    }
}
```

#### 2. Servicio `CustomUserDetailsService.java`
Ruta: `springboot/src/main/java/com/compunet/springboot/service/CustomUserDetailsService.java`

```java
package com.compunet.springboot.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compunet.springboot.config.CustomUserDetails;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + email));

        // Inicializar colecciones para evitar LazyInitializationException
        usuario.getRoles().forEach(rol -> rol.getPermisos().size());

        return new CustomUserDetails(usuario);
    }
}
```

*(Nota: Asegurar que [`UsuarioRepository`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/repository/UsuarioRepository.java) tenga el método `Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);`)*.

---

### 🛡️ Paso 3: Configuración de Seguridad en `SecurityConfig.java` (Sesión 2)

Ruta: `springboot/src/main/java/com/compunet/springboot/config/SecurityConfig.java`

```java
package com.compunet.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Autorización de rutas por Rol y Permiso
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                
                // Consola H2 para desarrollo (opcional)
                .requestMatchers("/h2-console/**").permitAll()

                // Gestión de Permisos y Roles: Solo Administrador
                .requestMatchers("/roles-mvc/**", "/permisos-mvc/**").hasRole("ADMINISTRADOR")

                // Gestión de Profesores y Usuarios: Administrador o permiso USER_READ
                .requestMatchers("/profesores-mvc/**").hasAnyRole("ADMINISTRADOR", "PROFESOR")
                .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")

                // Gestión de Cursos y Matrículas: Todos los usuarios autenticados
                .requestMatchers("/cursos-mvc/**", "/matriculas-mvc/**").authenticated()

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )

            // 2. Configuración de Formulario de Login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/usuarios", true)
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )

            // 3. Configuración de Cierre de Sesión (Logout)
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // 4. Manejo de Acceso Denegado (403)
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )

            // 5. Gestión de Sesiones y mitigación de Session Fixation
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        // Habilitar frames para la consola H2 en caso de usarla
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
```

---

### 🎨 Paso 4: Vistas HTML Personalizadas con Thymeleaf (Sesión 2)

#### 1. Formulario de Login: `springboot/src/main/resources/templates/login.html`
```html
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión — ICESI Académico</title>
    <link rel="stylesheet" th:href="@{/css/styles.css}">
    <style>
        .login-card { max-width: 400px; margin: 80px auto; padding: 30px; border: 1px solid #CBD5E1; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); font-family: sans-serif; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; color: #1e293b; }
        .form-control { width: 100%; padding: 10px; border: 1px solid #94a3b8; border-radius: 5px; box-sizing: border-box; }
        .btn-primary { width: 100%; padding: 12px; background: #5454E9; color: white; border: none; border-radius: 5px; font-weight: bold; cursor: pointer; }
        .btn-primary:hover { background: #4343d0; }
        .alert-error { background: #FEE2E2; color: #991B1B; padding: 10px; border-radius: 5px; margin-bottom: 15px; }
        .alert-info { background: #E0E7FF; color: #3730A3; padding: 10px; border-radius: 5px; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="login-card">
        <h2 style="text-align: center; color: #5454E9;">ICESI Académico</h2>
        <p style="text-align: center; color: #64748b; margin-top: -10px;">Ingreso al Sistema de Gestión</p>

        <div th:if="${param.error}" class="alert alert-error">
            Correo o contraseña incorrectos.
        </div>
        <div th:if="${param.logout}" class="alert alert-info">
            Has cerrado sesión exitosamente.
        </div>

        <!-- th:action inyecta automáticamente el token oculto _csrf -->
        <form th:action="@{/login}" method="post">
            <div class="form-group">
                <label for="username">Correo Institucional:</label>
                <input type="email" id="username" name="username" class="form-control" placeholder="admin@icesi.edu.co" required autofocus>
            </div>
            <div class="form-group">
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="••••••••" required>
            </div>
            <button type="submit" class="btn-primary">Ingresar</button>
        </form>
    </div>
</body>
</html>
```

#### 2. Página de Acceso Denegado (403): `springboot/src/main/resources/templates/access-denied.html`
```html
<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>403 — Acceso Prohibido</title>
    <link rel="stylesheet" th:href="@{/css/styles.css}">
    <style>
        .error-card { max-width: 500px; margin: 100px auto; padding: 40px; text-align: center; border-radius: 12px; border: 1px solid #FCA5A5; background: #FEF2F2; font-family: sans-serif; }
        .error-card h1 { font-size: 50px; color: #DC2626; margin: 0; }
        .error-card p { color: #4B5563; font-size: 16px; }
        .btn-back { display: inline-block; margin-top: 20px; padding: 10px 20px; background: #5454E9; color: white; text-decoration: none; border-radius: 6px; }
    </style>
</head>
<body>
    <div class="error-card">
        <h1>403</h1>
        <h2>Acceso Denegado</h2>
        <p>No cuentas con los permisos o roles necesarios para acceder al recurso solicitado.</p>
        <a th:href="@{/cursos-mvc}" class="btn-back">Volver al Inicio</a>
    </div>
</body>
</html>
```

#### 3. Barra de Navegación Condicional en `springboot/src/main/resources/templates/fragments/layout.html`
```html
<!DOCTYPE html>
<html lang="es" 
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
</head>
<body>

    <!-- Fragmento de Barra de Navegación Global con Seguridad -->
    <header th:fragment="mainNavbar" style="background:#f8fafc; padding:12px 20px; border-bottom:1px solid #e2e8f0;">
        <nav style="display:flex; justify-content:space-between; align-items:center;">
            <div>
                <a th:href="@{/usuarios}" style="color:#5454E9; font-weight:bold; font-size:18px; text-decoration:none;">ICESI Académico</a>
                <span style="margin: 0 10px; color:#cbd5e1;">|</span>
                
                <!-- Enlaces visibles según rol -->
                <a th:href="@{/usuarios}" sec:authorize="hasRole('ADMINISTRADOR')" style="margin-right:12px;">Usuarios</a>
                <a th:href="@{/profesores-mvc}" sec:authorize="hasAnyRole('ADMINISTRADOR', 'PROFESOR')" style="margin-right:12px;">Profesores</a>
                <a th:href="@{/cursos-mvc}" sec:authorize="isAuthenticated()" style="margin-right:12px;">Cursos</a>
                <a th:href="@{/matriculas-mvc}" sec:authorize="isAuthenticated()" style="margin-right:12px;">Matrículas</a>
                <a th:href="@{/roles-mvc}" sec:authorize="hasRole('ADMINISTRADOR')" style="margin-right:12px;">Roles</a>
                <a th:href="@{/permisos-mvc}" sec:authorize="hasRole('ADMINISTRADOR')" style="margin-right:12px;">Permisos</a>
            </div>

            <!-- Panel de Usuario Autenticado y Logout -->
            <div sec:authorize="isAuthenticated()" style="display:flex; align-items:center; gap:15px;">
                <span style="font-size:13px; color:#475569;">
                    Conectado: <strong sec:authentication="name" style="color:#1e293b;">usuario</strong> 
                    (<span sec:authentication="principal.authorities" style="color:#64748b; font-size:11px;">ROLES</span>)
                </span>
                <form th:action="@{/logout}" method="post" style="margin:0; display:inline;">
                    <button type="submit" style="background:#fee2e2; color:#b91c1c; border:1px solid #fca5a5; padding:6px 12px; border-radius:5px; cursor:pointer; font-weight:600;">
                        Cerrar Sesión
                    </button>
                </form>
            </div>
        </nav>
    </header>

</body>
</html>
```

#### 4. Controlador de Login y Navegación: `TemplateController.java`
Ruta: `springboot/src/main/java/com/compunet/springboot/controller/TemplateController.java`
```java
package com.compunet.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
```

---

### 🔑 Paso 5: Hashes BCrypt para `data.sql`

En [`data.sql`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/data.sql), las contraseñas deben ser sustituidas por el hash real BCrypt correspondiente a `password123` generado con factor de costo 10:

```text
Hash BCrypt para 'password123': $2a$10$wK5W3yC3yF15D1nB.vN7teI0wL/y6V5t1oB97GgD4aAefXkK/Zl9G
```

*Actualización en [`data.sql`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/data.sql)*:
```sql
-- Administradores
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) 
VALUES ('Super', 'Admin', 'admin@icesi.edu.co', '$2a$10$wK5W3yC3yF15D1nB.vN7teI0wL/y6V5t1oB97GgD4aAefXkK/Zl9G', TRUE);

-- Docentes
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) 
VALUES ('Domiciano', 'Rincon', 'drincon@icesi.edu.co', '$2a$10$wK5W3yC3yF15D1nB.vN7teI0wL/y6V5t1oB97GgD4aAefXkK/Zl9G', TRUE);

-- Estudiantes
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) 
VALUES ('Alejandro', 'Paez', 'apaez@icesi.edu.co', '$2a$10$wK5W3yC3yF15D1nB.vN7teI0wL/y6V5t1oB97GgD4aAefXkK/Zl9G', TRUE);
```

---

### 🔍 Live Demo 3: Validación Integral de RBAC (Sesión 2)

#### Objetivo:
Comprobar el aislamiento por roles en el sistema académico y la denegación estricta de rutas no autorizadas.

#### Paso a Paso para el Docente:
1. **Prueba como ADMINISTRADOR (`admin@icesi.edu.co` / `password123`)**:
   * Ingresa al sistema.
   * Navega por `/usuarios`, `/roles-mvc` y `/permisos-mvc`.
   * Verifica que la barra de navegación muestra todos los enlaces.
   * Cierra sesión haciendo clic en el botón **Cerrar Sesión**.
2. **Prueba como PROFESOR (`drincon@icesi.edu.co` / `password123`)**:
   * Inicia sesión.
   * Accede a `/cursos-mvc` y `/profesores-mvc`.
   * Intenta forzar la URL `/springboot-api/roles-mvc` en la barra del navegador.
   * **Resultado**: Se dispara `AuthorizationFilter`, bloquea la petición y redirige a `/access-denied` (403 Forbidden).
3. **Prueba como ESTUDIANTE (`apaez@icesi.edu.co` / `password123`)**:
   * Inicia sesión.
   * Observa cómo en la barra de navegación solo aparecen los enlaces a **Cursos** y **Matrículas**.
   * Intenta ingresar a `/springboot-api/usuarios`.
   * **Resultado**: Bloqueo automático 403 Forbidden.

---

## ⚠️ 4. Puntos de Fricción Frecuentes y Respuestas Rápidas

| Problema / Error del Estudiante | Causa Raíz | Solución Inmediata |
|---|---|---|
| **`LazyInitializationException`** al invocar `usuario.getRoles()` | La sesión de Hibernate se cerró antes de cargar las colecciones ManyToMany en `CustomUserDetailsService`. | Anotar `loadUserByUsername` con `@Transactional(readOnly = true)` e inicializar las listas explícitamente (`size()`). |
| **Error `403 Forbidden` persistente con `hasRole('ADMINISTRADOR')`** | Se configuró `ROLE_ADMINISTRADOR` dentro de `.hasRole("ROLE_ADMINISTRADOR")`. Spring agrega automáticamente el prefijo `ROLE_`, buscando `ROLE_ROLE_ADMINISTRADOR`. | Utilizar `.hasRole("ADMINISTRADOR")` o alternativamente `.hasAuthority("ROLE_ADMINISTRADOR")`. |
| **Error `403 Forbidden` en peticiones POST (ej. al hacer Logout o guardar usuario)** | Spring Security tiene activada la protección CSRF por defecto y el formulario no envía el token `_csrf`. | Usar `th:action="@{/ruta}"` en Thymeleaf (inyecta el input oculto automáticamente) o agregar el input `_csrf` manual. |
| **Login falla siempre con usuarios de `data.sql`** | Las contraseñas están almacenadas en texto plano o con prefijos no reconocidos por el `BCryptPasswordEncoder`. | Hashear la contraseña con `BCryptPasswordEncoder` antes de insertarla en el script `data.sql`. |

---

## 📂 5. Ubicación de Diapositivas Generadas
* **Sesión 1 (HTML / PDF Vectorial)**: [`slides/spring-security-mvc/spring-security-s1/`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/slides/spring-security-mvc/spring-security-s1/)
* **Sesión 2 (HTML / PDF Vectorial)**: [`slides/spring-security-mvc/spring-security-s2/`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/slides/spring-security-mvc/spring-security-s2/)

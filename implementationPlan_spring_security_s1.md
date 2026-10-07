# Plan de Implementación y Guía Docente: Sesión 1 — Spring Security en Spring MVC

**Curso**: Computación en Internet II  
**Programa**: Ingeniería de Sistemas — Universidad Icesi  
**Proyecto Base**: `springboot` (`com.compunet.springboot` — Sistema Académico ICESI)  
**Tema**: Fundamentos, Paradigmas Criptográficos, Cookies, Sesiones HTTP (`JSESSIONID`), Cadena de Filtros y Login Básico Personalizado  
**Duración**: 1 Sesión (2 horas = 120 minutos) — **[SESIÓN YA REALIZADA]**  
**Documento de Continuación (Sesión 2)**: [`implementationPlan_spring_security.md`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/implementationPlan_spring_security.md)

---

## 🛠️ 1. Entorno Técnico y Parámetros de Ejecución

Toda la implementación se realizó directamente sobre el proyecto base ubicado en [`springboot/`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot).

* **Puerto del Servidor**: `8080`
* **Context Path**: `/springboot-api`
* **URL Base de la Aplicación**: `http://localhost:8080/springboot-api`
* **Controlador Principal**: [`UsuarioController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/UsuarioController.java): `/usuarios`

---

## ⏱️ 2. Cronograma Minuto a Minuto — Sesión 1

| Minutos | Bloque Temático | Dinámica y Actividad Pedagógica | Diapositivas |
|:---:|---|---|:---:|
| **00 - 15** | **Apertura y Motivación** | Presentación de la agenda y ruta de aprendizaje. Pregunta disparadora: *¿Por qué al recargar una página web el servidor sigue sabiendo quiénes somos si el protocolo HTTP no guarda estado?* | Slides 01 - 03 |
| **15 - 45** | **Bloque 1: Autenticación vs Autorización, Paradigmas y Criptografía** | Concepto de identidad proclamada vs validada. Los 3 Paradigmas: Codificación vs Encriptación vs Hashing. Defensas: Salt Criptográfico y Costo Adaptativo. Anatomía del Hash BCrypt (60 caracteres). | Slides 04 - 10 |
| **45 - 65** | **Bloque 2: Cookies, Sesiones HTTP del Servidor y Dependencias Maven** | Mecánica de `Set-Cookie` y `Cookie`. Flags de seguridad esenciales: `HttpOnly`, `Secure`, `SameSite` (Lax/Strict), `Max-Age` y `Path`. Arquitectura interna de `HttpSession` y `JSESSIONID` en RAM de Tomcat. Starters en `pom.xml`. | Slides 11 - 16 |
| **65 - 80** | 🔍 **Live Demo 1: Inspección de Cookies y Sesiones en DevTools** | **Actividad Práctica Guiada con `springboot`**: Solicitar `/springboot-api/usuarios`, observar la redirección, autenticarse, inspeccionar `JSESSIONID`, banderas de cookie y experimentar con el borrado manual de la cookie en vivo. | Slide 17 |
| **80 - 100** | **Bloque 3: Cadena de Filtros, UserDetails y Login Personalizado** | Arquitectura `DelegatingFilterProxy`, `FilterChainProxy`, la `SecurityFilterChain` y filtros cardinales. Contratos fundamentales: `UserDetails` vs `UserDetailsService`. Configuración declarativa y controlador/vista de login básico. | Slides 18 - 25 |
| **100 - 120** | 🔍 **Live Demo 2: Implementación de `SecurityConfig`, Login Propio y Depuración** | **Actividad Práctica Guiada con `springboot`**: Implementar `SecurityConfig.java`, crear `LoginController.java` y plantilla básica `login.html` (form con 2 campos y botón), registrar usuario de prueba en memoria, activar logs `DEBUG` y observar en consola el paso por cada filtro. | Slides 26 - 27 |

---

## 💻 3. Guía Detallada de Live Demos y Código de la Sesión 1

### 📦 Paso 1: Configurar Dependencias en `springboot/pom.xml`

En [`springboot/pom.xml`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/pom.xml):

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

6. **Configuración Explícita de Banderas de Cookie**:
   Para forzar y visibilizar estos atributos en la cabecera `Set-Cookie`, agregar en [`springboot/src/main/resources/application.properties`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/application.properties):
   ```properties
   # ==========================================
   # CONFIGURACIÓN DE COOKIES DE SESIÓN
   # ==========================================
   # 1. HttpOnly (Viene en true por defecto)
   server.servlet.session.cookie.http-only=true

   # 2. Forzar SameSite explícito en la cabecera (lax | strict | none)
   server.servlet.session.cookie.same-site=lax

   # 3. Forzar Secure (Solo activar en entornos con HTTPS / TLS)
   # server.servlet.session.cookie.secure=true
   ```

   > [!TIP]
   > **Dinámica Pedagógica para la Clase:**
   > Inicia el demo mostrando el comportamiento por defecto (donde solo `HttpOnly` está activo en la cabecera). Luego, agrega `server.servlet.session.cookie.same-site=lax` en `application.properties`, reinicia el servidor y pide a los estudiantes que refresquen DevTools para observar cómo la cabecera `Set-Cookie` ahora incluye explícitamente `; SameSite=Lax`.

7. **Experimento de Desconexión Manual**:
   * Haz clic derecho sobre `JSESSIONID` en la tabla de cookies y pulsa **Delete**.
   * Presiona `F5` para recargar la página.
   * **Resultado**: Como Tomcat ya no recibe el ID en la cabecera `Cookie: JSESSIONID=...`, no encuentra la sesión en RAM y redirige inmediatamente al formulario de login.

---

### 🔍 Live Demo 2: Implementación Progresiva de `SecurityConfig`, Filtros y Login Propio (Sesión 1)

#### Objetivo:
Construir de forma interactiva y progresiva la seguridad en el proyecto `springboot`:
1. **Fase 1**: Crear la `SecurityFilterChain` básica clasificando rutas públicas (`permitAll`) vs protegidas (`authenticated`) utilizando `formLogin(Customizer.withDefaults())`.
2. **Fase 2**: Reemplazar el usuario autogenerado de Spring por un usuario propio en memoria mediante `PasswordEncoder` (`BCryptPasswordEncoder`) y `UserDetailsService` (`InMemoryUserDetailsManager`).
3. **Fase 3**: Sustituir el formulario estándar de Spring por una vista y controlador de login personalizado (`LoginController.java` y `login.html`) estilizada vía `styles.css`.
4. **Fase 4**: Depurar en vivo la ejecución de la cadena de filtros en la consola mediante logs `DEBUG`.

---

#### Paso a Paso Detallado para el Docente:

#### 🔹 Fase 1: Creación de la Filter Chain y Control de Acceso a Rutas
En [`springboot/src/main/java/com/compunet/springboot/config/SecurityConfig.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/config/SecurityConfig.java):
* Se inicia configurando exclusivamente las reglas de autorización de rutas con el formulario por defecto de Spring:

```java
package com.compunet.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                // 1. Rutas públicas de libre acceso
                .requestMatchers("/css/**", "/js/**", "/public/**").permitAll()
                // 2. Cualquier otra ruta exige autenticación obligatoria
                .anyRequest().authenticated()
            )
            // Habilita el formulario de login y logout por defecto para pruebas iniciales
            .formLogin(Customizer.withDefaults())
            .logout(Customizer.withDefaults())
            .build();
    }
}
```

* **Demostración en Clase**:
  1. Acceder a `http://localhost:8080/springboot-api/css/styles.css` ➔ Carga directamente (Pública).
  2. Acceder a `http://localhost:8080/springboot-api/usuarios` ➔ Redirige automáticamente al login estándar de Spring Security (Privada).

---

#### 🔹 Fase 2: Instanciar Usuario Propio en Memoria (`UserDetailsService` y `PasswordEncoder`)
* Explicar que por defecto Spring genera una clave aleatoria en consola. Para definir nuestras propias identidades de prueba sin tocar la BD aún, agregamos dos Beans a `SecurityConfig.java`:

```java
    // 1. Algoritmo de Hashing BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Proveedor de Usuarios en Memoria (Contrato UserDetailsService)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails docente = User.builder()
            .username("docente@icesi.edu.co")
            .password(passwordEncoder().encode("profesor123"))
            .roles("PROFESOR")
            .build();

        return new InMemoryUserDetailsManager(docente);
    }
```

* **Demostración en Clase**:
  1. Ingresar en el formulario por defecto con `docente@icesi.edu.co` y `profesor123`.
  2. Demostrar cómo `DaoAuthenticationProvider` delega en `UserDetailsService` y valida la contraseña con `BCryptPasswordEncoder.matches()`.

---

#### 🔹 Fase 3: Formulario y Controlador de Login Personalizado
* Modificar `SecurityConfig.java` para habilitar nuestra propia página de login:

```java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                // IMPORTANTE: /login debe ser público para permitir el acceso al formulario
                .requestMatchers("/login", "/css/**", "/js/**", "/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/usuarios", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .build();
    }
```

* **Crear el Controlador [`LoginController.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/LoginController.java)**:
```java
package com.compunet.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Retorna src/main/resources/templates/login.html
    }
}
```

* **Crear la Plantilla Thymeleaf [`springboot/src/main/resources/templates/login.html`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/templates/login.html)** (estilos centralizados en `styles.css`):
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión — Sistema Académico ICESI</title>
    <link rel="stylesheet" th:href="@{/css/styles.css}">
</head>
<body>
    <div class="login-card">
        <h2>Iniciar Sesión</h2>
        <p class="subtitle">Computación en Internet II — Demo</p>

        <!-- Mensaje de Error de Autenticación -->
        <div th:if="${param.error}" class="alert alert-error">
            Correo o contraseña incorrectos. Por favor intenta de nuevo.
        </div>

        <!-- Mensaje de Cierre de Sesión -->
        <div th:if="${param.logout}" class="alert alert-info">
            Has cerrado sesión satisfactoriamente.
        </div>

        <form th:action="@{/login}" method="post">
            <div class="form-group">
                <label for="username">Correo Institucional</label>
                <input type="text" id="username" name="username" placeholder="docente@icesi.edu.co" required autofocus />
            </div>

            <div class="form-group">
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" required />
            </div>

            <!-- th:action inyecta automáticamente el token CSRF oculto -->
            <button type="submit" class="btn-submit">Ingresar al Sistema</button>
        </form>
    </div>
</body>
</html>
```

---

#### 🔹 Fase 4: Depuración y Análisis en Consola con Logs `DEBUG`
1. **Activar Logging en [`application.properties`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/application.properties)**:
   ```properties
   logging.level.org.springframework.security=DEBUG
   ```
2. **Observar en la Consola la Cadena de Filtros**:
   ```text
   DEBUG o.s.s.web.DefaultSecurityFilterChain : Will secure any request with [
     DisableEncodeUrlFilter,
     SecurityContextHolderFilter,
     HeaderWriterFilter,
     CsrfFilter,
     LogoutFilter,
     UsernamePasswordAuthenticationFilter,
     DefaultLoginPageGeneratingFilter,
     ExceptionTranslationFilter,
     AuthorizationFilter
   ]
   ```
3. **Flujo de Ejecución en Vivo**:
   * Petición inicial a `/usuarios` ➔ Rechazo en `AuthorizationFilter` ➔ `ExceptionTranslationFilter` redirige a `/login`.
   * Envío del formulario ➔ `UsernamePasswordAuthenticationFilter` intercepta `POST /login`, consulta `UserDetailsService`, valida con `BCryptPasswordEncoder` y establece el `SecurityContext`.
   * Redirección a `/usuarios` ➔ `SecurityContextHolderFilter` restaura la identidad y `AuthorizationFilter` autoriza el acceso.

---

> [!NOTE]
> **Continuación:** La implementación de la persistencia de usuarios con base de datos (`data.sql`), el modelo relacional RBAC, `CustomUserDetailsService`, autorización por roles/permisos, `@PreAuthorize` y Thymeleaf se encuentra en [`implementationPlan_spring_security.md`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/implementationPlan_spring_security.md).

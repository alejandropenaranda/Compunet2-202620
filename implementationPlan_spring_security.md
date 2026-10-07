# Plan de Implementación y Guía Docente: Sesión 2 — Spring Security en Spring MVC

**Curso**: Computación en Internet II  
**Programa**: Ingeniería de Sistemas — Universidad Icesi  
**Proyecto Base**: `springboot` (`com.compunet.springboot` — Sistema Académico ICESI)  
**Tema**: Autorización Granular, Roles, Authorities, Motor de Autenticación, Persistencia JPA (RBAC), `@PreAuthorize` y Vistas Thymeleaf  
**Duración**: 1 Sesión (2 horas = 120 minutos)  
**Documento Previo (Sesión 1)**: [`implementationPlan_spring_security_s1.md`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/implementationPlan_spring_security_s1.md)

---

> [!NOTE]
> **Punto de Partida tras la Sesión 1:**  
> La Sesión 1 ya fue dictada exitosamente. El proyecto cuenta con un formulario de login personalizado (`/login`), interceptor de rutas público/privado en `SecurityConfig.java` y validación de credenciales en memoria.  
> Los detalles y demos de la Sesión 1 se encuentran archivados en [`implementationPlan_spring_security_s1.md`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/implementationPlan_spring_security_s1.md).  
> **Objetivo de la Sesión 2:** Conectar la seguridad a la base de datos real con el modelo relacional RBAC, aplicar autorización granular por roles (`hasRole`) y privilegios atómicos (`hasAuthority`), blindar métodos con `@PreAuthorize` y adaptar dinámicamente la UI en Thymeleaf con `sec:authorize`.

---

## 🛠️ 1. Entorno Técnico y Datos Semilla de Prueba

Toda la implementación se realiza sobre el proyecto base en [`springboot/`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot).

* **Puerto del Servidor**: `8080`
* **Context Path**: `/springboot-api`
* **URL Base**: `http://localhost:8080/springboot-api`
* **Controladores MVC Existentes**:
  * [`UsuarioController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/UsuarioController.java): `/usuarios`
  * [`ProfesorController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/ProfesorController.java): `/profesores-mvc`
  * [`CursoController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/CursoController.java): `/cursos-mvc`
  * [`MatriculaController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/MatriculaController.java): `/matriculas-mvc`
  * [`RolController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/RolController.java): `/roles-mvc`
  * [`PermisoController`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/PermisoController.java): `/permisos-mvc`
* **Datos Semilla en [`data.sql`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/data.sql)**:
  * **Roles**: `ADMINISTRADOR` (ID 1), `PROFESOR` (ID 2), `ESTUDIANTE` (ID 3).
  * **Permisos**: `USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`, `COURSE_READ`, `COURSE_WRITE`, `ENROLLMENT_WRITE`.
  * **3 Usuarios Semilla de Prueba con Hashes BCrypt (Strength 10)**:
    1. **Administrador** (Acceso total al sistema y gestión de roles):
       * Usuario: `admin@icesi.edu.co`
       * Contraseña en plano: `admin123`
       * Hash en `data.sql`: `$2a$10$4zGHzXmSU49qDHK7m820NeTrNxP9EZ4xC9h6xhD9FtlkphqvSgWKO`
       * Rol: `ADMINISTRADOR` | Authorities: `ROLE_ADMINISTRADOR`, `USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`, etc.
    2. **Profesor** (Gestión académica de cursos y lectura):
       * Usuario: `drincon@icesi.edu.co`
       * Contraseña en plano: `profesor123`
       * Hash en `data.sql`: `$2a$10$u1/GC.ZBn1gwL9NY8oHwy./j.iN04SkyAV1WUh1BnBc33w6GafNI6`
       * Rol: `PROFESOR` | Authorities: `ROLE_PROFESOR`, `USER_READ`, `COURSE_READ`, `COURSE_WRITE`.
    3. **Estudiante** (Consulta de asignaturas y matrículas):
       * Usuario: `apaez@icesi.edu.co`
       * Contraseña en plano: `estudiante123`
       * Hash en `data.sql`: `$2a$10$fqtWn/Xuwr9KvGMw1ybRUezTtEgeK8RoxNk2hD.ZWY.x0UXhpmAAy`
       * Rol: `ESTUDIANTE` | Authorities: `ROLE_ESTUDIANTE`, `COURSE_READ`, `ENROLLMENT_WRITE`.

---

## ⏱️ 2. Cronograma Minuto a Minuto — Sesión 2 (120 Minutos)

| Minutos | Bloque Temático | Dinámica y Actividad Pedagógica | Diapositivas |
|:---:|---|---|:---:|
| **00 - 15** | **Recapitulación S1 y Apertura S2** | Breve revisión del punto de partida: login básico ya operativo en memoria. Planteamiento del reto de AuthZ: *Si todos los usuarios pueden iniciar sesión, ¿cómo evitamos que un ESTUDIANTE acceda a `/roles-mvc` o invoque la acción de eliminar profesores?* | Slides 01 - 03 |
| **15 - 32** | **Bloque 1: Autorización a Fondo (AuthZ) y Anatomía de Authentication** | Principio de Menor Privilegio (PoLP). El modelo en memoria de Spring Security: `SecurityContextHolder` (`ThreadLocal`), `SecurityContext` y el objeto `Authentication` (Principal, Credentials, Authorities, Details). **Diagrama arquitectónico SVG**: Anatomía interna de `Authentication` y flujo de interacción con el contexto de seguridad. | Slides 04 - 06 |
| **32 - 47** | **Bloque 2: Roles vs Authorities y Modelo RBAC** | Diferenciación formal: `GrantedAuthority` (permisos atómicos ej. `USER_DELETE`, `COURSE_WRITE`) vs Roles de alto nivel (`ADMINISTRADOR`, `PROFESOR`, `ESTUDIANTE`). Convención `ROLE_`, reglas `hasRole(...)` vs `hasAuthority(...)`. Diagrama relacional RBAC limpio en BD (tablas `usuario`, `usuario_rol`, `rol`, `rol_permiso`, `permiso`). | Slides 07 - 09 |
| **47 - 67** | **Bloque 3: Arquitectura del Motor de Autenticación y Persistencia JPA** | Arquitectura interna: flujo `AuthenticationFilter` ➔ `AuthenticationManager` (`ProviderManager`) ➔ `DaoAuthenticationProvider` ➔ `UserDetailsService` (BD) & `PasswordEncoder` (BCrypt) ➔ `SecurityContextHolder`. Implementación de `CustomUserDetails` y `CustomUserDetailsService` en `springboot`. Transición de usuarios en memoria a la BD (`data.sql`). | Slides 10 - 15 |
| **67 - 92** | **Bloque 4: Configuración, @PreAuthorize y Vistas Thymeleaf** | Construcción de `SecurityConfig.java`: DSL declarativo de `authorizeHttpRequests`, integración del login existente con la BD y página amigable de error 403 (`/access-denied`). **Seguridad de métodos con `@EnableMethodSecurity` y `@PreAuthorize`**: protección granular de métodos en controladores (`@PreAuthorize("hasAuthority('USER_DELETE')")`). **Renderización condicional en Thymeleaf**: tags `sec:authorize="hasRole(...)"` y `sec:authorize="hasAuthority(...)"`. Las 3 capas de **Defensa en Profundidad** (UI + Web + Método). | Slides 16 - 22 |
| **92 - 120** | 🔍 **Live Demo 3: Validación Integral, @PreAuthorize y Pruebas Multiusuario** | **Actividad Práctica Guiada con `springboot`**: Iniciar sesión con diferentes perfiles (`admin@icesi.edu.co`, `drincon@icesi.edu.co`, `apaez@icesi.edu.co`). Evaluar la barra de navegación condicional, verificar bloqueo perimetral 403 en `/roles-mvc` y probar el rechazo de `@PreAuthorize` ante intentos de forzar acciones prohibidas (como eliminar usuarios). | Slides 23 - 25 |

---

## 💻 3. Implementación Paso a Paso para `springboot` (Sesión 2)

### 🧱 Paso 2: Implementación de la Persistencia de Usuarios con JPA

Para conectar la autenticación con los usuarios de la base de datos real (`data.sql`), revisamos la semilla de datos y construimos las clases del paquete `com.compunet.springboot.config` y `com.compunet.springboot.service`:

#### 0. Datos Semilla en `data.sql` con Contraseñas Hasheadas (BCrypt)
Ruta: [`springboot/src/main/resources/data.sql`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/data.sql)

En sistemas seguros **nunca** se almacenan contraseñas en texto plano. En [`data.sql`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/data.sql), la columna `password` contiene hashes calculados con **BCrypt** (fuerza 10). Al procesar el login, `DaoAuthenticationProvider` invoca `passwordEncoder.matches(rawPassword, encodedPassword)` para validar la clave recibida en el formulario contra el hash persistido.

Los 3 usuarios principales configurados para realizar pruebas en vivo son:

```sql
-- 1. Administrador (Clave plana: 'admin123')
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) 
VALUES ('Super', 'Admin', 'admin@icesi.edu.co', '$2a$10$4zGHzXmSU49qDHK7m820NeTrNxP9EZ4xC9h6xhD9FtlkphqvSgWKO', TRUE);

-- 2. Docente (Clave plana: 'profesor123')
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) 
VALUES ('Domiciano', 'Rincon', 'drincon@icesi.edu.co', '$2a$10$u1/GC.ZBn1gwL9NY8oHwy./j.iN04SkyAV1WUh1BnBc33w6GafNI6', TRUE);

-- 3. Estudiante (Clave plana: 'estudiante123')
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) 
VALUES ('Alejandro', 'Paez', 'apaez@icesi.edu.co', '$2a$10$fqtWn/Xuwr9KvGMw1ybRUezTtEgeK8RoxNk2hD.ZWY.x0UXhpmAAy', TRUE);
```

#### 1. Configuración de Carga Inmediata (FetchType.EAGER) en el Modelo
Para que las colecciones `roles` y `permisos` se carguen en memoria tan pronto se consulte el usuario desde el repositorio, se define `fetch = FetchType.EAGER` en las relaciones ManyToMany:

* **En [`Usuario.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/model/Usuario.java)**:
  ```java
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "usuario_rol", ...)
  private List<Rol> roles = new ArrayList<>();
  ```
* **En [`Rol.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/model/Rol.java)**:
  ```java
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "rol_permiso", ...)
  private List<Permiso> permisos = new ArrayList<>();
  ```

#### 2. Adaptador `CustomUserDetails.java`
Ruta: [`springboot/src/main/java/com/compunet/springboot/config/CustomUserDetails.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/config/CustomUserDetails.java)

Al estar configurado el modelo con `FetchType.EAGER`, el objeto `Usuario` ya contiene sus roles y permisos disponibles en memoria, permitiendo que `CustomUserDetails` delegue directamente sobre la entidad:

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

    public CustomUserDetails(Usuario usuario){
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Rol rol : usuario.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));

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
        return usuario.getCorreoInstitucional();
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

#### 3. Servicio `CustomUserDetailsService.java`
Ruta: [`springboot/src/main/java/com/compunet/springboot/config/CustomUserDetailsService.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/config/CustomUserDetailsService.java)

```java
package com.compunet.springboot.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return new CustomUserDetails(usuario);
    }
}
```

---

### 🛡️ Paso 3: Configuración de Seguridad en `SecurityConfig.java`

Actualizamos [`springboot/src/main/java/com/compunet/springboot/config/SecurityConfig.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/config/SecurityConfig.java) para:
1. Retirar el `InMemoryUserDetailsManager` provisional de la Sesión 1.
2. Inyectar automáticamente nuestro `CustomUserDetailsService`.
3. Definir reglas perimetrales por Rol con `hasRole()` o `hasAnyRole()`.
4. Manejar el error 403 Forbidden con una página amigable.

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
@EnableMethodSecurity // Habilita la seguridad granular de métodos con @PreAuthorize
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                // 1. Recursos estáticos y rutas públicas
                .requestMatchers("/css/**", "/js/**", "/images/**", "/public/**").permitAll()
                .requestMatchers("/login", "/access-denied").permitAll()

                // 2. Rutas administrativas exclusivas
                .requestMatchers("/roles-mvc/**", "/permisos-mvc/**").hasRole("ADMINISTRADOR")

                // 3. Rutas de gestión académica (Admin o Profesor)
                .requestMatchers("/profesores-mvc/**").hasAnyRole("ADMINISTRADOR", "PROFESOR")

                // 4. Rutas operativas generales (cualquier usuario con sesión activa)
                .requestMatchers("/usuarios/**", "/cursos-mvc/**", "/matriculas-mvc/**").authenticated()

                // 5. Todo lo demás requiere autenticación
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
            .build();
    }
}
```

---

### 🎯 Paso 3.1: Seguridad a Nivel de Método con `@PreAuthorize`

La autorización perimetral basada en URL (`requestMatchers`) no es suficiente: un usuario autenticado podría intentar enviar un `POST /usuarios/eliminar/5` por fuera de la interfaz gráfica.

Anotamos los métodos del controlador con `@PreAuthorize` evaluando permisos atómicos SpEL:

#### En [`UsuarioController.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/UsuarioController.java):

```java
    // 1. Solo usuarios con el permiso atómico USER_DELETE pueden invocar la eliminación
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

    // 2. Solo administradores pueden crear nuevos usuarios
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }
```

---

### 🎨 Paso 4: Vistas Thymeleaf, Fragmento `mainNavbar` y Renderización Condicional

Para que la interfaz se adapte según los privilegios del usuario autenticado, utilizamos el dialecto de seguridad de Thymeleaf.

#### 0. Dependencia y Namespace XML Requeridos
Ruta: [`springboot/pom.xml`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/pom.xml)

Para que Thymeleaf reconozca los atributos `sec:authorize` y `sec:authentication`, es indispensable incluir el starter de extras en `pom.xml`:

```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

> [!WARNING]
> **¿Por qué es obligatoria esta dependencia?**  
> Sin `thymeleaf-extras-springsecurity6`, Spring Boot **no registra el `SpringSecurityDialect`**. Por tanto, Thymeleaf desconoce el prefijo `sec:*` e ignora los atributos, dejando etiquetas como `<strong sec:authentication="name"></strong>` completamente vacías en el navegador.

Además, en las plantillas HTML se debe declarar el espacio de nombres en la etiqueta raíz:
```html
<html lang="es" 
      xmlns:th="http://www.thymeleaf.org" 
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
```

#### 1. Despliegue de Identidad del Usuario (`sec:authentication`)

Spring Security permite inspeccionar el objeto `Authentication` y el `Principal` autenticado mediante `sec:authentication`:

| Expresión Thymeleaf | Objeto Leído | Resultado Visualizado |
|---|---|---|
| `sec:authentication="name"` | `authentication.getName()` | Correo Institucional (ej. `admin@icesi.edu.co`) |
| `sec:authentication="principal.nombreCompleto"` | `CustomUserDetails.getNombreCompleto()` | Nombre y Apellido (ej. `Super Admin`) |
| `sec:authentication="principal.usuario.nombre"` | `CustomUserDetails.getUsuario().getNombre()` | Primer Nombre (ej. `Super`) |

> [!TIP]
> **Enfoque Pedagógico en Clase (HTML Semántico Puro sin Clases):**  
> Gracias a las reglas definidas en [`styles.css`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/static/css/styles.css), los elementos como `<label>`, `<input>`, `<button>`, `<form>` y `<nav>` toman automáticamente sus estilos, márgenes y colores por defecto. **El docente no necesita escribir atributos `class="..."` en vivo durante la clase.**

#### 2. Controlador para Acceso Denegado [`LoginController.java`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/java/com/compunet/springboot/controller/LoginController.java)
```java
    @GetMapping({"/access-denied", "/acces-denied"})
    public String accessDenied() {
        return "acces-denied"; // Renderiza src/main/resources/templates/acces-denied.html
    }
```

#### 3. Plantilla Limpia [`springboot/src/main/resources/templates/acces-denied.html`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/templates/acces-denied.html)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title>403 — Acceso Denegado</title>
    <link rel="stylesheet" th:href="@{/css/styles.css}">
</head>
<body>
    <main style="text-align:center;">
        <h1 style="color:#dc2626; font-size:56px; margin:0;">403</h1>
        <h2>Acceso Denegado</h2>
        <p style="color:#64748b; margin-bottom:20px;">No tienes los privilegios o roles necesarios para acceder a este recurso.</p>
        <p><a th:href="@{/usuarios}">Volver al Inicio</a></p>
    </main>
</body>
</html>
```

#### 4. Fragmento Modular de Barra de Navegación en [`springboot/src/main/resources/templates/fragments/layout.html`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/templates/fragments/layout.html)

En la arquitectura del proyecto `springboot`, la navegación no se duplica en cada página, sino que se administra centralizadamente mediante el fragmento `mainNavbar`:

```html
<!-- Fragmento th:fragment="mainNavbar" en templates/fragments/layout.html -->
<header th:fragment="mainNavbar">
    <nav>
        <div>
            <a th:href="@{/usuarios}"><strong>ICESI Académico</strong></a> |
            <a th:href="@{/usuarios}">Usuarios</a> |
            <!-- Solo visible para Administradores o Profesores -->
            <a th:href="@{/profesores-mvc}" sec:authorize="hasAnyRole('ADMINISTRADOR', 'PROFESOR')">Cuerpo Docente</a> |
            <a th:href="@{/cursos-mvc}">Cursos</a> |
            <a th:href="@{/matriculas-mvc}">Matrículas</a> |
            <!-- Solo visible para Administradores -->
            <a th:href="@{/roles-mvc}" sec:authorize="hasRole('ADMINISTRADOR')">Roles</a> |
            <a th:href="@{/permisos-mvc}" sec:authorize="hasRole('ADMINISTRADOR')">Permisos</a>
        </div>

        <!-- Identidad del Usuario Logueado y Formulario de Logout (Sin clases, estilizado por CSS) -->
        <span sec:authorize="isAuthenticated()">
            Usuario: <strong sec:authentication="name"></strong>
            <form th:action="@{/logout}" method="post">
                <button type="submit">Salir</button>
            </form>
        </span>
    </nav>
</header>
```

#### 5. Consumo en Vistas Hijas y Botones Condicionales en [`templates/usuarios/lista.html`](file:///C:/Users/Alexo/Documents/ICESI/2026-2/COMPUNET_II/Repositorios/Code%20-%20AP%20-%20Compunet2-202620/springboot/src/main/resources/templates/usuarios/lista.html)

Las vistas hijas incluyen el fragmento con `th:replace`:

```html
<!-- Reutilización del Navbar -->
<header th:replace="~{fragments/layout :: mainNavbar}"></header>

<!-- Botón de Eliminar Protegido en la Tabla de Usuarios -->
<td>
    <a th:href="@{/usuarios/editar/{id}(id=${u.id})}">Editar</a> |
    
    <!-- Solo visible si el usuario tiene el permiso atómico USER_DELETE -->
    <form th:action="@{/usuarios/eliminar/{id}(id=${u.id})}" method="post" 
          sec:authorize="hasAuthority('USER_DELETE')" style="display:inline;">
        <button type="submit" onclick="return confirm('¿Seguro que deseas eliminar definitivamente este usuario?');">
            Eliminar
        </button>
    </form>
</td>
```

---

### 🔍 Live Demo 3: Validación Integral y Pruebas Multiusuario (Sesión 2)

#### Objetivo:
Comprobar en vivo el funcionamiento de la **Defensa en Profundidad**:
1. **Capa 1 (UI - Thymeleaf):** La interfaz oculta enlaces y botones según roles y permisos.
2. **Capa 2 (Web - SecurityFilterChain):** Si se intenta navegar directamente por URL a `/roles-mvc`, se bloquea con 403.
3. **Capa 3 (Método - `@PreAuthorize`):** Si un usuario sin `USER_DELETE` intenta enviar una petición POST forzada, el método Java rechaza la invocación antes de tocar el servicio.

#### Matriz de Pruebas en Vivo:

| Usuario | Contraseña | Rol | Acceso a `/roles-mvc` | Botón "Eliminar" en UI | POST `/usuarios/eliminar/{id}` |
|---|---|---|:---:|:---:|:---:|
| `admin@icesi.edu.co` | `admin123` | `ADMINISTRADOR` | ✅ Permitido | ✅ Visible | ✅ Ejecutado (Tiene `USER_DELETE`) |
| `drincon@icesi.edu.co` | `profesor123` | `PROFESOR` | ❌ 403 Access Denied | ❌ Oculto | ❌ Rechazado por `@PreAuthorize` |
| `apaez@icesi.edu.co` | `estudiante123` | `ESTUDIANTE` | ❌ 403 Access Denied | ❌ Oculto | ❌ Rechazado por `@PreAuthorize` |

---

## 📋 Checklist Docente para la Sesión 2

- [ ] Verificar que la base de datos contenga las contraseñas hasheadas con BCrypt en `data.sql`.
- [ ] Confirmar que `@EnableMethodSecurity` esté presente en `SecurityConfig.java`.
- [ ] Recordar a los estudiantes la regla de oro: `hasRole('ADMINISTRADOR')` busca internamente `ROLE_ADMINISTRADOR`; no poner `ROLE_` dentro de `hasRole()`.
- [ ] Mostrar en vivo cómo `CustomUserDetailsService` ejecuta una única consulta SQL gracias a `@Transactional(readOnly = true)` e inicializa las colecciones `roles` y `permisos`.
- [ ] Probar el cierre de sesión (`POST /logout`), verificando que se destruye la `HttpSession` en el servidor y se limpia la cookie en el navegador.

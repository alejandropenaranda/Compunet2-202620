# Guía Docente y Plan de Implementación en Vivo — Sesión 1: Arquitectura de APIs RESTful, DTOs, Java Streams, MapStruct y `@RestController`

**Curso:** Computación en Internet II — Universidad Icesi  
**Sesión:** Semana 11 — Sesión 1 (Duración: 2.5 Horas / 150 min)  
**Presentación Asociada:** [spring-rest-s1.html](../slides/spring-REST-JWT/spring-rest-s1/spring-rest-s1.html) / [spring-rest-s1.pdf](../slides/spring-REST-JWT/spring-rest-s1/spring-rest-s1.pdf) (30 Diapositivas)  
---

## 1. Cronograma Progresivo de la Sesión 1 (4 Micro-Ciclos)

| Micro-Ciclo | Tiempo | Conceptos en Slides | Práctica en Vivo en el Proyecto (`springboot`) |
| :--- | :--- | :--- | :--- |
| **Ciclo 1** | `00:00 - 00:35` (35 min) | **Evolución MVC $\rightarrow$ REST:** `@Controller` vs `@RestController`, `@RequestMapping`, diseño orientado a recursos (sustantivos en plural `/api/v1/usuarios`), versionamiento, métodos REST e idempotencia. | **Paso 1.1:** Diagnóstico en `Controller.java` + Creación inicial del esqueleto de `UsuarioRestController.java` con `@RestController` y `@RequestMapping("/api/v1/usuarios")`. |
| **Ciclo 2** | `00:35 - 01:10` (35 min) | **El Patrón DTO:** Riesgos de exponer la entidad JPA `Usuario` (fuga de contraseñas, *Mass Assignment*, ciclos) y asimetría de contratos (`UsuarioRequestDTO` vs `UsuarioResponseDTO`). | **Paso 1.2:** Creación de `UsuarioRequestDTO.java` y `UsuarioResponseDTO.java` en el paquete `dto.Usuarios`. Mapeo manual inicial en el controlador devolviendo `List<UsuarioResponseDTO>`. |
| **Ciclo 3** | `01:10 - 01:45` (35 min) | **Java Streams API y MapStruct Avanzado:** Procesamiento declarativo (`.stream()`, `.filter()`, `.map()`, `.collect()`), generación en compilación (JSR 269), y resolución de mapeos con `@Named` y `qualifiedByName`. | **Paso 1.3:** Creación de `UsuarioMapper.java` con MapStruct + `@Named("rolToString")`, compilación con Maven e inyección en `UsuarioRestController` reemplazando el mapeo manual. |
| **Ciclo 4** | `01:45 - 02:30` (45 min) | **Parámetros HTTP y `ResponseEntity<T>`:** `@PathVariable`, `@RequestParam`, `@RequestBody`, control de cabeceras (`Location`, `Cache-Control`) y códigos de estado semánticos (`200`, `201`, `204`, `404`, `409`). | **Paso 1.4:** Sobrecarga de `registrarUsuario(usuario, Set<String>)` en `UsuarioService` para asignar roles reales de BD + evolución de `UsuarioRestController` a `ResponseEntity<T>` completando el CRUD. Pruebas en Postman. |

---

## 2. Diagnóstico del Proyecto Base (`springboot/`)

Ten presente las características reales del proyecto:

1. **Context Path:** En `src/main/resources/application.properties`:
   ```properties
   server.servlet.context-path=/springboot-app
   ```
   Todas las URLs en Postman comienzan por `http://localhost:8080/springboot-api/...`.
2. **Paquete Raíz:** `com.compunet.springboot`.
3. **Servicio Real Existente (`com.compunet.springboot.service.UsuarioService`):**
   - No existe interfaz `IUsuarioService`: se inyecta directamente `UsuarioService`.
   - Métodos ya existentes en la clase:
     - `public List<Usuario> listarUsuarios()` $\rightarrow$ Todos los usuarios.
     - `public List<Usuario> listarUsuarios(Boolean active)` $\rightarrow$ Ya implementado con Java Streams (`.filter(u -> active == u.isActive())`).
     - `public Optional<Usuario> obtenerPorId(Long id)`.
     - `public Usuario registrarUsuario(Usuario usuario, List<Long> rolIds)` / `registrarUsuario(Usuario usuario)`.
     - `public Usuario registrarUsuario(Usuario usuario, String nombreRolInicial)`.
     - `public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado, List<Long> rolIds)`.
     - `public Usuario alternarEstado(Long id)` $\rightarrow$ Invierte el booleano `active` y guarda.
     - `public void eliminarUsuario(Long id)`.
4. **Ajuste Previo de Seguridad para Pruebas REST en la Sesión 1 (Paso 0):**
   - La seguridad basada en tokens JWT se implementará en la **Sesión 2**.
   - Para poder probar libremente todos los endpoints REST en Postman durante esta clase (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) sin que el formulario web `/login` intercepte las llamadas ni la protección CSRF bloquee las peticiones de mutación con error `403` o redirección `302`, modificamos previamente [`src/main/java/com/compunet/springboot/config/SecurityConfig.java`](src/main/java/com/compunet/springboot/config/SecurityConfig.java):
     ```java
     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         return http
             // 1. Omitir CSRF exclusivamente para los endpoints de la API REST
             .csrf(csrf -> csrf
                 .ignoringRequestMatchers("/api/**")
             )
             .authorizeHttpRequests(auth -> auth
                 // 2. Permitir acceso público a los endpoints versionados /api/v1/**
                 .requestMatchers("/css/**", "/js/**", "/public/**", "/api/v1/**").permitAll()
                 .requestMatchers("/login", "/access-denied").permitAll()
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
             // ... resto de configuración MVC intacta
             .build();
     }
     ```
   *(Nota: En la Sesión 2 reemplazaremos este bypass temporal por una arquitectura dual limpia con `@Order(1)` stateless exclusiva para `/api/**` con JWT).*

---

## Micro-Ciclo 1: De MVC a REST, `@RestController`, `@RequestMapping` y Diagnóstico

### 1.A. Guía Teórica (20 min)
- **Evolución Arquitectónica:** En semanas anteriores usamos `@Controller` para enviar datos al modelo y renderizar plantillas HTML Thymeleaf con `ViewResolver`. Hoy introducimos `@RestController` (`@Controller` + `@ResponseBody`), donde `MappingJackson2HttpMessageConverter` serializa las respuestas directamente a JSON (`application/json`).
- **REST Orientado a Recursos:**
  - Las URIs modelan **sustantivos en plural** (`/api/v1/usuarios`), no acciones ni verbos (`/obtenerUsuarios`, `/crear-usuario`).
  - Versionamiento de rutas como estándar de la industria (`/api/v1/{recurso}`).
- **Semántica de Verbos HTTP e Idempotencia ($f(f(x)) = f(x)$):**
  - `GET`: Seguro + Idempotente (solo lectura).
  - `POST`: No seguro + No idempotente (creación).
  - `PUT`: No seguro + Idempotente (reemplazo completo).
  - `PATCH`: No seguro + Idempotente si asigna un valor fijo (actualización parcial).
  - `DELETE`: No seguro + Idempotente (eliminación).

### 1.B. Práctica en Vivo — Paso 1.1: Diagnóstico y Primer Esqueleto del Controlador (15 min)

1. Abre `src/main/java/com/compunet/springboot/controller/Controller.java` y muestra los antipatrones existentes:
   ```java
   @RestController
   public class Controller {
       // ANTIPATRÓN 1: Devuelve la @Entity JPA directamente (filtrará el password)
       @GetMapping("/usuario")
       public List<Usuario> getUsuarios() {
           return usuarioService.listarUsuarios();
       }

       // ANTIPATRÓN 2: Usa un verbo en la URL y ¡modifica la BD con un método GET!
       @GetMapping("/crear-usuario")
       public Usuario crearUsuario() { ... }
   }
   ```
2. Explica por qué esto viola RFC 9110 y expone los hashes BCrypt.
3. Crea el paquete `controller.rest` y el archivo `UsuarioRestController.java` introduciendo `@RestController` y `@RequestMapping`:
   ```java
   package com.compunet.springboot.controller.rest;

   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   import com.compunet.springboot.service.UsuarioService;
   import lombok.RequiredArgsConstructor;

   @RestController
   @RequestMapping("/api/v1/usuarios")
   @RequiredArgsConstructor
   public class UsuarioRestController {

       private final UsuarioService usuarioService;

       // En el siguiente micro-ciclo añadiremos los DTOs para no devolver la entidad Usuario directa
   }
   ```

---

## Micro-Ciclo 2: El Patrón DTO (Data Transfer Object) y Mapeo Manual

### 2.A. Guía Teórica (15 min)
- **¿Por qué jamás devolver ni recibir `@Entity` en la API?**
  1. **Fuga de Información:** Al serializar `Usuario`, viaja el hash `password` en el JSON.
  2. **Mass Assignment:** Si recibes `Usuario` en un `POST`/`PUT`, un cliente puede enviar `"active": true` o `"id": 1` inyectando valores no autorizados.
  3. **Acoplamiento con la BD:** Si agregas o renombras columnas en JPA, rompes a todos los clientes.
- **Asimetría de Contratos:**
  - `UsuarioRequestDTO`: Datos de entrada (incluye `password` plano y nombres de roles, pero **no** `id` ni `active`).
  - `UsuarioResponseDTO`: Datos de salida (incluye `id`, `nombre`, `correoInstitucional`, `active` y `Set<String> roles`, pero **jamás** `password`).

### 2.B. Práctica en Vivo — Paso 1.2: DTOs y Primer Endpoint sin `ResponseEntity` (20 min)

#### 1. Archivo `src/main/java/com/compunet/springboot/dto/Usuarios/UsuarioRequestDTO.java`
```java
package com.compunet.springboot.dto.Usuarios;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class UsuarioRequestDTO {
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private String password;
    private Set<String> roles;
}
```

#### 2. Archivo `src/main/java/com/compunet/springboot/dto/Usuarios/UsuarioResponseDTO.java`
```java
package com.compunet.springboot.dto.Usuarios;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String correoInstitucional;
    private boolean active;
    private Set<String> roles;
}
```

#### 3. Endpoint Inicial en `UsuarioRestController.java` (Retorno Directo de Objetos)
En este punto todavía **no usamos `ResponseEntity`** porque aún no se ha explicado esa teoría. Mostramos cómo retornar directamente `List<UsuarioResponseDTO>` mapeando a mano:

```java
package com.compunet.springboot.controller.rest;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.compunet.springboot.dto.Usuarios.UsuarioResponseDTO;
import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor 
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponseDTO> getUsuarios(@RequestParam(name = "activo", required = false) Boolean active) {
        List<Usuario> usuarios = (active != null) 
                ? usuarioService.listarUsuarios(active) 
                : usuarioService.listarUsuarios();

        // Mapeo manual para notar el código repetitivo que luego reemplazará MapStruct
        return usuarios.stream()
                .map(usuario -> UsuarioResponseDTO.builder()
                        .id(usuario.getId())
                        .nombre(usuario.getNombre())
                        .apellido(usuario.getApellido())
                        .correoInstitucional(usuario.getCorreoInstitucional())
                        .active(usuario.isActive())
                        .roles(usuario.getRoles().stream()
                                .map(Rol::getNombre)
                                .collect(Collectors.toSet()))
                        .build())
                .toList();
    }
}
```

---

## Micro-Ciclo 3: Java Streams API y MapStruct Avanzado (`@Named` y `qualifiedByName`)

### 3.A. Guía Teórica (15 min)
- **Java Streams API:**
  - Pipeline declarativo: **Fuente** (`.stream()`) $\rightarrow$ **Filtrado** (`.filter(...)`) $\rightarrow$ **Transformación** (`.map(Rol::getNombre)`) $\rightarrow$ **Operación Terminal** (`.toList()` o `.collect(Collectors.toSet())`).
  - Beneficios: inmutabilidad, concisión y legibilidad frente a bucles `for` con acumuladores mutables.
- **MapStruct (JSR 269):**
  - Elimina el código repetitivo de mapeo manual en compilación (cero reflexión en runtime).
- **¿Qué hace `@Named` y `qualifiedByName` en MapStruct? (Momento Conceptual Clave):**
  - **El Problema:** MapStruct empareja tipos automáticamente por su firma. Pero, ¿qué pasa si tenemos una colección `List<Rol>` y queremos transformarla a `Set<String>` extrayendo solo el nombre del rol? O si tuviéramos múltiples formas de mapear un `Rol` (por ejemplo, extraer su nombre vs. extraer su descripción).
  - **La Solución:** Con `@Named("rolToString")` le damos un identificador único y explícito a una función de conversión personalizada. Luego, en la anotación `@Mapping` declaramos `qualifiedByName = "rolToString"`.
  - **Resultado:** MapStruct autogenera el bucle sobre la lista invocando exactamente la función identificada, garantizando cero ambigüedad y total claridad en la conversión:
    ```java
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolToString")
    UsuarioResponseDTO toResponseDTO(Usuario usuario);

    @Named("rolToString")
    default String rolToString(Rol rol) {
        return (rol != null) ? rol.getNombre() : null;
    }
    ```

### 3.B. Práctica en Vivo — Paso 1.3: Configuración de MapStruct e Interfaz `UsuarioMapper` (20 min)

#### 1. Configuración de MapStruct en `pom.xml`
Para que MapStruct funcione en Spring Boot y conviva armoniosamente con Lombok (generando las clases de implementación `@Component` en tiempo de compilación sin errores de dependencias), se deben agregar 3 bloques en `springboot/pom.xml`:

##### A. Declarar versiones en `<properties>`:
```xml
<properties>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
</properties>
```

##### B. Añadir la dependencia en `<dependencies>`:
```xml
<!-- MapStruct: Anotaciones e interfaces para mapeo DTO <-> Entidad -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>${mapstruct.version}</version>
</dependency>
```

##### C. Configurar `annotationProcessorPaths` en `maven-compiler-plugin`:
> **¿Por qué es indispensable este orden?**  
> Tanto Lombok como MapStruct funcionan como procesadores de anotaciones (JSR 269). Si no se declara este plugin con los 3 procesadores en orden estricto, o si se omite `lombok-mapstruct-binding`, MapStruct se ejecutará antes de que Lombok genere los getters, setters y builders, provocando que la clase `UsuarioMapperImpl` no compile o no se genere como bean `@Component` (disparando el error: *`required a bean of type 'UsuarioMapper' that could not be found`*).

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <!-- 1. Lombok genera getters, setters y @Builder -->
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
            <!-- 2. Binding oficial entre Lombok y MapStruct -->
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok-mapstruct-binding</artifactId>
                <version>${lombok-mapstruct-binding.version}</version>
            </path>
            <!-- 3. Procesador de MapStruct que genera las clases *Impl.java -->
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>${mapstruct.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

#### 2. Crear `src/main/java/com/compunet/springboot/mapper/UsuarioMapper.java`
Crea el paquete `mapper` y define la interfaz `UsuarioMapper`:

```java
package com.compunet.springboot.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.compunet.springboot.dto.Usuarios.UsuarioRequestDTO;
import com.compunet.springboot.dto.Usuarios.UsuarioResponseDTO;
import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    // 1. Mapeo de Entidad a ResponseDTO usando qualifiedByName
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario);

    List<UsuarioResponseDTO> toUsuarioResponseDTOList(List<Usuario> usuarios);

    // 2. Mapeo de RequestDTO a Entidad
    // Ignoramos id (lo genera la BD), active (se asigna en el servicio) y roles:
    // Los roles se resuelven contra la BD en UsuarioService, ¡un mapper nunca debe hacer consultas SQL!
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Usuario toEntity(UsuarioRequestDTO requestDTO);

    // 3. Método calificado con @Named para convertir cada Rol individual a su nombre String
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(List<Rol> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Rol::getNombre)
                .collect(Collectors.toSet());
    }
}
```

#### 3. Compilar con Maven y mostrar el código autogenerado
Ejecuta en la terminal:
```bash
mvn compile
```
Abre en el IDE `target/generated-sources/annotations/com/compunet/springboot/mapper/UsuarioMapperImpl.java`. Muestra a los estudiantes cómo MapStruct:
1. Generó la clase `@Component public class UsuarioMapperImpl implements UsuarioMapper`.
2. Generó automáticamente un bucle que recorre `List<Rol>` invocando `rolesToStrings(roles)` para poblar el `Set<String>` del builder de `UsuarioResponseDTO`.

#### 4. Inyectar `UsuarioMapper` en `UsuarioRestController`
Ahora el endpoint `getUsuarios` reemplaza el bloque manual de mapeo por una sola llamada limpia:
```java
@GetMapping
public List<UsuarioResponseDTO> getUsuarios(@RequestParam(name = "activo", required = false) Boolean active) {
    List<Usuario> usuarios = (active != null) 
            ? usuarioService.listarUsuarios(active) 
            : usuarioService.listarUsuarios();

    return usuarioMapper.toUsuarioResponseDTOList(usuarios);
}
```

---

## Micro-Ciclo 4: Asignación de Roles en Servicio, Parámetros HTTP y `ResponseEntity<T>`

### 4.A. Guía Teórica (15 min)
- **Lógica de Asignación de Roles en el Registro (Explicación para la Clase):**
  - Cuando el cliente envía un JSON con `"roles": ["ADMINISTRADOR", "PROFESOR"]`, `UsuarioRequestDTO` recibe un `Set<String>`.
  - Sin embargo, la entidad JPA `Usuario` necesita objetos `@Entity Rol` vinculados al contexto de persistencia.
  - **Regla de Arquitectura:** El Mapper es puro (no consulta BD). La búsqueda de los roles y la validación de si existen en la BD es **responsabilidad exclusiva de la Capa de Servicio**.
  - En `UsuarioService`, iteramos sobre el `Set<String> roles`, buscamos cada rol en `RolRepository.findByNombre(nombre)` y los asociamos al usuario antes de guardarlo.
- **Extracción de Parámetros HTTP:**
  - `@PathVariable`: Identificador único y obligatorio en el segmento de la URL (`/api/v1/usuarios/{id}`).
  - `@RequestParam`: Filtros o paginación opcionales tras el signo `?` (`?activo=true`).
  - `@RequestBody`: Deserialización automática del JSON entrante hacia `UsuarioRequestDTO`.
- **Control Total con `ResponseEntity<T>` y `HttpStatus`:**
  - Permite responder con el código HTTP exacto: `200 OK`, `201 Created` + cabecera `Location`, `204 No Content` (sin cuerpo), `404 Not Found` y `409 Conflict`.
  - Soporte de directivas de caché con `CacheControl.maxAge(...)` y `eTag(...)`.

### 4.B. Práctica en Vivo — Paso 1.4: Servicio y Controlador Completo (30 min)

#### 1. Agregar Sobrecarga de Registro en `UsuarioService.java`
Para procesar el `Set<String> nombresRoles` del DTO sin alterar los métodos existentes utilizados por Thymeleaf MVC, añade esta sobrecarga limpia en [`src/main/java/com/compunet/springboot/service/UsuarioService.java`](src/main/java/com/compunet/springboot/service/UsuarioService.java):

```java
    /**
     * Registra un nuevo usuario en el sistema a partir de un conjunto de nombres de roles (Set<String> del DTO).
     * Itera sobre cada nombre, busca la entidad Rol real en BD y la asigna al usuario.
     */
    public Usuario registrarUsuario(Usuario usuario, java.util.Set<String> nombresRoles) {
        if (usuarioRepository.existsByCorreoInstitucional(usuario.getCorreoInstitucional())) {
            throw new IllegalArgumentException("El correo institucional ya se encuentra registrado: "
                    + usuario.getCorreoInstitucional());
        }

        List<Rol> rolesAsignados = new ArrayList<>();

        if (nombresRoles != null && !nombresRoles.isEmpty()) {
            for (String nombreRol : nombresRoles) {
                // Buscamos el rol real en la base de datos mediante RolRepository
                rolRepository.findByNombre(nombreRol).ifPresent(rolesAsignados::add);
            }
        }

        // Si no se suministraron roles o ninguno fue válido, asignamos el rol por defecto
        if (rolesAsignados.isEmpty()) {
            Rol rolDefault = rolRepository.findByNombre("ESTUDIANTE")
                    .orElseThrow(() -> new IllegalStateException("El rol por defecto ESTUDIANTE no existe."));
            rolesAsignados.add(rolDefault);
        }

        usuario.setRoles(rolesAsignados);
        usuario.setActive(true);
        return usuarioRepository.save(usuario);
    }
```

#### 2. Implementar el CRUD Completo en `UsuarioRestController.java` con `ResponseEntity<T>`
Actualiza `UsuarioRestController.java` integrando `ResponseEntity<T>` y la nueva sobrecarga de registro con asignación de roles:

```java
package com.compunet.springboot.controller.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.compunet.springboot.dto.Usuarios.UsuarioRequestDTO;
import com.compunet.springboot.dto.Usuarios.UsuarioResponseDTO;
import com.compunet.springboot.mapper.UsuarioMapper;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor 
public class UsuarioRestController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    /**
     * 1. GET /api/v1/usuarios?activo=true
     * Lista usuarios con filtro opcional de estado.
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getUsuarios(
            @RequestParam(name = "activo", required = false) Boolean active) {

        List<Usuario> usuarios = (active != null) 
                ? usuarioService.listarUsuarios(active) 
                : usuarioService.listarUsuarios();

        return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDTOList(usuarios));
    }

    /**
     * 2. GET /api/v1/usuarios/{id}
     * Consulta por ID con directivas de caché y control 404 NOT_FOUND.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(id);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UsuarioResponseDTO response = usuarioMapper.toUsuarioResponseDTO(usuarioOpt.get());
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate())
                .eTag(String.valueOf(response.hashCode()))
                .body(response);
    }

    /**
     * 3. POST /api/v1/usuarios
     * Crea un nuevo usuario procesando sus roles reales en la BD y retorna 201 Created con Location.
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrarUsuario(@RequestBody UsuarioRequestDTO requestDTO) {
        try {
            Usuario usuario = usuarioMapper.toEntity(requestDTO);
            // Pasamos los datos del usuario y el Set<String> roles del DTO para su resolución en BD
            Usuario creado = usuarioService.registrarUsuario(usuario, requestDTO.getRoles());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(creado.getId())
                    .toUri();

            return ResponseEntity.created(location).body(usuarioMapper.toUsuarioResponseDTO(creado));
        } catch (IllegalArgumentException e) {
            // El correo institucional ya se encuentra registrado
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * 4. PUT /api/v1/usuarios/{id}
     * Reemplazo total e idempotente del recurso.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO requestDTO) {
        try {
            Usuario datosNuevos = usuarioMapper.toEntity(requestDTO);
            Usuario actualizado = usuarioService.actualizarUsuario(id, datosNuevos, requestDTO.getRoles());
            return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDTO(actualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 5. PATCH /api/v1/usuarios/{id}/estado
     * Modificación parcial usando el método alternarEstado de UsuarioService.
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> alternarEstado(@PathVariable Long id) {
        try {
            Usuario usuarioModificado = usuarioService.alternarEstado(id);
            return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDTO(usuarioModificado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 6. DELETE /api/v1/usuarios/{id}
     * Eliminación con respuesta semántica 204 No Content (sin cuerpo).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
```

---

## 3. Demostración en Postman y Cierre de la Sesión 1

Ejecuta estas solicitudes en Postman para validar la API REST y la asignación dinámica de roles:

1. **Listar usuarios:**
   - `GET http://localhost:8080/springboot-api/api/v1/usuarios` $\rightarrow$ `200 OK` (Array JSON con `Set<String> roles` aplanado y sin `password`).
   - `GET http://localhost:8080/springboot-api/api/v1/usuarios?activo=true` $\rightarrow$ `200 OK` (Solo usuarios con `active: true`).
2. **Consultar usuario por ID:**
   - `GET http://localhost:8080/springboot-api/api/v1/usuarios/1` $\rightarrow$ `200 OK` con cabeceras `Cache-Control` y `ETag`.
   - `GET http://localhost:8080/springboot-api/api/v1/usuarios/999` $\rightarrow$ `404 Not Found`.
3. **Crear usuario asignando roles específicos desde el DTO:**
   - `POST http://localhost:8080/springboot-api/api/v1/usuarios` con JSON:
     ```json
     {
       "nombre": "Ana",
       "apellido": "Gomez",
       "correoInstitucional": "agomez@icesi.edu.co",
       "password": "passwordSeguro123",
       "roles": ["PROFESOR"]
     }
     ```
   - Código `201 Created`, encabezado `Location: .../api/v1/usuarios/28`, y respuesta JSON confirmando `"roles": ["PROFESOR"]` y `"active": true`.
4. **Alternar estado:**
   - `PATCH http://localhost:8080/springboot-api/api/v1/usuarios/28/estado` $\rightarrow$ `active: false`.
5. **Eliminar usuario:**
   - `DELETE http://localhost:8080/springboot-api/api/v1/usuarios/28` $\rightarrow$ `204 No Content`.

### Puente Pedagógico hacia la Sesión 2
> Muestra a los estudiantes que actualmente los endpoints de la API son accesibles sin autenticación o dependen de sesiones de navegador. En la **Sesión 2 (`PLAN_IMPLEMENTACION_S2_JWT.md`)** construiremos la cadena de seguridad `SecurityFilterChain` Stateless con `OncePerRequestFilter`, emisión de tokens JWT criptográficos con `AuthenticationManager` y protección de métodos mediante `@PreAuthorize`.

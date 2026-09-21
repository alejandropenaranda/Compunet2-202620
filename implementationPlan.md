# 📋 Implementation Plan: Sesión Práctica de Testing en Spring Boot

Este documento es una guía paso a paso y checklist para replicar en vivo durante la clase de **Testing y Calidad de Software Backend**. Está estructurado secuencialmente para guiar la explicación teórica, la codificación en vivo (*live coding*) y la verificación de resultados.

---

## 🎯 Objetivos de la Sesión
1. Comprender la diferencia entre **Pruebas Unitarias** (aisladas con Mockito) y **Pruebas de Integración** (`@SpringBootTest` con H2).
2. Configurar las dependencias de testing y el perfil de aislamiento `application-test.properties`.
3. Desarrollar la lógica de negocio en la capa de servicios con validaciones y manejo de excepciones.
4. Implementar casos de prueba unitaria aplicando el patrón **AAA (Arrange - Act - Assert)** y dobles de prueba (`@Mock`, `@InjectMocks`, `when()`, `verify()`).
5. Implementar pruebas de integración que validen el ciclo completo con base de datos en memoria H2.

---

## 🧭 Checklist de Clase (Seguimiento Rápido)

- [ ] **Fase 1:** Verificar dependencias en `pom.xml` (Starter Test, H2, Lombok).
- [ ] **Fase 2:** Crear y configurar `src/test/resources/application-test.properties`.
- [ ] **Fase 3:** Construir los Servicios de Negocio (`UsuarioService` y `MatriculaService`).
- [ ] **Fase 4:** Crear Pruebas Unitarias con Mockito (`UsuarioServiceTest` y `MatriculaServiceTest`).
- [ ] **Fase 5:** Crear Pruebas de Integración con H2 (`UsuarioServiceIntegrationTest`).
- [ ] **Fase 6:** Ejecutar y validar la suite de pruebas desde consola y desde el IDE.

---

## 📦 Fase 1: Dependencias del Proyecto (`pom.xml`)

> **Concepto a explicar en clase:** `spring-boot-starter-test` es una dependencia sombrilla (*umbrella dependency*) que incluye JUnit 5 (Jupiter), Mockito, AssertJ, Hamcrest y Spring Test.

### 1.1. Verificar en `pom.xml`
Asegurarse de contar con las siguientes dependencias:

```xml
<!-- Spring Boot Starter Test (JUnit 5 + Mockito + AssertJ + Spring Test) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Base de datos H2 para pruebas en memoria -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok para simplificar código -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### 1.2. Procesador de Anotaciones de Lombok para Tests
En la sección `<build><plugins>` del `pom.xml`, validar que `maven-compiler-plugin` tenga configurado Lombok tanto en `default-compile` como en `default-testCompile`:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <executions>
        <execution>
            <id>default-compile</id>
            <phase>compile</phase>
            <goals><goal>compile</goal></goals>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </execution>
        <execution>
            <id>default-testCompile</id>
            <phase>test-compile</phase>
            <goals><goal>testCompile</goal></goals>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## ⚙️ Fase 2: Configuración del Perfil de Test (`application-test.properties`)

> **Concepto a explicar en clase:** El archivo `application-test.properties` bajo `src/test/resources/` permite aislar las pruebas de la base de datos de producción/desarrollo (PostgreSQL) usando una base de datos liviana en memoria (H2) que se crea y destruye en cada ciclo de pruebas.

### 2.1. Archivo: `src/test/resources/application-test.properties`

```properties
# Nombre de la aplicación para el perfil de test
spring.application.name=springboot-test

# Base de datos en memoria H2
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# Hibernate regenera el esquema automáticamente para cada ejecución de test
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Desactivar ejecución automática de data.sql en perfil de test para evitar colisión de datos
spring.sql.init.mode=never
```
---

## 🧱 Fase 3: Creación de los Servicios Bajo Prueba (SUT)

> **Concepto a explicar en clase:** Los servicios encapsulan las **Reglas de Negocio**. Para probarlos de forma unitaria, inyectamos sus dependencias por constructor usando `@RequiredArgsConstructor` de Lombok.

### 3.1. `UsuarioService.java`
**Ruta:** `src/main/java/com/compunet/springboot/service/UsuarioService.java`

**Reglas de negocio a implementar:**
1. Validar que el correo institucional no exista (`existsByCorreoInstitucional`). Si existe, lanzar `IllegalArgumentException`.
2. Validar que el rol asignado exista en la base de datos (`findByNombre`). Si no existe, lanzar `IllegalStateException`.
3. Asignar el rol al usuario, marcarlo como activo (`setActive(true)`) y guardar.

```java
package com.compunet.springboot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.RolRepository;
import com.compunet.springboot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public Usuario registrarUsuario(Usuario usuario, String nombreRolInicial) {
        // Regla 1: Correo único
        if (usuarioRepository.existsByCorreoInstitucional(usuario.getCorreoInstitucional())) {
            throw new IllegalArgumentException("El correo institucional ya se encuentra registrado: "
                    + usuario.getCorreoInstitucional());
        }

        // Regla 2: Asignación de rol base
        Rol rol = rolRepository.findByNombre(nombreRolInicial)
                .orElseThrow(() -> new IllegalStateException("El rol especificado no existe: " + nombreRolInicial));

        if (usuario.getRoles() == null) {
            usuario.setRoles(new ArrayList<>());
        }

        usuario.getRoles().add(rol);
        usuario.setActive(true);

        return usuarioRepository.save(usuario);
    }
}
```

---

### 3.2. `MatriculaService.java`
**Ruta:** `src/main/java/com/compunet/springboot/service/MatriculaService.java`

**Reglas de negocio a implementar:**
1. Buscar el estudiante; si no existe, lanzar `IllegalArgumentException`.
2. Verificar que el estudiante esté activo; si no, lanzar `IllegalStateException`.
3. Buscar el curso; si no existe, lanzar `IllegalArgumentException`.
4. Validar que no exista matrícula previa con la clave compuesta `EstudianteCursoId(estudianteId, cursoId)`.
5. Guardar y retornar la nueva matrícula (`EstudianteCurso`).

```java
package com.compunet.springboot.service;

import org.springframework.stereotype.Service;
import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Estudiante;
import com.compunet.springboot.model.EstudianteCurso;
import com.compunet.springboot.model.EstudianteCursoId;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.EstudianteCursoRepository;
import com.compunet.springboot.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final EstudianteCursoRepository estudianteCursoRepository;

    public EstudianteCurso matricularEstudianteEnCurso(Long estudianteId, Long cursoId) throws Exception {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
            .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado con ID: " + estudianteId));

        if (!estudiante.isActive()) {
            throw new IllegalStateException("El estudiante está inactivo y no puede matricular cursos.");
        }

        Curso curso = cursoRepository.findById(cursoId)
            .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado con ID: " + cursoId));

        EstudianteCursoId idCompuesto = new EstudianteCursoId(estudianteId, cursoId);
        if (estudianteCursoRepository.existsById(idCompuesto)) {
            throw new IllegalStateException("El estudiante ya se encuentra matriculado en este curso.");
        }

        EstudianteCurso nuevaMatricula = new EstudianteCurso(estudiante, curso);
        return estudianteCursoRepository.save(nuevaMatricula);
    }

}
```

---

## 🧪 Fase 4: Pruebas Unitarias con Mockito (Sin Contexto de Spring)

> **Concepto a explicar en clase:**
> * Las pruebas unitarias **no arrancan Spring** (son instantáneas, duran milisegundos).
> * `@ExtendWith(MockitoExtension.class)` habilita las anotaciones de Mockito.
> * `@Mock` crea dobles de prueba simulados.
> * `@InjectMocks` instancia el servicio real e inyecta los `@Mock`.
> * Se sigue estrictamente el patrón **AAA (Arrange, Act, Assert)**.

### 4.1. `UsuarioServiceTest.java`
**Ruta:** `src/test/java/com/compunet/springboot/service/UsuarioServiceTest.java`

**Casos a cubrir:**
1. Registro exitoso de usuario con rol válido.
2. Excepción cuando el correo ya existe (`IllegalArgumentException`) + verificar que no se guarda nada.
3. Excepción cuando el rol especificado no existe (`IllegalStateException`).

```java
package com.compunet.springboot.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.RolRepository;
import com.compunet.springboot.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - UsuarioService - Mockito")
public class UsuarioServiceTest {

    @Mock 
    private UsuarioRepository usuarioRepository;

    @Mock 
    private RolRepository rolRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;
    private Rol rolEstudiante;
    
    @BeforeEach
    void setUp() {
        usuarioEjemplo = new Usuario();
        usuarioEjemplo.setId(1L);
        usuarioEjemplo.setNombre("Carlos");
        usuarioEjemplo.setApellido("Perez");
        usuarioEjemplo.setCorreoInstitucional("cPerez@icesi.edu.co");
        usuarioEjemplo.setPassword("Secreto123");

        rolEstudiante = new Rol();
        rolEstudiante.setId(10L);
        rolEstudiante.setNombre("ESTUDIANTE");
    }

    @Test 
    @DisplayName("Debe registrar un usuario exitosamente cuando los datos y el rol son válidos")
    void debeRegistrarUsuarioExitosamente() {
        // 1. Arrange
        when(usuarioRepository.existsByCorreoInstitucional(usuarioEjemplo.getCorreoInstitucional())).thenReturn(false);
        when(rolRepository.findByNombre("ESTUDIANTE")).thenReturn(Optional.of(rolEstudiante));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act
        Usuario resultado = usuarioService.registrarUsuario(usuarioEjemplo, "ESTUDIANTE");

        // 3. Assert
        assertNotNull(resultado);
        assertTrue(resultado.isActive());
        assertTrue(resultado.getRoles().contains(rolEstudiante));
        verify(usuarioRepository).save(any(Usuario.class));
    }
    
    @Test 
    @DisplayName("Debe lanzar excepción cuando el correo institucional ya está registrado")
    void debeLanzarExcepcionCuandoCorreoYaExiste() {
        // 1. Arrange
        when(usuarioRepository.existsByCorreoInstitucional(usuarioEjemplo.getCorreoInstitucional())).thenReturn(true);

        // 2. Act & 3. Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuarioEjemplo, "ESTUDIANTE");
        });

        assertTrue(exception.getMessage().contains("ya se encuentra registrado"));
        verify(rolRepository, never()).findByNombre(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test 
    @DisplayName("Debe lanzar excepción cuando el rol especificado no existe")
    void debeLanzarExcepcionCuandoRolNoExiste() {
        // 1. Arrange
        when(usuarioRepository.existsByCorreoInstitucional(usuarioEjemplo.getCorreoInstitucional())).thenReturn(false);
        when(rolRepository.findByNombre("ROL_INEXISTENTE")).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            usuarioService.registrarUsuario(usuarioEjemplo, "ROL_INEXISTENTE");
        });

        assertTrue(exception.getMessage().contains("El rol especificado no existe"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
```

---

### 4.2. `MatriculaServiceTest.java`
**Ruta:** `src/test/java/com/compunet/springboot/service/MatriculaServiceTest.java`

```java
package com.compunet.springboot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Estudiante;
import com.compunet.springboot.model.EstudianteCurso;
import com.compunet.springboot.model.EstudianteCursoId;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.EstudianteCursoRepository;
import com.compunet.springboot.repository.EstudianteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - MatriculaService")
class MatriculaServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private EstudianteCursoRepository estudianteCursoRepository;

    @InjectMocks
    private MatriculaService matriculaService;

    private Estudiante estudianteActivo;
    private Curso cursoNormal;

    @BeforeEach
    void setUp() {
        estudianteActivo = new Estudiante();
        estudianteActivo.setId(1L);
        estudianteActivo.setNombre("Ana");
        estudianteActivo.setActive(true);

        cursoNormal = new Curso();
        cursoNormal.setId(101L);
        cursoNormal.setNombre("Computación en Internet II");
        cursoNormal.setCreditos(3);
    }

    @Test
    @DisplayName("Debe matricular correctamente a un estudiante activo en un curso")
    void debeMatricularEstudianteExitosamente() throws Exception {
        // Arrange
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
        when(cursoRepository.findById(101L)).thenReturn(Optional.of(cursoNormal));
        when(estudianteCursoRepository.existsById(any(EstudianteCursoId.class))).thenReturn(false);
        when(estudianteCursoRepository.save(any(EstudianteCurso.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        EstudianteCurso resultado = matriculaService.matricularEstudianteEnCurso(1L, 101L);

        // Assert
        assertNotNull(resultado);
        assertEquals(estudianteActivo, resultado.getEstudiante());
        assertEquals(cursoNormal, resultado.getCurso());
        verify(estudianteCursoRepository, times(1)).save(any(EstudianteCurso.class));
    }

    @Test
    @DisplayName("Debe fallar si el estudiante está inactivo")
    void debeLanzarExcepcionSiEstudianteEstaInactivo() {
        // Arrange
        estudianteActivo.setActive(false);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            matriculaService.matricularEstudianteEnCurso(1L, 101L);
        });

        assertTrue(ex.getMessage().contains("inactivo"));
        verifyNoInteractions(cursoRepository);
        verify(estudianteCursoRepository, never()).save(any(EstudianteCurso.class));
    }

    @Test
    @DisplayName("Debe fallar si el estudiante ya está matriculado en el curso")
    void debeLanzarExcepcionSiYaEstaMatriculado() {
        // Arrange
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteActivo));
        when(cursoRepository.findById(101L)).thenReturn(Optional.of(cursoNormal));
        when(estudianteCursoRepository.existsById(new EstudianteCursoId(1L, 101L))).thenReturn(true);

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            matriculaService.matricularEstudianteEnCurso(1L, 101L);
        });

        assertTrue(ex.getMessage().contains("ya se encuentra matriculado"));
        verify(estudianteCursoRepository, never()).save(any(EstudianteCurso.class));
    }
}
```

---

## 🔄 Fase 5: Pruebas de Integración (`@SpringBootTest` + H2)

> **Concepto a explicar en clase:**
> * Levantan el `ApplicationContext` de Spring.
> * Usan `@ActiveProfiles("test")` para activar `application-test.properties` (H2 en memoria).
> * Inyectan componentes reales con `@Autowired` (no mocks).
> * Validan la integración real entre Service + Repository + Hibernate + Base de Datos.

### 5.1. `UsuarioServiceIntegrationTest.java`
**Ruta:** `src/test/java/com/compunet/springboot/Integration/UsuarioServiceIntegrationTest.java`

```java
package com.compunet.springboot.Integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.RolRepository;
import com.compunet.springboot.service.UsuarioService;

@SpringBootTest 
@ActiveProfiles("test")
@DisplayName("Pruebas de Integración - UsuarioService con base de datos H2 en memoria")
public class UsuarioServiceIntegrationTest {
    
    @Autowired 
    private UsuarioService usuarioService;
    
    @Autowired 
    private RolRepository rolRepository;

    @Test 
    @DisplayName("Debe persistir el usuario con su rol en la DB H2 y poder consultarlo")
    void debeRegistrarYConsultarUsuarioEnBaseDeDatosReal() {
        // 1. Preparar datos reales en H2
        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("ADMINISTRADOR_TEST");
        rolRepository.save(rolAdmin);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Laura");
        nuevoUsuario.setApellido("Gomez");
        nuevoUsuario.setCorreoInstitucional("lgomez@icesi.edu.co");
        nuevoUsuario.setPassword("Segura123");

        // 2. Ejecutar servicio real
        Usuario usuarioGuardado = usuarioService.registrarUsuario(nuevoUsuario, "ADMINISTRADOR_TEST");

        // 3. Verificar persistencia y generación de ID
        assertNotNull(usuarioGuardado.getId(), "La base de datos debe generar el ID autoincremental");
        assertTrue(usuarioGuardado.isActive());
        assertEquals("lgomez@icesi.edu.co", usuarioGuardado.getCorreoInstitucional());

        // 4. Consultar mediante repositorio integrado
        List<Usuario> usuariosAdmin = usuarioService.listarUsuariosActivosPorRol("ADMINISTRADOR_TEST");
        assertFalse(usuariosAdmin.isEmpty(), "Debe encontrar al menos un usuario con el rol");
        assertTrue(usuariosAdmin.stream().anyMatch(u -> u.getCorreoInstitucional().equals("lgomez@icesi.edu.co")));
    }
}
```

---

## 🚀 Fase 6: Ejecución y Verificación

### 6.1. Comandos Maven desde Terminal
Desde la raíz del proyecto `springboot/`:

```bash
# 1. Ejecutar toda la suite de pruebas (unitarias + integración)
mvn test

# 2. Ejecutar únicamente una clase de prueba específica
mvn test -Dtest=UsuarioServiceTest
mvn test -Dtest=MatriculaServiceTest
mvn test -Dtest=UsuarioServiceIntegrationTest

# 3. Compilar solo los tests sin ejecutarlos
mvn test-compile
```

### 6.2. Ejecución gráfica desde IDEs
* **VS Code:**
  1. Abrir la pestaña de **Testing** (icono de matraz 🧪 en la barra lateral).
  2. Hacer clic en **Run Tests** o ejecutar individualmente cada método con el botón play sobre el código.
* **IntelliJ IDEA:**
  1. Clic derecho sobre la carpeta `src/test/java` -> **Run 'All Tests'**.
  2. O clic en la flecha verde ▶️ al lado del nombre de la clase o método de prueba.

---

## 📊 Resumen Comparativo para la Pizarra

| Criterio | Prueba Unitaria (`UsuarioServiceTest`) | Prueba de Integración (`UsuarioServiceIntegrationTest`) |
| :--- | :--- | :--- |
| **Anotación Base** | `@ExtendWith(MockitoExtension.class)` | `@SpringBootTest` + `@ActiveProfiles("test")` |
| **Levanta Spring** | ❌ No (ultra rápido, ~50 ms) | ✅ Sí (carga `ApplicationContext`, ~2 s) |
| **Base de Datos** | ❌ No (repositorios simulados con `@Mock`) | ✅ Sí (H2 en memoria con tablas reales) |
| **Inyección** | `@InjectMocks` | `@Autowired` |
| **Propósito** | Validar reglas y lógica algorítmica aislada | Validar persistencia real, JPA y conectividad de capas |

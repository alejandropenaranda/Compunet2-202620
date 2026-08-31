# 📋 Plan de Implementación: Proyecto Base Spring Boot
**Curso:** Computación en Red II (Compunet II) — Universidad ICESI  
**Profesor / Docente Guía:** Alejandro Peñaranda  
**Objetivo del Taller:** Guiar a los estudiantes paso a paso en la creación, configuración y comprensión de los componentes fundamentales de una aplicación Spring Boot moderna con persistencia JPA (H2/PostgreSQL), logging, Lombok y endpoints REST.

---

## 🎯 Tabla de Contenido
1. [Visión General del Proyecto](#1-visión-general-del-proyecto)
2. [Estructura del Proyecto](#2-estructura-del-proyecto)
3. [Paso 1: Configuración de Dependencias (`pom.xml`)](#3-paso-1-configuración-de-dependencias-pomxml)
4. [Paso 2: Configuración de Propiedades (`application.properties`)](#4-paso-2-configuración-de-propiedades-applicationproperties)
5. [Paso 3: Modelado de Datos y Entidades JPA con Lombok](#5-paso-3-modelado-de-datos-y-entidades-jpa-con-lombok)
6. [Paso 4: Inicialización Automática de Datos (`data.sql`)](#6-paso-4-inicialización-automática-de-datos-datasql)
7. [Paso 5: Exposición de Endpoints REST (`Controller.java`)](#7-paso-5-exposición-de-endpoints-rest-controllerjava)
8. [Paso 6: Ejecución, Pruebas y Validación en Clase](#8-paso-6-ejecución-pruebas-y-validación-en-clase)
9. [Preguntas Frecuentes y Errores Comunes para Estudiantes](#9-preguntas-frecuentes-y-errores-comunes-para-estudiantes)

---

## 1. Visión General del Proyecto

Este proyecto sirve como plantilla base para aprender:
- El ciclo de vida de una aplicación **Spring Boot**.
- La configuración de **Spring Data JPA** y **Hibernate** para la gestión automática del esquema de base de datos.
- El uso de bases de datos en memoria (**H2 Database**) para desarrollo rápido y su consola web interactiva.
- La alternativa para conectarse a un motor de base de datos relacional de producción (**PostgreSQL**).
- La reducción de código repetitivo (*boilerplate*) mediante **Project Lombok**.
- El orden de ejecución y sincronización entre la creación del esquema DDL y la inserción de scripts SQL (`data.sql`).

---

## 2. Estructura del Proyecto

```text
springboot/
├── pom.xml                                   # Configuración de Maven y dependencias
├── src/
│   ├── main/
│   │   ├── java/com/compunet/springboot/
│   │   │   ├── SpringbootApplication.java     # Clase principal (punto de entrada)
│   │   │   ├── ServletInitializer.java        # Inicializador para empaquetado WAR
│   │   │   ├── controller/
│   │   │   │   └── Controller.java            # Controlador REST base
│   │   │   └── model/
│   │   │       ├── Entidad.java               # Ejemplo de Lombok avanzado (@RequiredArgsConstructor)
│   │   │       ├── Estudiante.java            # Entidad JPA: Estudiante
│   │   │       └── Profesor.java              # Entidad JPA: Profesor
│   │   └── resources/
│   │       ├── application.properties         # Configuración centralizada de Spring Boot
│   │       └── data.sql                       # Semilla de datos iniciales
│   └── test/
│       └── java/com/compunet/springboot/
│           └── SpringbootApplicationTests.java
└── logs/
    └── application.log                        # Archivo físico de logs generado
```

---

## 3. Paso 1: Configuración de Dependencias (`pom.xml`)

El archivo `pom.xml` define las bibliotecas necesarias y la configuración del compilador.

### 3.1. Propiedades y Parent POM
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.8</version>
    <relativePath/>
</parent>

<properties>
    <java.version>17</java.version>
</properties>
```
* **`spring-boot-starter-parent`:** Gestiona las versiones compatibles (BOM - *Bill of Materials*) y los plugins de Maven para evitar conflictos de dependencias.
* **`<java.version>17</java.version>`:** Establece la versión de Java del proyecto.

---

### 3.2. Detalle de Dependencias

| Dependencia | `groupId` / `artifactId` | Propósito pedagógico |
| :--- | :--- | :--- |
| **Spring Web MVC** | `org.springframework.boot:spring-boot-starter-webmvc` | Proporciona el servidor Tomcat embebido y el soporte para construir APIs REST y controladores HTTP. |
| **Spring Data JPA** | `org.springframework.boot:spring-boot-starter-data-jpa` | Integra Hibernate y JPA para mapear clases Java a tablas de base de datos relacionales sin escribir SQL manual. |
| **H2 Database** | `com.h2database:h2` (`runtime`) | Motor de base de datos relacional ligero en memoria RAM / archivo local, ideal para desarrollo y pruebas. |
| **H2 Web Console** | `org.springframework.boot:spring-boot-h2console` | Habilita la interfaz web interactiva en el navegador para consultar tablas y datos de H2. |
| **PostgreSQL Driver** | `org.postgresql:postgresql` (`runtime`) | Driver JDBC para conectar la aplicación a un servidor PostgreSQL real cuando se pase a producción/taller. |
| **Project Lombok** | `org.projectlombok:lombok` (`optional`) | Genera automáticamente getters, setters, constructores y métodos `toString/equals/hashCode` en tiempo de compilación. |

---

### 3.3. Configuración del Plugin de Compilación para Lombok
Para que Lombok funcione correctamente con el compilador de Java y procese las anotaciones (`@Getter`, `@Setter`, etc.):

```xml
<build>
    <finalName>mySpringBootApp</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <executions>
                <execution>
                    <id>default-compile</id>
                    <phase>compile</phase>
                    <goals>
                        <goal>compile</goal>
                    </goals>
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
    </plugins>
</build>
```

---

## 4. Paso 2: Configuración de Propiedades (`application.properties`)

El archivo `src/main/resources/application.properties` está dividido en 5 bloques fundamentales que los estudiantes deben dominar:

### 4.1. Configuración Básica del Servidor y Logging
```properties
# 1. Nombre identificador del microservicio o aplicación
spring.application.name=springboot

# 2. Puerto HTTP donde escuchará el servidor Tomcat embebido
server.port=8080

# 3. Context Path: Prefijo global de todas las rutas HTTP
# Cualquier endpoint como @GetMapping("/users") responderá en:
# http://localhost:8080/springboot-api/users
server.servlet.context-path=/springboot-api

# 4. Configuración de Logs:
# Nivel de log global para todas las librerías del framework
logging.level.root=INFO
# Nivel de log detallado únicamente para los paquetes de nuestra aplicación
logging.level.com.compunet.springboot=DEBUG
# Guarda los logs en un archivo físico en disco además de la consola
logging.file.name=logs/application.log
```

---

### 4.2. Configuración de Base de Datos (H2 vs PostgreSQL)

#### Opción A: H2 Database (Activa para la clase)
```properties
# Driver JDBC de H2
spring.datasource.driver-class-name=org.h2.Driver

# H2 en Memoria RAM:
# 'DB_CLOSE_DELAY=-1' evita que la base de datos se destruya cuando se cierran las conexiones inactivas
spring.datasource.url=jdbc:h2:mem:sistema-academico;DB_CLOSE_DELAY=-1

# (Alternativa: H2 persistido en archivo en disco)
# spring.datasource.url=jdbc:h2:./sistema-academico;DB_CLOSE_DELAY=-1

# Credenciales de conexión
spring.datasource.username=user
spring.datasource.password=password

# Dialecto SQL específico para optimizar las consultas de Hibernate hacia H2
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

#### Opción B: PostgreSQL (Lista para alternar)
```properties
# spring.datasource.driver-class-name=org.postgresql.Driver
# spring.datasource.url=jdbc:postgresql://localhost:5432/boardgame
# spring.datasource.username=postgres
# spring.datasource.password=postgres
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

### 4.3. Gestión del Esquema DDL con Hibernate (`ddl-auto`)
```properties
spring.jpa.hibernate.ddl-auto=create-drop
```
> **Explicación para los estudiantes sobre los valores de `ddl-auto`:**
> - `none`: Hibernate no hace nada en la base de datos (requiere esquemas manuales).
> - `validate`: Verifica que las tablas y columnas existentes en la BD coincidan exactamente con las clases `@Entity`. Falla si no coinciden.
> - `update`: Modifica la estructura agregando nuevas tablas o columnas sin borrar los datos existentes.
> - `create`: Borra las tablas existentes y las vuelve a crear al arrancar la aplicación.
> - `create-drop`: Crea el esquema al iniciar la aplicación y lo destruye automáticamente al apagarla (ideal para pruebas y laboratorios).

---

### 4.4. Inicialización de Datos y Sincronización Clave
```properties
# Ejecutar siempre los scripts SQL de inicialización (data.sql / schema.sql)
spring.sql.init.mode=always

# Muestra u oculta las sentencias SQL ejecutadas por Hibernate en la consola
spring.jpa.show-sql=false

# ¡PROPIEDAD CRÍTICA!
# En Spring Boot moderno, el script data.sql se ejecuta por defecto ANTES de que Hibernate cree las tablas.
# Al habilitar 'defer-datasource-initialization=true', forzamos a Spring a esperar que Hibernate cree las tablas
# (mediante ddl-auto) ANTES de ejecutar el script data.sql. Sin esta propiedad, la app fallará con "Table not found".
spring.jpa.defer-datasource-initialization=true
```

---

### 4.5. Consola Web Interactiva de H2
```properties
# Habilita la consola web en el navegador
spring.h2.console.enabled=true

# Ruta URI para ingresar (tomará en cuenta el context-path o ruta directa)
spring.h2.console.path=/h2-console
```

---

## 5. Paso 3: Modelado de Datos y Entidades JPA con Lombok

En este paso se implementan las entidades dentro del paquete `com.compunet.springboot.model`.

### 5.1. Entidad `Estudiante.java`
Ubicación: `src/main/java/com/compunet/springboot/model/Estudiante.java`

```java
package com.compunet.springboot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String apellido;
    private String email;
    private boolean active;

}
```
* **`@Entity`:** Marca la clase como una tabla administrada por JPA/Hibernate.
* **`@Id` y `@GeneratedValue(strategy = GenerationType.IDENTITY)`:** Define la clave primaria auto-incremental.
* **Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`):** Ahorra más de 40 líneas de código repetitivo de constructores y métodos de acceso.

---

### 5.2. Entidad `Profesor.java`
Ubicación: `src/main/java/com/compunet/springboot/model/Profesor.java`

```java
package com.compunet.springboot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profesor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String apellido;
    private String especialidad;

}
```

---

### 5.3. Modelo POJO Didáctico: `Entidad.java` (Lombok Avanzado)
Ubicación: `src/main/java/com/compunet/springboot/model/Entidad.java`

```java
package com.compunet.springboot.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Entidad {

    private final String prop1;
    private String prop2;
    private final String prop3;

    @NonNull
    private Integer prop4;
    
}
```
> **Nota de Clase:** Explicar a los estudiantes cómo `@RequiredArgsConstructor` genera automáticamente un constructor únicamente para los campos marcados como `final` (`prop1`, `prop3`) o anotados con `@NonNull` (`prop4`), mientras que `prop2` queda fuera de ese constructor.

---

## 6. Paso 4: Inicialización Automática de Datos (`data.sql`)

Ubicación: `src/main/resources/data.sql`

Este script se ejecuta automáticamente al levantar el contexto de Spring:

```sql
-- Inserts base de las tablas - Se ejecuta automáticamente al iniciar el proyecto --
INSERT INTO Estudiante (NOMBRE, APELLIDO, EMAIL, ACTIVE) VALUES ('Alejandro', 'Penaranda', 'apenaranda@icesi.edu.co', TRUE);
INSERT INTO Estudiante (NOMBRE, APELLIDO, EMAIL, ACTIVE) VALUES ('Carlos', 'Perez', 'cperez@icesi.edu.co', TRUE);
INSERT INTO Estudiante (NOMBRE, APELLIDO, EMAIL, ACTIVE) VALUES ('Raul', 'Martinez', 'rmartinez@icesi.edu.co', TRUE);

INSERT INTO Profesor (NOMBRE, APELLIDO, ESPECIALIDAD) VALUES ('Domiciano', 'Rincon', 'Telematica');
INSERT INTO Profesor (NOMBRE, APELLIDO, ESPECIALIDAD) VALUES ('Kevin', 'Rodriguez', 'Desarrollo de Software');
INSERT INTO Profesor (NOMBRE, APELLIDO, ESPECIALIDAD) VALUES ('Alejandro', 'Munoz', 'Arquitectura de Software');
```

---

## 7. Paso 5: Exposición de Endpoints REST (`Controller.java`)

Ubicación: `src/main/java/com/compunet/springboot/controller/Controller.java`

```java
package com.compunet.springboot.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class Controller {

    @GetMapping("/")
    public String home() {
        return "!Proyecto Spring boot funcionando correctamente¡";
    }
    
}
```
* **`@RestController`:** Indica que la clase maneja peticiones HTTP y que las respuestas de los métodos se serializan directamente en el cuerpo de la respuesta (Response Body).
* **`@GetMapping("/")`:** Mapea solicitudes HTTP GET en la ruta raíz del contexto.

---

## 8. Paso 6: Ejecución, Pruebas y Validación en Clase

### 8.1. Compilación y Ejecución
Ejecutar en la terminal de la raíz del proyecto:
```bash
# Con Maven Wrapper (Windows PowerShell / CMD)
./mvnw.cmd spring-boot:run

# O con Maven estándar
mvn spring-boot:run
```

---

### 8.2. Verificación de Endpoints y Consolas

1. **Endpoint REST Principal:**
   - Abrir el navegador en: `http://localhost:8080/springboot-api/`
   - **Respuesta esperada:** `!Proyecto Spring boot funcionando correctamente¡`

2. **Consola Web de H2 Database:**
   - Abrir en el navegador: `http://localhost:8080/springboot-api/h2-console`
   - **Parámetros de conexión a ingresar en la interfaz:**
     * **Driver Class:** `org.h2.Driver`
     * **JDBC URL:** `jdbc:h2:mem:sistema-academico`
     * **User Name:** `user`
     * **Password:** `password`
   - Presionar **Connect** y ejecutar:
     ```sql
     SELECT * FROM ESTUDIANTE;
     SELECT * FROM PROFESOR;
     ```
   - Verificar que los datos insertados por `data.sql` estén presentes.

3. **Verificación de Logs:**
   - Revisar la consola estándar de ejecución.
   - Revisar el archivo generado en `logs/application.log` para confirmar la salida de nivel `DEBUG` del paquete `com.compunet.springboot`.

---

## 9. Preguntas Frecuentes y Errores Comunes para Estudiantes

### ❓ 1. ¿Por qué la URL tiene `/springboot-api` antes de cualquier endpoint?
> Porque en `application.properties` configuramos `server.servlet.context-path=/springboot-api`. Todo endpoint expuesto en la aplicación hereda este prefijo global.

### ❓ 2. ¿Por qué `data.sql` fallaba antes con error `Table "ESTUDIANTE" not found`?
> En versiones recientes de Spring Boot, el inicializador SQL corre antes de que Hibernate cree las tablas. Se debe agregar obligatoriamente `spring.jpa.defer-datasource-initialization=true` para indicar a Spring que espere la creación del esquema por parte de Hibernate.

### ❓ 3. ¿Por qué al reiniciar el servidor se borran los datos en H2?
> Porque se configuró la URL `jdbc:h2:mem:...` (en memoria RAM) y `spring.jpa.hibernate.ddl-auto=create-drop`. Para persistir datos entre reinicios en H2, se debe cambiar la URL a archivo local: `jdbc:h2:./sistema-academico;DB_CLOSE_DELAY=-1` y el ddl-auto a `update`.

### ❓ 4. ¿Cómo cambiar a PostgreSQL para el laboratorio?
> Comentar las líneas del bloque H2 en `application.properties` y descomentar el bloque de PostgreSQL configurando la base de datos, usuario y clave correspondientes.

# Plan de Implementación: Spring Data JPA, Hibernate y relaciones

**Proyecto:** Compunet II - Spring Boot

## 1. Objetivo

Evidenciar cómo una aplicación Spring Boot utiliza Spring Data JPA y Hibernate para:

- Definir entidades Java que se convierten en tablas relacionales.
- Generar claves primarias, columnas, restricciones y relaciones a partir de anotaciones JPA.
- Crear repositorios sin implementar manualmente las consultas básicas.
- Exponer métodos HTTP `GET` como punto de entrada para consultar la información persistida.

## 2. Flujo general de la aplicación

```text
Entidades JPA
    -> Hibernate analiza las anotaciones
    -> genera el esquema SQL de la base de datos
    -> data.sql inserta datos iniciales
    -> Spring Data crea los repositorios
    -> Controller recibe GET y llama findAll()
    -> Hibernate consulta la BD y convierte las filas en objetos Java
    -> Spring Web serializa los objetos como JSON
```

## 3. Configuración y dependencias

El archivo `pom.xml` incluye los componentes que soportan el flujo:

| Dependencia | Función |
| --- | --- |
| `spring-boot-starter-data-jpa` | Integra JPA con Hibernate y Spring Data. |
| `spring-boot-starter-webmvc` | Expone los controladores y endpoints HTTP. |
| `h2` | Base de datos relacional en memoria para desarrollo y pruebas. |
| `spring-boot-h2console` | Permite inspeccionar H2 desde la consola web. |
| `lombok` | Genera getters, setters y constructores durante la compilación. |
| `postgresql` | Driver disponible para cambiar a PostgreSQL. |

La aplicación usa Java 17 y está empaquetada como WAR.

## 4. Configuración de persistencia

En `src/main/resources/application.properties` se configura H2:

```properties
spring.datasource.url=jdbc:h2:mem:sistema-academico;DB_CLOSE_DELAY=-1
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
```

### Efecto de las propiedades principales

- `ddl-auto=create-drop`: Hibernate crea las tablas al iniciar y las elimina al cerrar la aplicación.
- `sql.init.mode=always`: ejecuta `data.sql` durante el arranque.
- `defer-datasource-initialization=true`: ejecuta `data.sql` después de que Hibernate haya creado las tablas.
- `server.servlet.context-path=/springboot-api`: agrega ese prefijo a todas las rutas HTTP.
- `spring.h2.console.path=/h2-console`: habilita la consola de H2.

La URL de conexión para la consola es `jdbc:h2:mem:sistema-academico`, con usuario `user` y contraseña `password`.

## 5. Definición de entidades y generación de tablas

Las clases ubicadas en `src/main/java/com/compunet/springboot/model` están marcadas con `@Entity`. Hibernate las registra como entidades persistentes y, a partir de ellas, genera el esquema.

### 5.1. Tabla `Estudiante`

`Estudiante` usa `@Table(name = "Estudiante")`. Su estructura se define con:

- `id`: clave primaria generada con `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- `nombre` y `apellido`: columnas obligatorias (`nullable = false`).
- `correo_institucional`: columna obligatoria, única y con longitud máxima de 50 caracteres.
- `active`: columna booleana obligatoria.

La propiedad Java `correoInstitucional` se mapea explícitamente a la columna `correo_institucional` mediante `@Column(name = "correo_institucional")`.

### 5.2. Tabla `Profesor`

`Profesor` usa `@Table(name = "Profesor")` y contiene:

- `id`: clave primaria autogenerada.
- `nombre`, `apellido`, `correo_institucional`, `especialidad`, `departamento` y `active`.
- Restricciones `NOT NULL` y unicidad para el correo, definidas con `@Column`.
- La colección `cursos`, definida con `@OneToMany(mappedBy = "profesor")`.

`mappedBy = "profesor"` indica que la relación es administrada por el atributo `profesor` de la entidad `Curso`. `cascade = CascadeType.ALL` propaga operaciones y `orphanRemoval = true` elimina cursos huérfanos.

### 5.3. Tabla `Curso` y relación con `Profesor`

`Curso` usa `@Table(name = "Curso")` y contiene `id`, `nombre`, `creditos` y `departamento`. El vínculo con el profesor se define así:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "profesor_id", nullable = false)
private Profesor profesor;
```

Esto genera una relación **muchos a uno**:

```text
Profesor (1) -------- (N) Curso
                         |
                         +-- profesor_id -> Profesor.id
```

En la base de datos, `Curso.profesor_id` funciona como clave foránea hacia `Profesor.id`. Por tanto, varios cursos pueden pertenecer al mismo profesor, pero cada curso debe tener un profesor (`nullable = false`). `FetchType.LAZY` indica que el profesor relacionado se carga bajo demanda.

## 6. Datos iniciales

El archivo `src/main/resources/data.sql` inserta:

- Tres estudiantes.
- Cuatro profesores.
- Cuatro cursos asociados a profesores mediante `profesor_id`.

Los `id` usados por los cursos (`1` a `4`) corresponden a los profesores insertados previamente. El orden de inserción es posible porque `data.sql` se ejecuta después de la creación del esquema y porque los profesores se insertan antes que los cursos.

## 7. Repositorios con Spring Data JPA

`CursoRepository` y `ProfesorRepository` son interfaces anotadas con `@Repository` que extienden:

```java
JpaRepository<Curso, Long>
JpaRepository<Profesor, Long>
```

Al extender `JpaRepository`, Spring Data genera automáticamente la implementación y proporciona operaciones como `findAll`, `findById`, `save` y `deleteById`. En este proyecto se declara `findAll()` para obtener todos los registros de cada entidad.

No es necesario escribir una consulta SQL para los `GET`: el repositorio delega la operación a Hibernate, que genera y ejecuta el `SELECT` correspondiente y transforma cada fila en una instancia de la entidad.

## 8. Controller como punto de entrada

El archivo `controller/Controller.java` recibe los repositorios por inyección de dependencias en su constructor.

### Endpoints disponibles

| Método | URL completa | Comportamiento |
| --- | --- | --- |
| `GET` | `http://localhost:8080/springboot-api/` | Verifica que la aplicación está funcionando. |
| `GET` | `http://localhost:8080/springboot-api/cursos` | Ejecuta `cursoRepository.findAll()` y retorna cursos en JSON. |
| `GET` | `http://localhost:8080/springboot-api/profesores` | Ejecuta `profesorRepository.findAll()` y retorna profesores en JSON. |

El recorrido de `/cursos` es:

```text
Solicitud HTTP GET
    -> Controller.getCursos()
    -> CursoRepository.findAll()
    -> Hibernate ejecuta la consulta SQL
    -> filas de Curso se convierten en objetos Curso
    -> Spring MVC responde con JSON
```

El endpoint de profesores sigue el mismo recorrido mediante `ProfesorRepository`.

## 9. Ejecución y comprobación

Desde la carpeta `springboot`:

```bash
./mvnw.cmd spring-boot:run
```

Después de iniciar la aplicación:

1. Abrir `/springboot-api/` para comprobar el estado del servidor.
2. Abrir `/springboot-api/profesores` y verificar los profesores cargados desde `data.sql`.
3. Abrir `/springboot-api/cursos` y verificar el campo `profesor` y la relación con cada curso.
4. Abrir `/springboot-api/h2-console` y conectarse con los datos configurados para revisar las tablas `ESTUDIANTE`, `PROFESOR` y `CURSO`.

La evidencia principal del funcionamiento es que las tablas se generan desde las entidades, la clave foránea `CURSO.PROFESOR_ID` se genera desde `@JoinColumn` y los endpoints consultan los datos mediante repositorios JPA sin SQL escrito en el controlador.

## 10. Resultado esperado

La implementación demuestra la separación de responsabilidades:

- **Modelo:** define la estructura y las relaciones de los datos.
- **Hibernate:** traduce el modelo JPA a SQL y administra la persistencia.
- **Spring Data JPA:** ofrece repositorios con operaciones CRUD.
- **Controller:** expone la información mediante endpoints HTTP `GET`.
- **H2:** permite observar y validar el esquema y los datos durante la ejecución.
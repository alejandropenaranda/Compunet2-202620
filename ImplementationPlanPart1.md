# Implementation Plan: Transición de Configuración XML a Anotaciones en Spring

Este plan detalla los pasos exactos para migrar el manejo de dependencias y el ciclo de vida de los Beans del archivo `applicationContext.xml` a la configuración moderna basada en anotaciones de Spring.

### 1. Eliminar (o comentar) la configuración XML
* **Acción:** Dirigirse a `src/main/resources/applicationContext.xml`.
* **Modificación:** Comentar todo el contenido del archivo o eliminarlo por completo. A partir de ahora, Spring no leerá los Beans desde aquí.

### 2. Crear la Clase de Configuración Principal (`AppConfig`)
* **Acción:** Crear un nuevo archivo llamado `AppConfig.java` dentro del paquete base del proyecto (por ejemplo, `com.example.config`).
* **Modificación:**
    * Anotar la clase con `@Configuration`.
    * Anotar la clase con `@ComponentScan(basePackages = "com.example")` para indicarle a Spring dónde buscar los componentes, servicios y repositorios.

### 3. Migrar el Bean del Repositorio y su Ciclo de Vida
* **Objetivo:** Reemplazar el bean `estudianteRepositoryBean` y sus métodos `init-method` y `destroy-method`.
* **Modificación en `EstudianteRepositoryInMemory.java`:**
    * Anotar la clase con `@Repository`.
    * Anotar el método `iniciarRepositorio()` con `@PostConstruct` (Asegurarse de tener la dependencia `jakarta.annotation-api` en el `pom.xml`).
    * Anotar el método `limpiarRecursos()` con `@PreDestroy`.

### 4. Migrar los Beans de la Capa de Servicio (Inyección de Dependencias)
* **Objetivo:** Reemplazar `estudianteServiceBean` y `estudianteServiceSetterBean`. 
* *Nota para la clase:* Para evitar el error `NoUniqueBeanDefinitionException` (ya que hay dos implementaciones de la misma interfaz), solo una debe estar activa como Componente principal, o usar perfiles.
* **Modificación en `EstudianteServiceImpl.java` (Inyección por Constructor):**
    * Anotar la clase con `@Service`.
    * Anotar el constructor que recibe el `EstudianteRepository` con `@Autowired`.
* **Modificación en `EstudianteServiceSetterImpl.java` (Inyección por Setter):**
    * *Si se usa esta implementación:* Anotar la clase con `@Service`.
    * Anotar el método `setEstudianteRepository(...)` con `@Autowired`.

### 5. Migrar el Bean del Carrito de Matrícula (Manejo de Scopes)
* **Objetivo:** Reemplazar `carritoMatriculoBean` y asignar correctamente su ciclo de vida para que no comparta estado entre sesiones.
* **Modificación en `CarritoMatricula.java`:**
    * Anotar la clase con `@Component`.
    * Anotar la clase con `@Scope("prototype")` para asegurar que cada vez que se inyecte o solicite este Bean, Spring entregue una instancia completamente nueva.

### 6. Actualizar la Inicialización del Contexto en el Servlet/Main
* **Objetivo:** Cambiar la forma en que la aplicación arranca el contenedor de Spring, dejando de leer el XML.
* **Modificación (Si usan el método `init()` manual en el Servlet):**
    * Reemplazar `ClassPathXmlApplicationContext("applicationContext.xml")` por `AnnotationConfigApplicationContext(AppConfig.class)`.
* **Modificación (Si usan `web.xml` con ContextLoaderListener):**
    * Cambiar el `contextClass` a `AnnotationConfigWebApplicationContext`.
    * Cambiar el `contextConfigLocation` para que apunte al paquete/clase `com.example.config.AppConfig`.
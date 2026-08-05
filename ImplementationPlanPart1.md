# Plan de Implementación - Práctica Sesión 4: Scopes de Beans (Singleton vs Prototype)

Este documento detalla exclusivamente las modificaciones y adiciones a realizar sobre el proyecto de la sesión anterior para explicar de forma práctica los Scopes de Spring.

- **Paso 1: Creación de la Entidad Stateful (`CarritoMatricula`)**
  - Ubicación: Crear en el paquete `com.compunet.model`.
  - Atributos a definir:
    - `Estudiante estudiante`
    - `List<String> materias`
  - Constructor: 
    - Inicializar la lista de materias.
    - **Clave pedagógica:** Agregar un `System.out.println` que imprima el Hash de memoria del objeto (`System.identityHashCode(this)`) para evidenciar en consola cada vez que Spring hace un `new`.
  - Métodos: `asignarEstudiante()`, `agregarMateria()`, y getters correspondientes.

- **Paso 2: Modificación del XML (`applicationContext.xml`)**
  - Abrir el archivo existente en `src/main/resources`.
  - Explicar brevemente que los beans de Repositorio y Servicio siguen siendo `singleton` (su comportamiento por defecto, ya que no guardan estado de sesión).
  - Agregar la definición del nuevo bean `carritoMatricula`:
    - **Fase 1 (Para mostrar el error):** Configurar el bean con `scope="singleton"` (o sin el atributo, para demostrar el valor por defecto).
    - **Fase 2 (Para mostrar la solución):** Modificar el bean agregando `scope="prototype"`.

- **Paso 3: Actualización de la clase de pruebas (`Main.java`)**
  - Mantener la inicialización del contexto `ClassPathXmlApplicationContext`.
  - Obtener el `estudianteService` y extraer dos estudiantes distintos (ej. Ana y Carlos).
  - **Simulación del Estudiante 1:**
    - Pedir el bean `carritoMatricula` al contexto (`context.getBean`).
    - Asignar el estudiante Ana y agregarle 2 materias.
    - Imprimir el estado del carrito.
  - **Simulación del Estudiante 2:**
    - Pedir NUEVAMENTE el bean `carritoMatricula` al contexto.
    - Asignar el estudiante Carlos y agregarle 1 materia distinta.
    - Imprimir el estado del carrito.
  - **Verificación en memoria:**
    - Imprimir el resultado de la comparación de instancias: `(carritoAna == carritoCarlos)`.

- **Paso 4: Dinámica de Ejecución en Clase**
  - **Primera ejecución (Singleton):** Mostrar a los estudiantes cómo el carrito de Carlos incluye accidentalmente las materias de Ana. Explicar el riesgo en aplicaciones web concurrentes (fuga de datos).
  - **Segunda ejecución (Prototype):** Tras cambiar el scope en el XML, reejecutar para evidenciar que Spring ahora instancia hashes de memoria diferentes y los carritos son completamente independientes.

  ---

- **Ciclo de Vida de los Beans (Init y Destroy)**
  - **Modificación en el Repositorio (`EstudianteRepositoryInMemory`):**
    - Dejar el constructor vacío.
    - Crear el método `iniciarRepositorio()`: Aquí se deben agregar los 3 estudiantes por defecto (Ana, Carlos, María) y un `System.out.println` con la etiqueta `[LIFECYCLE]` para evidenciar la inicialización.
    - Crear el método `limpiarRecursos()`: Aquí se simula la eliminación de datos o cierre de conexiones, agregando un `System.out.println` con la etiqueta `[LIFECYCLE]`.
  - **Configuración del XML (`applicationContext.xml`):**
    - En la definición del bean del repositorio, enlazar los métodos creados usando los atributos de ciclo de vida.
    - El bean debe quedar estructurado así:
      `<bean id="estudianteRepository" class="com.compunet.repository.EstudianteRepositoryInMemory" init-method="iniciarRepositorio" destroy-method="limpiarRecursos" />`
  - **Ejecución y Cierre del Contexto en el `Main.java`:**
    - Al inicio de la ejecución del programa, los estudiantes notarán en la consola que el método `iniciarRepositorio()` se ejecuta automáticamente al instanciar el contenedor.
    - **Paso Clave:** Para que Spring invoque el método de destrucción (`limpiarRecursos`), se debe cerrar el contexto explícitamente al final del programa.
    - Agregar al final del `main` la línea: `((ClassPathXmlApplicationContext) context).close();` (o alternativamente `registerShutdownHook()`).
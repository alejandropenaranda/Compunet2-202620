# Hoja de Ruta Práctica — Clase 2: Props, Memoria e Interactividad (`useState` y `useEffect`)

> **Curso:** Computación en Internet II (Universidad Icesi)  
> **Duración:** 2 horas (120 minutos)  
> **Estructura Didáctica:** 4 Macro-Partes (Diapositivas Teóricas $\longrightarrow$ Codificación en Vivo en VS Code).  
> **Material de Soporte:**  
> - `slides/React/Material/introduccion-react-ampliado.pdf` (Diapositivas 60 a 76: Tu primer componente y Props).  
> - `slides/React/Material/semana-15-hooks-router.pdf` (Diapositivas 1 a 51: `useState`, `useEffect` y Custom Hooks).

---

## Estructura General de la Clase (4 Grandes Partes)

| Parte | Tema Central | Diapositivas a Proyectar | Práctica en Vivo en VS Code | Tiempo |
| :---: | :--- | :--- | :--- | :---: |
| **Parte 1** | **Finalizar Slides de Clase 1 (Props) + Mini-Práctica** | `introduccion-react-ampliado.pdf`<br>(Slides 60 a 76) | Enriquecer `CursoCard.jsx` (prop condicional `destacado`, badge) y `CursoList.jsx` (delegación de callback). | **25 min** |
| **Parte 2** | **Teoría de `useState` + Codificación Reactiva** | `semana-15-hooks-router.pdf`<br>(Slides 1 a 24) | En `App.jsx`: primer estado (`cursoSeleccionado`) con banner, componente `CursoFiltro.jsx` con estado derivado, y formulario con inmutabilidad. | **40 min** |
| **Parte 3** | **Teoría de `useEffect` + Codificación Asíncrona** | `semana-15-hooks-router.pdf`<br>(Slides 25 a 40) | En `App.jsx`: ciclo de vida asíncrono con temporizador de 1.2s, *cleanup* con `clearTimeout`, banderas `isLoading`/`error` y verificación en navegador. | **40 min** |
| **Parte 4** | **Teoría de Custom Hooks + Extracción y Cierre** | `semana-15-hooks-router.pdf`<br>(Slides 41 a 51) | Crear `src/hooks/useCursos.js` para desacoplar la lógica de estado y preparar la conexión con Axios y React Router (Clase 3). | **15 min** |

---

## Regla de Oro: Cero Estilos en Medio de la Codificación

Todos los estilos visuales residen exclusivamente en `src/index.css`. Durante la codificación en vivo **no se escribe ningún objeto inline (`style={{ ... }}`)**. Se utilizan únicamente clases semánticas directas (`.card`, `.card-destacado`, `.card-top-row`, `.badge-troncal`, `.card-actions`, `.filtro-container`, `.banner-seleccion`, `.btn-close-banner`, `.form-curso`).

---

## Aspectos Clave a Tener en Cuenta

1. **Flujo Unidireccional de Props (Top-Down):**
   - Los **datos descienden**: El padre pasa propiedades a los hijos.
   - Los **eventos ascienden**: Si un hijo necesita avisar de una acción, el padre le pasa una **función callback por prop** (ej. `onSeleccionar={manejarSeleccion}`).
   - Las props son **inmutables** (solo lectura en el hijo).
2. **Variables Locales vs Estado (`useState`):**
   - Las variables locales mueren entre re-renders y no le notifican a React que debe redibujar.
   - `useState` almacena el valor en la memoria interna de React fuera de la función del componente y dispara un nuevo ciclo de render quirúrgico.
3. **Inmutabilidad Estricta:**
   - Incorrecto: `cursos.push(nuevoCurso)` (mutación directa: React no detecta la actualización).
   - Correcto: `setCursos(prev => [nuevoCurso, ...prev])` (nuevo arreglo generado con el operador spread).
4. **Estado Derivado vs Estado Redundante:**
   - No duplicar estados con `[cursosFiltrados, setCursosFiltrados]`. Se mantiene `cursos` y `filtro`, calculando `cursosFiltrados = cursos.filter(...)` al vuelo en el renderizado.
5. **Ciclo de Vida y Arreglo de Dependencias en `useEffect`:**
   - `useEffect(() => {}, [])`: Se ejecuta **una sola vez** tras el montaje inicial (ideal para peticiones HTTP).
   - En desarrollo, `<StrictMode>` ejecuta los efectos dos veces para validar que las funciones de limpieza (*cleanup*) funcionen correctamente.

---

## Parte 1: Finalizar Slides de Clase 1 (Props) y Codificar Mini-Práctica (25 min)

### 1.1 Bloque Teórico en Diapositivas
> [!IMPORTANT]
> **Proyectar y Explicar en Pantalla:**  
> `slides/React/Material/introduccion-react-ampliado.pdf` — **Diapositivas 60 a 76**  
> - Estructura de carpetas en `src/` (Slides 60–63).
> - Anatomía de un archivo `.jsx`, función de componente, `return` y llaves `{}` para evaluar JavaScript (Slides 64–71).
> - Capítulo 8: ¿Qué son las Props? Flujo unidireccional de datos, inmutabilidad de solo lectura y paso de callbacks (Slides 72–76).

### 1.2 Codificación en Vivo en VS Code

Demostraremos los dos pilares de las Props sin escribir estilos inline:
1. **Paso de datos condicionales:** La prop booleana `destacado`.
2. **Paso de callbacks (funciones):** La prop `onSeleccionar` para comunicar eventos del hijo al padre.

#### A. Actualización de `src/components/CursoCard.jsx`

> [!NOTE]
> **Cambios frente a Clase 1:** Se agregan las props `destacado = false` y `onSeleccionar`, la clase condicional `.card-destacado`, el badge `.badge-troncal` y el botón con evento `onClick`.

```jsx
// src/components/CursoCard.jsx

// >>> [MODIFICADO]: Se agregan las props 'destacado' (default false) y 'onSeleccionar' (callback)
export default function CursoCard({ curso, destacado = false, onSeleccionar }) {
  const { id, nombre, creditos, departamento, profesor } = curso;

  return (
    // >>> [MODIFICADO]: Clase condicional 'card-destacado' según la prop 'destacado'
    <div className={`card ${destacado ? 'card-destacado' : ''}`}>
      
      {/* >>> [NUEVO]: Fila superior con título y badge de asignatura troncal condicional */}
      <div className="card-top-row">
        <h3>{nombre}</h3>
        {destacado && <span className="badge-troncal">Troncal</span>}
      </div>

      <p className="card-subtitle">
        <strong>Créditos:</strong> {creditos} · <strong>Depto:</strong> {departamento}
      </p>
      <p className="card-profesor">
        <strong>Docente:</strong> {profesor || 'Sin profesor asignado'}
      </p>

      <div className="card-actions">
        {/* >>> [MODIFICADO]: El botón delega el evento al padre ejecutando onSeleccionar */}
        <button
          className="btn btn-primary"
          onClick={() => onSeleccionar && onSeleccionar(nombre)}
        >
          Seleccionar
        </button>
      </div>
    </div>
  );
}
```

#### B. Actualización de `src/components/CursoList.jsx`

> [!NOTE]
> **Cambios frente a Clase 1:** Se recibe `onSeleccionarCurso`, se usa `index` para marcar el primer curso como `destacado`, y se conecta el callback a cada tarjeta.

```jsx
// src/components/CursoList.jsx
import CursoCard from './CursoCard';

// >>> [MODIFICADO]: Se recibe la prop callback 'onSeleccionarCurso' delegada desde App.jsx
export default function CursoList({ cursos, onSeleccionarCurso }) {
  if (!cursos || cursos.length === 0) {
    return <p className="card-subtitle">No hay cursos registrados en el catálogo.</p>;
  }

  return (
    <div className="grid-cursos">
      {/* >>> [MODIFICADO]: Se añade el parámetro 'index' para identificar el primer elemento */}
      {cursos.map((c, index) => (
        <CursoCard
          key={c.id}
          curso={c}
          // >>> [NUEVO]: Envía true solo al primer curso de la lista
          destacado={index === 0}
          // >>> [NUEVO]: Delega la función callback hacia el componente hijo
          onSeleccionar={onSeleccionarCurso}
        />
      ))}
    </div>
  );
}
```

---

## Parte 2: Teoría de `useState` y Codificación Reactiva (40 min)

### 2.1 Bloque Teórico en Diapositivas
> [!IMPORTANT]
> **Proyectar y Explicar en Pantalla:**  
> `slides/React/Material/semana-15-hooks-router.pdf` — **Diapositivas 1 a 24**  
> - El dilema del contador con variables normales: las variables locales mueren en el render y no le avisan a React (Slides 3–7).
> - `useState` almacena el valor fuera de la función y devuelve `[valor, setValor]` (Slides 8–11).
> - Cada instancia tiene su propio estado y React ejecuta renders quirúrgicos (Slides 12–16).
> - El estado es un *snapshot*, la función actualizadora `prev => ...`, inmutabilidad estricta (`.push` prohibido, spread `[nuevo, ...prev]`) y reglas de los hooks (Slides 17–24).

### 2.2 Codificación en Vivo en VS Code (Evolución Incremental de `src/App.jsx`)

Para que la codificación sea fluida y didáctica, **no modificamos todo `App.jsx` de golpe**. Lo transformamos en **3 incrementos paso a paso**:

```
Jerarquía Visual final en el JSX de App.jsx:
<main className="container">
  <h1>Catálogo de Asignaturas</h1>
  ┌── Incremento 1: Banner de selección activa ({cursoSeleccionado && ...})
  ├── Incremento 3: Formulario de nueva asignatura (<form className="form-curso">...)
  ├── Incremento 2: Buscador reactivo (<CursoFiltro ... />)
  └── Incremento 4 (Parte 3): Estados de carga y Lista ({!isLoading && <CursoList ... />})
</main>
```

---

#### Incremento 1: Memoria de Selección y Banner (`cursoSeleccionado`)

> [!NOTE]
> **Objetivo:** Recibir el callback del hijo al hacer clic en "Seleccionar" y mostrarlo en un banner superior.
> - **Arriba en `App.jsx`:** Importar `useState` y declarar `const [cursoSeleccionado, setCursoSeleccionado] = useState(null);`.
> - **Ubicación en el JSX:** Inmediatamente **debajo de `<h1>` y antes de `<CursoList />`**.

```jsx
// src/App.jsx — Estado tras el Incremento 1

import { useState } from 'react';
import Navbar from './components/Navbar';
import CursoList from './components/CursoList';
import { MOCK_CURSOS } from './data/mockCursos';

export default function App() {
  // >>> [NUEVO]: Primer estado — Almacena la asignatura seleccionada
  const [cursoSeleccionado, setCursoSeleccionado] = useState(null);

  return (
    <div>
      <Navbar />
      <main className="container">
        <h1>Catálogo de Asignaturas</h1>

        {/* >>> [NUEVO]: 1. Banner interactivo colocado justo debajo del título */}
        {cursoSeleccionado && (
          <div className="status-box status-loading banner-seleccion">
            <span>Asignatura seleccionada para seguimiento: <strong>{cursoSeleccionado}</strong></span>
            <button className="btn-close-banner" onClick={() => setCursoSeleccionado(null)}>
              Cerrar
            </button>
          </div>
        )}

        {/* >>> [MODIFICADO]: Pasamos el callback que actualiza el estado */}
        <CursoList
          cursos={MOCK_CURSOS}
          onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
        />
      </main>
    </div>
  );
}
```

---

#### Incremento 2: Buscador Reactivo (`CursoFiltro.jsx`) y Estado Derivado

> [!NOTE]
> **Objetivo:** Filtrar la lista en vivo según lo que el usuario tipea en una caja de texto desacoplada.
> 1. Crear primero el componente controlado `src/components/CursoFiltro.jsx`.
> 2. En `src/App.jsx`:
>    - **Import:** `import CursoFiltro from './components/CursoFiltro';`
>    - **Estado:** `const [filtro, setFiltro] = useState('');`
>    - **Cálculo derivado (antes del `return`):** `cursosFiltrados = MOCK_CURSOS.filter(...)`
>    - **Ubicación en el JSX:** Colocar `<CursoFiltro />` **debajo del banner y justo arriba de `<CursoList />`**, y pasarle `cursosFiltrados` a la lista.

**1. Crear `src/components/CursoFiltro.jsx`:**
```jsx
// src/components/CursoFiltro.jsx
export default function CursoFiltro({ filtro, onFiltroChange }) {
  return (
    <div className="filtro-container">
      <input
        type="text"
        className="input-search"
        placeholder="Buscar curso o departamento..."
        value={filtro}
        onChange={(e) => onFiltroChange(e.target.value)}
      />
    </div>
  );
}
```

**2. Actualización de `src/App.jsx`:**
```jsx
// src/App.jsx — Estado tras el Incremento 2

import { useState } from 'react';
import Navbar from './components/Navbar';
import CursoList from './components/CursoList';
import { MOCK_CURSOS } from './data/mockCursos';
// >>> [NUEVO]: Importar el componente de búsqueda
import CursoFiltro from './components/CursoFiltro';

export default function App() {
  const [cursoSeleccionado, setCursoSeleccionado] = useState(null);
  // >>> [NUEVO]: Segundo estado para controlar el término de búsqueda
  const [filtro, setFiltro] = useState('');

  // >>> [NUEVO]: Cálculo de estado derivado al vuelo (sin duplicar estado)
  const cursosFiltrados = MOCK_CURSOS.filter((c) =>
    c.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    c.departamento.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div>
      <Navbar />
      <main className="container">
        <h1>Catálogo de Asignaturas</h1>

        {/* 1. Banner de selección activa */}
        {cursoSeleccionado && (
          <div className="status-box status-loading banner-seleccion">
            <span>Asignatura seleccionada para seguimiento: <strong>{cursoSeleccionado}</strong></span>
            <button className="btn-close-banner" onClick={() => setCursoSeleccionado(null)}>
              Cerrar
            </button>
          </div>
        )}

        {/* >>> [NUEVO]: 2. Buscador reactivo colocado entre el banner y la lista */}
        <CursoFiltro filtro={filtro} onFiltroChange={setFiltro} />

        {/* >>> [MODIFICADO]: Ahora recibe la lista filtrada al vuelo */}
        <CursoList
          cursos={cursosFiltrados}
          onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
        />
      </main>
    </div>
  );
}
```

---

#### Incremento 3: Formulario de Registro e Inmutabilidad con `useState`

> [!NOTE]
> **Objetivo:** Permitir agregar nuevas asignaturas a la lista sin mutar el arreglo original.
> - **Convertir `cursos` en estado:** Reemplazar la constante estática por `const [cursos, setCursos] = useState(MOCK_CURSOS);`.
> - **Estados para inputs:** `nuevoNombre`, `nuevosCreditos`, `nuevoDepto`.
> - **Manejador `handleAgregarCurso`:** Inmutabilidad con spread `setCursos((prev) => [nuevo, ...prev]);`.
> - **Actualizar filtro:** Ahora se filtra sobre `cursos`: `cursos.filter(...)`.
> - **Ubicación en el JSX:** El `<form className="form-curso">` se ubica **inmediatamente debajo del banner y justo encima del buscador `<CursoFiltro />`**.

```jsx
// src/App.jsx — Estado completo al finalizar la Parte 2 (useState)

import { useState } from 'react';
import Navbar from './components/Navbar';
import CursoList from './components/CursoList';
import { MOCK_CURSOS } from './data/mockCursos';
import CursoFiltro from './components/CursoFiltro';

export default function App() {
  // >>> [MODIFICADO]: cursos ahora es un estado mutable reactivamente (inicia con MOCK_CURSOS)
  const [cursos, setCursos] = useState(MOCK_CURSOS);
  const [filtro, setFiltro] = useState('');
  const [cursoSeleccionado, setCursoSeleccionado] = useState(null);

  // >>> [NUEVO]: Estados locales para cada campo del formulario
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [nuevosCreditos, setNuevosCreditos] = useState(3);
  const [nuevoDepto, setNuevoDepto] = useState('');

  // >>> [NUEVO]: Manejador del submit con inmutabilidad estricta
  const handleAgregarCurso = (e) => {
    e.preventDefault();
    if (!nuevoNombre.trim()) return;

    const nuevo = {
      id: Date.now(),
      nombre: nuevoNombre,
      creditos: Number(nuevosCreditos),
      departamento: nuevoDepto,
      profesor: 'Profesor Pendiente'
    };

    // Operador spread: nuevo arreglo con el elemento al inicio (prohibido cursos.push)
    setCursos((prev) => [nuevo, ...prev]);

    // Limpieza de inputs
    setNuevoNombre('');
    setNuevoDepto('');
    setNuevosCreditos(3);
  };

  // Filtro sobre el estado reactivo 'cursos'
  const cursosFiltrados = cursos.filter((c) =>
    c.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    c.departamento.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div>
      <Navbar />
      <main className="container">
        <h1>Catálogo de Asignaturas</h1>

        {/* 1. Banner de selección activa */}
        {cursoSeleccionado && (
          <div className="status-box status-loading banner-seleccion">
            <span>Asignatura seleccionada para seguimiento: <strong>{cursoSeleccionado}</strong></span>
            <button className="btn-close-banner" onClick={() => setCursoSeleccionado(null)}>
              Cerrar
            </button>
          </div>
        )}

        {/* >>> [NUEVO]: 2. Formulario de registro (ubicado entre el banner y el buscador) */}
        <form onSubmit={handleAgregarCurso} className="form-curso">
          <h3>Registrar Asignatura</h3>
          <input
            placeholder="Nombre de la asignatura"
            value={nuevoNombre}
            onChange={(e) => setNuevoNombre(e.target.value)}
            required
          />
          <input
            type="number"
            placeholder="Créditos"
            value={nuevosCreditos}
            onChange={(e) => setNuevosCreditos(e.target.value)}
            required
          />
          <input
            placeholder="Departamento (ej. TIC)"
            value={nuevoDepto}
            onChange={(e) => setNuevoDepto(e.target.value)}
            required
          />
          <button type="submit" className="btn btn-success">
            Agregar a la Lista
          </button>
        </form>

        {/* 3. Buscador reactivo */}
        <CursoFiltro filtro={filtro} onFiltroChange={setFiltro} />

        {/* 4. Lista filtrada */}
        <CursoList
          cursos={cursosFiltrados}
          onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
        />
      </main>
    </div>
  );
}
```

---

## Parte 3: Teoría de `useEffect` y Codificación Asíncrona (40 min)

### 3.1 Bloque Teórico en Diapositivas
> [!IMPORTANT]
> **Proyectar y Explicar en Pantalla:**  
> `slides/React/Material/semana-15-hooks-router.pdf` — **Diapositivas 25 a 40**  
> - Renderizar es puro; los efectos secundarios van después (Slides 25–26).
> - Anatomía del efecto: Setup, Limpieza (*cleanup*) y Arreglo de Dependencias (Slides 27–31).
> - Comparativa del arreglo de dependencias: `[]` (montaje único), `[dep]` (al cambiar dependencia) y sin arreglo (en cada render) (Slides 32–37).
> - ¿Por qué corre dos veces en desarrollo con `<StrictMode>`? (Slide 38).
> - Un efecto para solicitar datos asíncronos (Slide 39).

### 3.2 Codificación en Vivo en VS Code (Incremento 4: Asincronía con `useEffect`)

> [!NOTE]
> **Objetivo:** Simular la petición HTTP hacia un backend, mostrando un estado de carga mientras los datos viajan y garantizando la función de limpieza (*cleanup*).
> 
> **Modificaciones en `src/App.jsx`:**
> 1. **Importación:** Añadir `useEffect` a la línea 1: `import { useState, useEffect } from 'react';`.
> 2. **Estado inicial de datos:** `cursos` ya no inicia con `MOCK_CURSOS`, arranca vacío: `const [cursos, setCursos] = useState([]);`.
> 3. **Nuevos estados de feedback:** `const [isLoading, setIsLoading] = useState(true);` y `const [error, setError] = useState(null);`.
> 4. **El Hook `useEffect`:** Se ubica justo después de los estados y antes de `handleAgregarCurso`:
>    ```javascript
>    useEffect(() => {
>      const timer = setTimeout(() => {
>        setCursos(MOCK_CURSOS);
>        setIsLoading(false);
>      }, 1200);
> 
>      return () => clearTimeout(timer);
>    }, []);
>    ```
> 5. **Ubicación en el JSX (`return`):** Se insertan los bloques de feedback **inmediatamente después del buscador `<CursoFiltro />`**, y se envuelve `<CursoList />` en una condición para que solo renderice cuando la carga haya terminado:
>    ```jsx
>    {/* 3. Buscador */}
>    <CursoFiltro filtro={filtro} onFiltroChange={setFiltro} />
> 
>    {/* >>> [NUEVO]: Feedback de carga mientras isLoading sea true */}
>    {isLoading && (
>      <div className="status-box status-loading">
>        Cargando oferta académica desde el servidor...
>      </div>
>    )}
> 
>    {/* >>> [NUEVO]: Feedback visual de errores */}
>    {error && (
>      <div className="status-box status-error">
>        Error: {error}
>      </div>
>    )}
> 
>    {/* >>> [MODIFICADO]: La lista solo se pinta cuando terminó de cargar y no hay error */}
>    {!isLoading && !error && (
>      <CursoList
>        cursos={cursosFiltrados}
>        onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
>      />
>    )}
>    ```

### 3.3 Código Ensamblado Completo de `src/App.jsx` tras el Incremento 4

```jsx
// src/App.jsx — Estado completo integrado al finalizar la Parte 3 (Cero estilos inline)

import { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import CursoList from './components/CursoList';
import CursoFiltro from './components/CursoFiltro';
import { MOCK_CURSOS } from './data/mockCursos';

export default function App() {
  // === Estados de datos y ciclo de vida asíncrono ===
  const [cursos, setCursos] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // === Estados de interactividad (selección y búsqueda) ===
  const [filtro, setFiltro] = useState('');
  const [cursoSeleccionado, setCursoSeleccionado] = useState(null);

  // === Estados del formulario de registro ===
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [nuevosCreditos, setNuevosCreditos] = useState(3);
  const [nuevoDepto, setNuevoDepto] = useState('');

  // === Efecto de carga inicial simulando API HTTP ===
  useEffect(() => {
    const timer = setTimeout(() => {
      setCursos(MOCK_CURSOS);
      setIsLoading(false);
    }, 1200);

    return () => clearTimeout(timer);
  }, []);

  // === Manejador de adición con inmutabilidad estricta ===
  const handleAgregarCurso = (e) => {
    e.preventDefault();
    if (!nuevoNombre.trim()) return;

    const nuevo = {
      id: Date.now(),
      nombre: nuevoNombre,
      creditos: Number(nuevosCreditos),
      departamento: nuevoDepto,
      profesor: 'Profesor Pendiente'
    };

    setCursos((prev) => [nuevo, ...prev]);
    setNuevoNombre('');
    setNuevoDepto('');
    setNuevosCreditos(3);
  };

  // === Estado derivado calculado al vuelo ===
  const cursosFiltrados = cursos.filter((c) =>
    c.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    c.departamento.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div>
      <Navbar />

      <main className="container">
        <h1>Catálogo de Asignaturas</h1>

        {/* Banner interactivo de la asignatura seleccionada */}
        {cursoSeleccionado && (
          <div className="status-box status-loading banner-seleccion">
            <span>Asignatura seleccionada para seguimiento: <strong>{cursoSeleccionado}</strong></span>
            <button className="btn-close-banner" onClick={() => setCursoSeleccionado(null)}>
              Cerrar
            </button>
          </div>
        )}

        {/* Formulario controlado de nueva asignatura */}
        <form onSubmit={handleAgregarCurso} className="form-curso">
          <h3>Registrar Asignatura</h3>
          <input
            placeholder="Nombre de la asignatura"
            value={nuevoNombre}
            onChange={(e) => setNuevoNombre(e.target.value)}
            required
          />
          <input
            type="number"
            placeholder="Créditos"
            value={nuevosCreditos}
            onChange={(e) => setNuevosCreditos(e.target.value)}
            required
          />
          <input
            placeholder="Departamento (ej. TIC)"
            value={nuevoDepto}
            onChange={(e) => setNuevoDepto(e.target.value)}
            required
          />
          <button type="submit" className="btn btn-success">
            Agregar a la Lista
          </button>
        </form>

        {/* Componente de búsqueda reactiva */}
        <CursoFiltro filtro={filtro} onFiltroChange={setFiltro} />

        {/* Mensaje de carga asíncrona */}
        {isLoading && (
          <div className="status-box status-loading">
            Cargando oferta académica desde el servidor...
          </div>
        )}

        {/* Mensaje de error condicional */}
        {error && (
          <div className="status-box status-error">
            Error: {error}
          </div>
        )}

        {/* Lista de tarjetas renderizada tras completar la carga */}
        {!isLoading && !error && (
          <CursoList
            cursos={cursosFiltrados}
            onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
          />
        )}
      </main>
    </div>
  );
}
```

> [!TIP]
> **Verificación en el Navegador:**  
> Al recargar la página (`F5`), se muestra la caja azul de carga durante 1.2 segundos; luego se pinta la lista con el primer curso destacado en borde azul; al hacer clic en *"Seleccionar"* aparece el banner superior con el botón de píldora; al escribir en la barra de búsqueda se filtra en vivo; y al registrar un curso se agrega arriba instantáneamente.

---

## Parte 4: Teoría de Custom Hooks, Extracción y Cierre (15 min)

### 4.1 Bloque Teórico en Diapositivas
> [!IMPORTANT]
> **Proyectar y Explicar en Pantalla:**  
> `slides/React/Material/semana-15-hooks-router.pdf` — **Diapositivas 41 a 51**  
> - ¿Cuándo varios componentes repiten la misma lógica? Funciones que inician con `use*` (Slides 41–43).
> - Un custom hook puede contener estado (`useState`), sincronizarse (`useEffect`) y limpiar recursos (Slides 44–46).
> - Usarlo cabe en una línea limpia (Slide 48).
> - Regla de Oro: **Se comparte la lógica, no el estado**. Cada componente que llama al custom hook crea su propia memoria aislada (Slide 50).

### 4.2 Codificación en Vivo: Extracción y Consumo del Custom Hook

Demostraremos la regla de oro de la diapositiva 50: **"Se comparte la lógica, no el estado"**. Extraeremos la gestión de datos fuera del componente visual en 3 pasos:

#### Paso A: Crear `src/hooks/useCursos.js`

Creamos el archivo `src/hooks/useCursos.js` trasladando allí los estados de `cursos`, `isLoading`, `error`, el `useEffect` asíncrono y la función mutadora `agregarCurso`:

```javascript
// src/hooks/useCursos.js
import { useState, useEffect } from 'react';
import { MOCK_CURSOS } from '../data/mockCursos';

export function useCursos() {
  const [cursos, setCursos] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Ciclo de vida asíncrono encapsulado dentro del hook
  useEffect(() => {
    const timer = setTimeout(() => {
      setCursos(MOCK_CURSOS);
      setIsLoading(false);
    }, 1200);

    return () => clearTimeout(timer);
  }, []);

  // Lógica de adición con inmutabilidad encapsulada
  const agregarCurso = (nuevoCurso) => {
    setCursos((prev) => [nuevoCurso, ...prev]);
  };

  // Exponemos únicamente lo que los componentes visuales necesitan
  return { cursos, isLoading, error, agregarCurso };
}
```

---

#### Paso B: Refactorización de `src/App.jsx` (¿Qué se elimina y qué se queda?)

> [!NOTE]
> **1. Qué ELIMINAMOS de `src/App.jsx`:**
> - `import { useEffect } from 'react'` $\longrightarrow$ `App` ya no ejecuta efectos directamente.
> - `import { MOCK_CURSOS }` $\longrightarrow$ `App` ya no conoce los datos simulados.
> - `const [cursos, setCursos] = useState([])` $\longrightarrow$ Ahora lo gestiona el hook.
> - `const [isLoading, setIsLoading] = useState(true)` $\longrightarrow$ Ahora lo gestiona el hook.
> - `const [error, setError] = useState(null)` $\longrightarrow$ Ahora lo gestiona el hook.
> - Todo el bloque `useEffect(() => { ... }, [])` $\longrightarrow$ Reemplazado por el hook.
> - `setCursos((prev) => [nuevo, ...prev])` $\longrightarrow$ Se sustituye por `agregarCurso(nuevo)`.
> 
> **2. Qué AGREGAMOS en `src/App.jsx`:**
> - `import { useCursos } from './hooks/useCursos';`
> - Una sola línea declarativa: `const { cursos, isLoading, error, agregarCurso } = useCursos();`
> 
> **3. Qué se QUEDA INTACTO en `src/App.jsx`:**
> - La memoria de la interfaz: `cursoSeleccionado` (banner de seguimiento).
> - El estado del filtro: `filtro` y el cálculo derivado `cursosFiltrados`.
> - Los estados locales de los inputs del formulario: `nuevoNombre`, `nuevosCreditos`, `nuevoDepto`.
> - Todo el árbol visual JSX del `return (...)` (Navbar, Banner, Formulario, Buscador y Lista).

---

#### Paso C: Código Completo Final de `src/App.jsx` (Consumiendo el Custom Hook)

```jsx
// src/App.jsx — Versión final refactorizada consumiendo useCursos()

import { useState } from 'react';
import Navbar from './components/Navbar';
import CursoList from './components/CursoList';
import CursoFiltro from './components/CursoFiltro';
// >>> [NUEVO]: Importar nuestro propio Custom Hook
import { useCursos } from './hooks/useCursos';

export default function App() {
  // >>> [NUEVO]: 1 línea reemplaza 25 líneas de gestión de datos, useEffect y estados de carga
  const { cursos, isLoading, error, agregarCurso } = useCursos();

  // === Estados que SÍ pertenecen a la interfaz de esta vista ===
  const [filtro, setFiltro] = useState('');
  const [cursoSeleccionado, setCursoSeleccionado] = useState(null);

  // Estados locales para los campos del formulario
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [nuevosCreditos, setNuevosCreditos] = useState(3);
  const [nuevoDepto, setNuevoDepto] = useState('');

  // Manejador del submit delegando la acción al hook
  const handleAgregarCurso = (e) => {
    e.preventDefault();
    if (!nuevoNombre.trim()) return;

    // Delegamos la actualización del arreglo a la función del Custom Hook
    agregarCurso({
      id: Date.now(),
      nombre: nuevoNombre,
      creditos: Number(nuevosCreditos),
      departamento: nuevoDepto,
      profesor: 'Profesor Pendiente'
    });

    // Limpieza de inputs
    setNuevoNombre('');
    setNuevoDepto('');
    setNuevosCreditos(3);
  };

  // Estado derivado: filtrado al vuelo
  const cursosFiltrados = cursos.filter((c) =>
    c.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    c.departamento.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div>
      <Navbar />

      <main className="container">
        <h1>Catálogo de Asignaturas</h1>

        {/* 1. Banner interactivo de la asignatura seleccionada */}
        {cursoSeleccionado && (
          <div className="status-box status-loading banner-seleccion">
            <span>Asignatura seleccionada para seguimiento: <strong>{cursoSeleccionado}</strong></span>
            <button className="btn-close-banner" onClick={() => setCursoSeleccionado(null)}>
              Cerrar
            </button>
          </div>
        )}

        {/* 2. Formulario controlado de nueva asignatura */}
        <form onSubmit={handleAgregarCurso} className="form-curso">
          <h3>Registrar Asignatura</h3>
          <input
            placeholder="Nombre de la asignatura"
            value={nuevoNombre}
            onChange={(e) => setNuevoNombre(e.target.value)}
            required
          />
          <input
            type="number"
            placeholder="Créditos"
            value={nuevosCreditos}
            onChange={(e) => setNuevosCreditos(e.target.value)}
            required
          />
          <input
            placeholder="Departamento (ej. TIC)"
            value={nuevoDepto}
            onChange={(e) => setNuevoDepto(e.target.value)}
            required
          />
          <button type="submit" className="btn btn-success">
            Agregar a la Lista
          </button>
        </form>

        {/* 3. Componente de búsqueda reactiva */}
        <CursoFiltro filtro={filtro} onFiltroChange={setFiltro} />

        {/* 4. Mensajes de feedback visual expuestos por useCursos */}
        {isLoading && (
          <div className="status-box status-loading">
            Cargando oferta académica desde el servidor...
          </div>
        )}

        {error && (
          <div className="status-box status-error">
            Error: {error}
          </div>
        )}

        {/* 5. Lista de tarjetas renderizada tras completar la carga */}
        {!isLoading && !error && (
          <CursoList
            cursos={cursosFiltrados}
            onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
          />
        )}
      </main>
    </div>
  );
}
```

### 4.3 Puente hacia la Clase 3
- Explicar a la clase que en la **Clase 3** reemplazaremos el temporizador simulado de `useCursos` por llamadas HTTP reales hacia el backend Spring Boot (`/api/v1/cursos`) usando **Axios**, y estructuraremos la aplicación como una SPA con **React Router DOM**.

---

## Checklist de Verificación al Finalizar la Clase 2

- [ ] **Props en Acción (Parte 1):** `CursoCard` recibe y renderiza la prop `destacado` (`.card-destacado`) y delega el callback `onSeleccionar` hacia el padre `App.jsx`.
- [ ] **Estado Local (`useState`, Parte 2):** La asignatura seleccionada se almacena en memoria y muestra el banner con botón de cierre `.btn-close-banner`.
- [ ] **Búsqueda Reactiva (Parte 2):** El input en `CursoFiltro` actualiza el filtro en tiempo real mediante props y calcula `cursosFiltrados` al vuelo.
- [ ] **Inmutabilidad (Parte 2):** El formulario agrega un nuevo curso mediante `[nuevo, ...prev]` sin mutar el arreglo original.
- [ ] **Ciclo de Vida (`useEffect`, Parte 3):** Al recargar, se visualiza el estado de carga durante 1.2 segundos y luego se renderiza el catálogo con *cleanup* configurado.
- [ ] **Custom Hook (Parte 4):** La lógica de gestión de cursos queda desacoplada en `src/hooks/useCursos.js`.
- [ ] **Cero Estilos Inline:** No existe ningún atributo `style={{ ... }}` en ninguno de los componentes; todo el diseño proviene de `src/index.css`.

# Hoja de Ruta Práctica — Clase 2: Memoria e Interactividad (`useState` y `useEffect`)

> **Curso:** Computación en Internet II (Universidad Icesi)  
> **Duración:** 2 horas  
> **Tema:** Manejo del Estado Local (`useState`), Inmutabilidad Estricta, Búsqueda en Tiempo Real y Ciclo de Vida Asíncrono (`useEffect`).

---

## Aspectos Clave a Tener en Cuenta

1. **Inmutabilidad Estricta:** En React, el estado nunca se muta directamente.
   - Incorrecto: `cursos.push(nuevoCurso)` (no provoca re-renderizado porque la referencia del arreglo en memoria no cambia).
   - Correcto: `setCursos(prev => [nuevoCurso, ...prev])` (se crea un arreglo nuevo mediante el operador spread).
2. **Estado Derivado vs Estado Redundante:**
   - No crear un estado secundario `[cursosFiltrados, setCursosFiltrados]`.
   - Mantener el arreglo base en `cursos`, el término de búsqueda en `filtro`, y calcular `cursosFiltrados = cursos.filter(...)` al vuelo durante el renderizado.
3. **El Arreglo de Dependencias en `useEffect`:**
   - `useEffect(() => {}, [])`: Se ejecuta **una única vez** al montar el componente (ideal para peticiones de carga inicial).
   - `useEffect(() => {}, [filtro])`: Se ejecuta al montar y cada vez que `filtro` cambia.
   - `useEffect(() => {})` (sin arreglo): Se ejecuta en **cada render** (riesgo de bucle infinito si muta estado dentro).
4. **Función de Limpieza (Cleanup):**
   - Retornar una función dentro de `useEffect` (`return () => clearTimeout(timer)`) para evitar fugas de memoria o peticiones colgadas si el componente se desmonta.

---

## Paso 1: Componente de Búsqueda Controlada (20 min)

Crear `src/components/CursoFiltro.jsx`. Recibe el valor del filtro y la función de actualización como props:

```jsx
// src/components/CursoFiltro.jsx
export default function CursoFiltro({ filtro, onFiltroChange }) {
  return (
    <div style={{ marginBottom: '1.5rem' }}>
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

---

## Paso 2: Búsqueda Reactiva y Estado Derivado (20 min)

En `src/App.jsx`, vincular el término de búsqueda con el filtrado del arreglo:

```jsx
// Fragmento conceptual en App.jsx
const [filtro, setFiltro] = useState('');

// Calculo derivado sin crear estados redundantes:
const cursosFiltrados = cursos.filter((c) =>
  c.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
  c.departamento.toLowerCase().includes(filtro.toLowerCase())
);
```

---

## Paso 3: Formulario de Registro e Inmutabilidad (35 min)

Manejo de inputs para registrar un nuevo curso manteniendo la pureza de datos:

```jsx
// Fragmento en App.jsx
const [nuevoNombre, setNuevoNombre] = useState('');
const [nuevosCreditos, setNuevosCreditos] = useState(3);
const [nuevoDepto, setNuevoDepto] = useState('');

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

  // Se crea un arreglo nuevo con el nuevo curso al inicio
  setCursos((prevCursos) => [nuevo, ...prevCursos]);

  // Limpiar campos
  setNuevoNombre('');
  setNuevoDepto('');
  setNuevosCreditos(3);
};
```

---

## Paso 4: Ciclo de Vida y Asincronía con `useEffect` (30 min)

Simular la latencia de un servicio REST backend para enseñar el patrón clásico de 3 estados (`data`, `isLoading`, `error`):

```jsx
// Fragmento en App.jsx
const [cursos, setCursos] = useState([]);
const [isLoading, setIsLoading] = useState(true);
const [error, setError] = useState(null);

useEffect(() => {
  console.log("Montando componente: Simulando petición HTTP...");

  const timer = setTimeout(() => {
    // Simulamos respuesta exitosa desde los datos mock
    setCursos(MOCK_CURSOS);
    setIsLoading(false);
  }, 1200);

  // Limpieza al desmontar
  return () => clearTimeout(timer);
}, []); // Se ejecuta únicamente una vez al montar
```

---

## Paso 5: Código Completo Ensamblado de `src/App.jsx` (15 min)

```jsx
// src/App.jsx — Estado final al terminar la Clase 2
import { useState, useEffect } from 'react';
import Navbar from './components/Navbar';
import CursoList from './components/CursoList';
import CursoFiltro from './components/CursoFiltro';
import { MOCK_CURSOS } from './data/mockCursos';

export default function App() {
  const [cursos, setCursos] = useState([]);
  const [filtro, setFiltro] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Formulario de nuevo curso
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [nuevosCreditos, setNuevosCreditos] = useState(3);
  const [nuevoDepto, setNuevoDepto] = useState('');

  useEffect(() => {
    const timer = setTimeout(() => {
      setCursos(MOCK_CURSOS);
      setIsLoading(false);
    }, 1200);

    return () => clearTimeout(timer);
  }, []);

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

  const cursosFiltrados = cursos.filter((c) =>
    c.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    c.departamento.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div>
      <Navbar />
      <main className="container">
        <h1>Catálogo de Asignaturas</h1>

        <form onSubmit={handleAgregarCurso} className="form-curso" style={{ marginBottom: '1.5rem' }}>
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

        <CursoFiltro filtro={filtro} onFiltroChange={setFiltro} />

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

        {!isLoading && !error && <CursoList cursos={cursosFiltrados} />}
      </main>
    </div>
  );
}
```

---

## Checklist de Verificación al Finalizar la Clase 2

- [ ] Al cargar la página, se muestra el indicador visual de carga durante 1.2 segundos y luego aparecen los cursos.
- [ ] Al escribir en el buscador, la lista se filtra inmediatamente sin recargar la página.
- [ ] Al enviar el formulario de registro, el nuevo curso se inserta al inicio de la lista y se visualiza al instante.
- [ ] El arreglo de cursos se actualiza sin mutación directa (`.push()`).

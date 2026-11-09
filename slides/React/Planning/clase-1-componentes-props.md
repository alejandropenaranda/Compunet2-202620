# Hoja de Ruta Práctica — Clase 1: De Thymeleaf a Componentes y Props

> **Curso:** Computación en Internet II (Universidad Icesi)  
> **Duración:** 2 horas  
> **Tema:** Inicialización con Vite, JSX, Maquetación con Componentes Funcionales y Flujo de Datos con Props.

---

## Aspectos Clave a Tener en Cuenta

1. **JSX no es HTML:** Es una extensión sintáctica de JavaScript.
   - Usar `className` en lugar de `class`.
   - Toda etiqueta debe cerrarse explícitamente (`<input />`, `<img />`).
   - Todo componente debe retornar un único contenedor raíz (o un Fragment `<>...</>`).
2. **Inmutabilidad de Props:** Las props fluyen unidireccionalmente (de padre a hijo) y son de **solo lectura**. El hijo nunca debe mutar sus props.
3. **Equivalencia Conceptual:**
   - Thymeleaf: `<tr th:each="c : ${cursos}">` (el servidor procesa el template y retorna HTML estático).
   - React: `{cursos.map((c) => <CursoCard key={c.id} curso={c} />)}` (el navegador ejecuta la función JS y actualiza el DOM dinámicamente).
4. **Prop `key` Obligatoria:** En todo renderizado de listas (`.map`), cada elemento debe incluir una propiedad `key` única y estable para que el algoritmo de reconciliación de React rastree los nodos.

---

## Paso 1: Inicialización del Proyecto (10 min)

Ejecutar en terminal:

```bash
npm create vite@latest frontend-cursos -- --template react
cd frontend-cursos
npm install
npm run dev
```

---

## Paso 2: Estilos Base Ultraligeros (5 min)

Reemplazar completamente el archivo `src/index.css`. Se copia una sola vez y no requiere tocarse durante la clase:

```css
/* src/index.css */
:root {
  --primary: #002855;
  --accent: #5454E9;
  --success: #28a745;
  --danger: #dc3545;
  --bg-light: #f8fafc;
  --border: #e2e8f0;
  --text: #1e293b;
  --text-muted: #64748b;
}

body {
  margin: 0;
  font-family: system-ui, -apple-system, sans-serif;
  background-color: var(--bg-light);
  color: var(--text);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 1.5rem;
}

.navbar {
  background: var(--primary);
  color: #fff;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar h2 {
  margin: 0;
  font-size: 1.25rem;
}

.grid-cursos {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}

.card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 1rem 1.25rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.card h3 {
  margin: 0 0 0.5rem 0;
  color: var(--primary);
}

.card-subtitle {
  margin: 0.25rem 0;
  color: var(--text-muted);
  font-size: 0.9rem;
}

.card-profesor {
  margin: 0.5rem 0;
  font-size: 0.9rem;
  color: var(--text);
}

.btn {
  display: inline-block;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  text-decoration: none;
}

.btn-primary { background: var(--accent); color: #fff; }
.btn-success { background: var(--success); color: #fff; }
.btn-danger { background: var(--danger); color: #fff; }
.btn-link { background: transparent; color: var(--accent); padding-left: 0; }

.input-search {
  width: 100%;
  padding: 0.6rem 0.8rem;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

.form-curso {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 450px;
  background: #fff;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid var(--border);
}

.form-curso input {
  padding: 0.6rem 0.8rem;
  border: 1px solid var(--border);
  border-radius: 4px;
}

.status-box {
  padding: 0.75rem 1rem;
  border-radius: 6px;
  margin-bottom: 1rem;
}

.status-loading { background: #e0f2fe; color: #0369a1; }
.status-error { background: #fee2e2; color: #b91c1c; }
```

---

## Paso 3: Datos Mock Iniciales (5 min)

Crear `src/data/mockCursos.js` simulando la respuesta del backend Spring Boot:

```javascript
// src/data/mockCursos.js
export const MOCK_CURSOS = [
  {
    id: 1,
    nombre: "Computación en Internet II",
    creditos: 3,
    departamento: "Computación y Sistemas Inteligentes",
    profesor: "Kevin Rodriguez"
  },
  {
    id: 2,
    nombre: "Bases de Datos",
    creditos: 3,
    departamento: "Computación y Sistemas Inteligentes",
    profesor: "Claudia Martinez"
  },
  {
    id: 3,
    nombre: "Sistemas Distribuidos",
    creditos: 4,
    departamento: "TIC",
    profesor: "Andres Perez"
  }
];
```

---

## Paso 4: Componentes de Interfaz (45 min)

### 4.1 Navbar (`src/components/Navbar.jsx`)
Migración del layout base de Thymeleaf (`fragments/layout.html`):

```jsx
// src/components/Navbar.jsx
export default function Navbar() {
  return (
    <nav className="navbar">
      <h2>Portal Cursos Icesi</h2>
      <div>
        <span style={{ marginRight: '1rem' }}>Docente Activo</span>
        <button className="btn btn-danger">Salir</button>
      </div>
    </nav>
  );
}
```

### 4.2 Tarjeta de Curso (`src/components/CursoCard.jsx`)
Componente presentacional con destructuración de props:

```jsx
// src/components/CursoCard.jsx
export default function CursoCard({ curso }) {
  const { nombre, creditos, departamento, profesor } = curso;

  return (
    <div className="card">
      <h3>{nombre}</h3>
      <p className="card-subtitle">
        <strong>Créditos:</strong> {creditos} · <strong>Depto:</strong> {departamento}
      </p>
      <p className="card-profesor">
        <strong>Docente:</strong> {profesor || 'Sin profesor asignado'}
      </p>
      <div style={{ marginTop: '0.8rem' }}>
        <button className="btn btn-primary">Ver Detalle</button>
      </div>
    </div>
  );
}
```

### 4.3 Lista de Cursos (`src/components/CursoList.jsx`)
Mapeo de la colección con validación de estado vacío:

```jsx
// src/components/CursoList.jsx
import CursoCard from './CursoCard';

export default function CursoList({ cursos }) {
  if (!cursos || cursos.length === 0) {
    return <p className="card-subtitle">No hay cursos registrados en el catálogo.</p>;
  }

  return (
    <div className="grid-cursos">
      {cursos.map((c) => (
        <CursoCard key={c.id} curso={c} />
      ))}
    </div>
  );
}
```

---

## Paso 5: Ensamblado en `src/App.jsx` (15 min)

```jsx
// src/App.jsx
import Navbar from './components/Navbar';
import CursoList from './components/CursoList';
import { MOCK_CURSOS } from './data/mockCursos';

export default function App() {
  return (
    <div>
      <Navbar />
      <main className="container">
        <h1>Catálogo de Asignaturas</h1>
        <CursoList cursos={MOCK_CURSOS} />
      </main>
    </div>
  );
}
```

---

## Checklist de Verificación al Finalizar la Clase 1

- [ ] La aplicación compila sin errores en Vite (`http://localhost:5173`).
- [ ] La barra de navegación superior renderiza correctamente con los colores de Icesi.
- [ ] La lista de 3 cursos se despliega en una cuadrícula (`grid-cursos`) usando `mockCursos.js`.
- [ ] La consola del navegador (`F12`) está libre de advertencias sobre `key` ausente.

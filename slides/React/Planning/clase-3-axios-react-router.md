# Hoja de Ruta Práctica — Clase 3: Consumo de APIs (Axios) y Navegación SPA (React Router)

> **Curso:** Computación en Internet II (Universidad Icesi)  
> **Duración:** 2 horas  
> **Tema:** Cliente HTTP Centralizado (`axios`), Interceptores JWT, Enrutamiento SPA (`react-router-dom`), Rutas Dinámicas (`useParams`) y Navegación Imperativa (`useNavigate`).

---

## Aspectos Clave a Tener en Cuenta

1. **CORS en el Backend Spring Boot:**
   - Para que Vite (`http://localhost:5173`) pueda consumir Spring Boot (`http://localhost:8080`), el backend debe autorizar el origen con `@CrossOrigin(origins = "http://localhost:5173")` o en `SecurityConfig.java`.
2. **Cliente Centralizado:**
   - No instanciar `axios.get("http://localhost:8080/api/v1/cursos")` disperso en cada componente.
   - Definir una instancia en `api/axiosConfig.js` con `baseURL` para facilitar mantenimiento y cambios de entorno.
3. **Interceptores de Axios:**
   - Permiten interceptar cada solicitud saliente para inyectar automáticamente el header `Authorization: Bearer <token>` desde `localStorage`.
4. **Navegación sin Recarga (`<Link>` vs `<a>`):**
   - Nunca usar etiquetas `<a href="...">` para rutas internas. Provocan la recarga completa del navegador y destruyen el estado en memoria de la SPA.
   - Usar siempre `<Link to="...">` de `react-router-dom`.
5. **Hooks de Enrutamiento:**
   - `useParams()`: Extrae los parámetros variables de la URL (ej. el `:id` en `/cursos/:id`).
   - `useNavigate()`: Permite redirecciones programadas por código tras completar una acción (ej. redirigir a `/cursos` luego de un POST exitoso).

---

## Paso 1: Instalación de Dependencias (5 min)

```bash
npm install axios react-router-dom
```

---

## Paso 2: Cliente HTTP Centralizado (`src/api/axiosConfig.js`) (15 min)

```javascript
// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para inyectar el token JWT si existe en la sesion
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

---

## Paso 3: Configurar Enrutador en `src/main.jsx` (5 min)

Envolver la aplicación con `<BrowserRouter>`:

```jsx
// src/main.jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
```

---

## Paso 4: Páginas Modulares de la Aplicación (55 min)

### 4.1 Vista de Catálogo (`src/pages/CursosPage.jsx`)
Reemplaza la simulación por consumo REST real con Axios:

```jsx
// src/pages/CursosPage.jsx
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import CursoList from '../components/CursoList';
import CursoFiltro from '../components/CursoFiltro';

export default function CursosPage() {
  const [cursos, setCursos] = useState([]);
  const [filtro, setFiltro] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setIsLoading(true);
    api.get('/cursos')
      .then((res) => {
        setCursos(res.data);
        setError(null);
      })
      .catch((err) => {
        console.error(err);
        setError('No se pudo conectar con el servicio de Cursos');
      })
      .finally(() => setIsLoading(false));
  }, []);

  const cursosFiltrados = cursos.filter((c) =>
    c.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    c.departamento.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h1>Catálogo de Asignaturas</h1>
        <Link to="/cursos/nuevo" className="btn btn-success">
          Nuevo Curso
        </Link>
      </div>

      <CursoFiltro filtro={filtro} onFiltroChange={setFiltro} />

      {isLoading && (
        <div className="status-box status-loading">
          Consultando backend Spring Boot (/api/v1/cursos)...
        </div>
      )}

      {error && (
        <div className="status-box status-error">
          Error: {error}
        </div>
      )}

      {!isLoading && !error && <CursoList cursos={cursosFiltrados} />}
    </div>
  );
}
```

### 4.2 Vista de Detalle por ID (`src/pages/CursoDetallePage.jsx`)
Uso del hook `useParams()` para consultar el recurso individual:

```jsx
// src/pages/CursoDetallePage.jsx
import { useParams, Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

export default function CursoDetallePage() {
  const { id } = useParams();
  const [curso, setCurso] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get(`/cursos/${id}`)
      .then((res) => setCurso(res.data))
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="status-box status-loading">Cargando detalle del curso #{id}...</div>;
  if (!curso) return <div className="status-box status-error">Curso no encontrado.</div>;

  return (
    <div>
      <Link to="/cursos" className="btn btn-link">Volver al Catálogo</Link>
      <h1>{curso.nombre}</h1>
      <p><strong>Créditos:</strong> {curso.creditos}</p>
      <p><strong>Departamento:</strong> {curso.departamento}</p>
      <div className="card" style={{ marginTop: '1rem' }}>
        <h4>Docente Titular</h4>
        <p>{curso.profesorNombreCompleto || curso.profesor || 'Docente Asignado'}</p>
        <p className="card-subtitle">{curso.profesorCorreo || 'docente@icesi.edu.co'}</p>
      </div>
    </div>
  );
}
```

### 4.3 Formulario con Redirección (`src/pages/CursoFormPage.jsx`)
Envío HTTP POST y redirección imperativa con `useNavigate()`:

```jsx
// src/pages/CursoFormPage.jsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';

export default function CursoFormPage() {
  const navigate = useNavigate();
  const [nombre, setNombre] = useState('');
  const [creditos, setCreditos] = useState(3);
  const [departamento, setDepartamento] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post('/cursos', {
        nombre,
        creditos: Number(creditos),
        departamento,
        profesorId: 1
      });
      // Redirección imperativa al catálogo tras guardar exitosamente
      navigate('/cursos');
    } catch (err) {
      alert('Error al guardar el curso en el servidor');
    }
  };

  return (
    <div>
      <Link to="/cursos" className="btn btn-link">Volver al Catálogo</Link>
      <form onSubmit={handleSubmit} className="form-curso" style={{ marginTop: '1rem' }}>
        <h2>Registrar Nuevo Curso</h2>
        <input
          placeholder="Nombre de la asignatura"
          value={nombre}
          onChange={(e) => setNombre(e.target.value)}
          required
        />
        <input
          type="number"
          placeholder="Créditos"
          value={creditos}
          onChange={(e) => setCreditos(e.target.value)}
          required
        />
        <input
          placeholder="Departamento (ej. TIC)"
          value={departamento}
          onChange={(e) => setDepartamento(e.target.value)}
          required
        />
        <button type="submit" className="btn btn-success">
          Guardar en Backend
        </button>
      </form>
    </div>
  );
}
```

---

## Paso 5: Actualizar `src/components/CursoCard.jsx` (10 min)

Convertir el botón de acción en un `<Link>` navegable hacia la ruta dinámica:

```jsx
// src/components/CursoCard.jsx
import { Link } from 'react-router-dom';

export default function CursoCard({ curso }) {
  const { id, nombre, creditos, departamento, profesor } = curso;

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
        <Link to={`/cursos/${id}`} className="btn btn-primary">
          Ver Detalle
        </Link>
      </div>
    </div>
  );
}
```

---

## Paso 6: Configurar Rutas en `src/App.jsx` (15 min)

```jsx
// src/App.jsx — Enrutador global de la SPA
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import CursosPage from './pages/CursosPage';
import CursoDetallePage from './pages/CursoDetallePage';
import CursoFormPage from './pages/CursoFormPage';

export default function App() {
  return (
    <div>
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/" element={<CursosPage />} />
          <Route path="/cursos" element={<CursosPage />} />
          <Route path="/cursos/nuevo" element={<CursoFormPage />} />
          <Route path="/cursos/:id" element={<CursoDetallePage />} />
          <Route path="*" element={<h2>404: Página no encontrada</h2>} />
        </Routes>
      </main>
    </div>
  );
}
```

---

## Checklist de Verificación al Finalizar la Clase 3

- [ ] La aplicación realiza la petición GET a `http://localhost:8080/api/v1/cursos` y renderiza los datos reales de la base de datos sin errores de CORS.
- [ ] Al hacer clic en "Ver Detalle", la URL cambia a `/cursos/:id` y carga la información de ese curso específico sin recarga completa de página.
- [ ] Al hacer clic en "Nuevo Curso", navega a `/cursos/nuevo`, el formulario envía un HTTP POST y tras la respuesta redirige automáticamente a `/cursos` mostrando el nuevo registro persistido.

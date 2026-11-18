# Hoja de Ruta Práctica — Clase 3: React Router (Modo Data) + Axios y Auth JWT

## Parte 1: React Router (Modo Data) y Páginas con `MOCKDATA` (55 min)

#### Paso 1.1: Instalación de dependencias y estilos de Login en `src/index.css` (3 min)
```bash
npm install react-router axios
```
- En `src/components/Navbar.jsx`: quitar `style={{marginRight: '1rem'}}` del `<span>` (`.navbar-user` ya usa `gap: 1rem`).
- Agregar al final de `src/index.css` los estilos para centrar vertical y horizontalmente el Login sin Navbar:

#### Paso 1.2: Layout Raíz con `<Outlet />` (`src/pages/Root.jsx`) (4 min)
```jsx
// src/pages/Root.jsx
import { Outlet } from 'react-router';
import Navbar from '../components/Navbar';

export default function Root() {
  return (
    <div>
      <Navbar />
      <main className="container">
        <Outlet />
      </main>
    </div>
  );
}
```

#### Paso 1.3: Mover el catálogo actual de `App.jsx` a `src/pages/CursosPage.jsx` (6 min)
*(Sigue usando `useCursos()` con `MOCKDATA` por ahora; se corrige `className='status-box status-loading'`):*

```jsx
// src/pages/CursosPage.jsx
import { useState } from 'react';
import CursosGrid from '../components/CursosGrid';
import CursosFiltro from '../components/CursosFiltro';
import useCursos from '../hooks/useCursos';

export default function CursosPage() {
  const { cursos, isLoading } = useCursos();
  const [cursoSeleccionado, setCursoSeleccionado] = useState(null);
  const [filtro, setFiltro] = useState('');

  const cursosFiltrados = cursos.filter((c) =>
    c.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    c.departamento.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div>
      <h1>Catalogo de cursos</h1>

      {cursoSeleccionado && (
        <div className="status-box status-loading banner-seleccion">
          <span>
            Asignatura seleccionada para seguimiento: <strong>{cursoSeleccionado}</strong>
          </span>
          <button className="btn-close-banner" onClick={() => setCursoSeleccionado(null)}>
            Cerrar
          </button>
        </div>
      )}

      {isLoading && (
        <div className="status-box status-loading">
          Cargando cursos desde el servidor...
        </div>
      )}

      {!isLoading && (
        <>
          <CursosFiltro filtro={filtro} onFiltroChange={setFiltro} />
          <CursosGrid
            cursos={cursosFiltrados}
            onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
          />
        </>
      )}
    </div>
  );
}
```

#### Paso 1.4: Login inicial y Detalle con `useParams` sobre `MOCKDATA` (12 min)
Probamos la navegación entre rutas antes de conectar Axios:

```jsx
// src/pages/LoginPage.jsx (version inicial enfocada en useNavigate)
import { useState } from 'react';
import { useNavigate } from 'react-router';

export default function LoginPage() {
  const navigate = useNavigate();
  const [correoInstitucional, setCorreoInstitucional] = useState('drincon@icesi.edu.co');
  const [password, setPassword] = useState('profesor123');

  const handleLogin = (e) => {
    e.preventDefault();
    // En la Parte 2 reemplazaremos esto por api.post('/auth/login', ...)
    navigate('/cursos', { replace: true });
  };

  return (
    <div className="login-page">
      <form onSubmit={handleLogin} className="form-curso login-card">
        <h2>Iniciar Sesion</h2>
        <input
          type="email"
          placeholder="Correo institucional"
          value={correoInstitucional}
          onChange={(e) => setCorreoInstitucional(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Contrasena"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit" className="btn btn-primary">
          Ingresar
        </button>
      </form>
    </div>
  );
}
```

```jsx
// src/pages/CursoDetallePage.jsx (version inicial leyendo :id desde MOCKDATA)
import { useParams, Link } from 'react-router';
import { MOCKDATA } from '../data/MockData';

export default function CursoDetallePage() {
  const { id } = useParams();
  // En la Parte 2 reemplazaremos esto por useEffect + api.get('/cursos/' + id)
  const curso = MOCKDATA.find((c) => c.id === Number(id));

  if (!curso) {
    return (
      <div>
        <Link to="/cursos" className="btn btn-link">Volver al Catalogo</Link>
        <div className="status-box status-error">Curso #{id} no encontrado.</div>
      </div>
    );
  }

  return (
    <div>
      <Link to="/cursos" className="btn btn-link">Volver al Catalogo</Link>
      <h1>{curso.nombre}</h1>
      <p><strong>Creditos:</strong> {curso.creditos}</p>
      <p><strong>Departamento:</strong> {curso.departamento}</p>
      <div className="card card-detalle">
        <h4>Docente Titular</h4>
        <p>{curso.profesor || 'Docente Asignado'}</p>
      </div>
    </div>
  );
}
```

#### Paso 1.5: Enlace `<Link>` en `src/components/CursosCard.jsx` (3 min)
```jsx
// src/components/CursosCard.jsx
import { Link } from 'react-router';

function CursosCard({ curso, destacado = false, onSeleccionar }) {
  const { id, nombre, creditos, departamento, profesor, profesorNombre, profesorApellido } = curso;
  const nombreDocente = [profesorNombre, profesorApellido].filter(Boolean).join(' ') || profesor;

  return (
    <div className={`card ${destacado ? 'card-destacado' : ''}`}>
      <div className="card-top-row">
        <h3>{nombre}</h3>
        {destacado && <span className="badge-troncal">Troncal</span>}
      </div>
      <p className="card-subtitle">
        <strong>Creditos:</strong> {creditos} · <strong>Depto:</strong> {departamento}
      </p>
      <p className="card-profesor">
        <strong>Docente:</strong> {nombreDocente || 'Sin profesor asignado'}
      </p>
      <div className="card-actions">
        <button
          className="btn btn-primary"
          onClick={() => onSeleccionar && onSeleccionar(nombre)}
        >
          Seleccionar
        </button>
        <Link to={`/cursos/${id}`} className="btn btn-link">
          Ver Detalle
        </Link>
      </div>
    </div>
  );
}

export default CursosCard;
```

#### Paso 1.6: Crear `src/router.jsx` y conectar `RouterProvider` en `src/main.jsx` (7 min)
```jsx
// src/router.jsx
import { createBrowserRouter } from 'react-router';
import Root from './pages/Root';
import LoginPage from './pages/LoginPage';
import CursosPage from './pages/CursosPage';
import CursoDetallePage from './pages/CursoDetallePage';

export const router = createBrowserRouter([
  { path: '/', Component: LoginPage },
  {
    path: '/cursos',
    Component: Root,
    children: [
      { index: true, Component: CursosPage },
      { path: ':id', Component: CursoDetallePage },
    ],
  },
  { path: '*', Component: () => <h2>404: Pagina no encontrada</h2> },
]);
```

```jsx
// src/main.jsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router/dom';
import './index.css';
import { router } from './router';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
```

> [!TIP]
> **Verificación Intermedia (Fin Parte 1):**  
> Probar en el navegador: `/` $\to$ clic en *Ingresar* $\to$ `/cursos` $\to$ clic en *Ver Detalle* $\to$ `/cursos/1` $\to$ *Volver al Catalogo* $\to$ URL inválida (`/xyz` muestra 404). Todo sin recargar la página.

---

## Parte 2: Axios, Interceptores JWT, CORS e Integración Real (65 min)

### 2.1 Bloque Teórico en Diapositivas (25 min)
> [!IMPORTANT]
> **Proyectar y Explicar:** `slides/React/Material/semana-15-16-router-axios.pdf` — **Diapositivas 52 a 111**  
> - Axios vs `fetch` y patrón `GET` en 3 estados (`cursos`, `isLoading`, `error`) (Slides 52–76).
> - `localStorage`, `.env` en Vite y cliente centralizado `axios.create` (Slides 77–90).
> - Interceptor de Request (inyectar `Authorization: Bearer`), `POST` login, e Interceptor de Response (`401`) (Slides 91–108).
> - Puente CORS en Spring Boot (`SecurityConfig.java` `@Order(1)` + `OPTIONS`) y Resumen (Slides 109–111).

### 2.2 Práctica en Vivo: Conectar Todo al Backend (40 min)

#### Paso 2.1 (En Backend): Habilitar CORS en `SecurityConfig.java` (5 min)
En `src/main/java/com/compunet/springboot/config/SecurityConfig.java` (`@Order(1)`):

```java
@Bean
@Order(1)
public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
        .securityMatcher("/api/**")
        .cors(Customizer.withDefaults()) // <-- 1. Activar CORS
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(authz -> authz
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // <-- 2. Preflight sin JWT
            .requestMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        // ... resto de exceptionHandling y addFilterBefore igual ...
        .build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

#### Paso 2.2: Crear `src/api/axiosConfig.js` (7 min)
```
VITE_API_URL=http://localhost:8081/springboot-app/api/v1
```

```javascript
// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor de request: lee el token dinamicamente en cada peticion
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
```

#### Paso 2.3: Conectar `LoginPage.jsx` (`POST /auth/login`) y `Navbar.jsx` (8 min)
```jsx
// src/pages/LoginPage.jsx (actualizado con Axios)
import { useState } from 'react';
import { useNavigate } from 'react-router';
import api from '../api/axiosConfig';

export default function LoginPage() {
  const navigate = useNavigate();
  const [correoInstitucional, setCorreoInstitucional] = useState('drincon@icesi.edu.co');
  const [password, setPassword] = useState('profesor123');
  const [error, setError] = useState(null);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const res = await api.post('/auth/login', { correoInstitucional, password });
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('correo', res.data.correoInstitucional);
      navigate('/cursos', { replace: true });
    } catch (err) {
      console.error(err);
      setError('Credenciales invalidas o servidor no disponible');
    }
  };

  return (
    <div className="login-page">
      <form onSubmit={handleLogin} className="form-curso login-card">
        <h2>Iniciar Sesion</h2>
        {error && <div className="status-box status-error">{error}</div>}
        <input
          type="email"
          placeholder="Correo institucional"
          value={correoInstitucional}
          onChange={(e) => setCorreoInstitucional(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Contrasena"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit" className="btn btn-primary">
          Ingresar
        </button>
      </form>
    </div>
  );
}
```

```jsx
// src/components/Navbar.jsx (con logout)
import { useNavigate } from 'react-router';

function Navbar() {
  const navigate = useNavigate();
  const correo = localStorage.getItem('correo');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('correo');
    navigate('/', { replace: true });
  };

  return (
    <nav className="navbar">
      <h2>Portal Cursos Icesi</h2>
      <div className="navbar-user">
        <span>{correo || 'Invitado'}</span>
        {correo && (
          <button className="btn btn-danger" onClick={handleLogout}>
            Salir
          </button>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
```

#### Paso 2.4: Migrar `useCursos.js` y `CursosPage.jsx` a `GET /cursos` (10 min)
```javascript
// src/hooks/useCursos.js (reemplaza MOCKDATA por Axios + estado error)
import { useEffect, useState } from 'react';
import api from '../api/axiosConfig';

function useCursos() {
  const [cursos, setCursos] = useState([]);
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
        setError(
          err.response?.status === 401
            ? 'No autenticado. Inicia sesion para ver los cursos.'
            : 'No se pudo conectar con el servicio de Cursos'
        );
      })
      .finally(() => setIsLoading(false));
  }, []);

  return { cursos, isLoading, error };
}

export default useCursos;
```

En `src/pages/CursosPage.jsx`, desestructurar `error` de `useCursos()` y agregar el bloque de error:
```jsx
// En src/pages/CursosPage.jsx:
// 1. import { Link } from 'react-router';
// 2. const { cursos, isLoading, error } = useCursos();
// 3. Agregar debajo del bloque isLoading y ajustar condicion de la grilla:
{error && (
  <div className="status-box status-error">
    {error} <Link to="/" className="btn btn-link">Ir al Login</Link>
  </div>
)}

{!isLoading && !error && (
  <>
    <CursosFiltro filtro={filtro} onFiltroChange={setFiltro} />
    <CursosGrid
      cursos={cursosFiltrados}
      onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
    />
  </>
)}
```

#### Paso 2.5: Conectar `CursoDetallePage.jsx` a `GET /cursos/:id` (10 min)
Reemplazamos `MOCKDATA.find(...)` por `useEffect` + `api.get('/cursos/' + id)`:

```jsx
// src/pages/CursoDetallePage.jsx (actualizado con Axios)
import { useParams, Link } from 'react-router';
import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

export default function CursoDetallePage() {
  const { id } = useParams();
  const [curso, setCurso] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    api.get(`/cursos/${id}`)
      .then((res) => {
        setCurso(res.data);
        setError(null);
      })
      .catch((err) => {
        console.error(err);
        setError('No se pudo cargar el detalle del curso');
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="status-box status-loading">Cargando detalle del curso #{id}...</div>;
  if (error || !curso) return <div className="status-box status-error">{error || 'Curso no encontrado.'}</div>;

  return (
    <div>
      <Link to="/cursos" className="btn btn-link">Volver al Catalogo</Link>
      <h1>{curso.nombre}</h1>
      <p><strong>Creditos:</strong> {curso.creditos}</p>
      <p><strong>Departamento:</strong> {curso.departamento}</p>
      <div className="card card-detalle">
        <h4>Docente Titular</h4>
        <p>{`${curso.profesorNombre || ''} ${curso.profesorApellido || ''}`.trim() || 'Docente Asignado'}</p>
        <p className="card-subtitle">
          {curso.profesorCorreo || 'Sin correo'} · {curso.profesorDepartamento || 'No especificado'}
        </p>
      </div>
    </div>
  );
}
```

---

## Checklist de Verificación al Finalizar la Clase 3

- [ ] **Parte 1 (Router):** Navegación completa entre `/`, `/cursos`, `/cursos/:id` y `*` usando `createBrowserRouter`, `<Outlet />`, `<Link>`, `useNavigate` y `useParams` sin recargar el navegador.
- [ ] **Parte 2 (CORS + Axios + JWT):** Login exitoso contra `POST /api/v1/auth/login` en `:8081/springboot-app`, token guardado en `localStorage`, catálogo cargado desde BD con `GET /api/v1/cursos` y detalle individual con `GET /api/v1/cursos/:id`.
- [ ] **Cero Estilos Inline / Cero Emojis:** Todos los estilos provienen de `src/index.css`.

---
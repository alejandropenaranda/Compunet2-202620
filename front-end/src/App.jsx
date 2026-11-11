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

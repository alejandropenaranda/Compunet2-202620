// src/pages/CursosPage.jsx
import { useState } from 'react';
import CursoList from '../components/CursoList';
import CursoFiltro from '../components/CursoFiltro';
import useCursos from '../hooks/useCursos';
import { Link } from 'react-router';

export default function CursosPage() {
    const { cursos, isLoading, error } = useCursos();
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

            {error && (
                <div className="status-box status-error">
                    {error} <Link to="/" className="btn btn-link">Ir al Login</Link>
                </div>
            )}

            {!isLoading && !error && (
                <>
                    <CursoFiltro filtro={filtro} onFiltroChange={setFiltro} />
                    <CursoList
                        cursos={cursosFiltrados}
                        onSeleccionarCurso={(nombre) => setCursoSeleccionado(nombre)}
                    />
                </>
            )}
        </div>
    );
}

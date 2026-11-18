// src/pages/CursoDetallePage.jsx (version inicial leyendo :id desde mockCursos)
import { useParams, Link } from 'react-router';
import { useEffect, useState } from 'react';
import api from '../api/axiosConfig';

export default function CursoDetallePage() {
    const { id } = useParams();
    const [curso, setCurso] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    // En la Parte 2 reemplazaremos esto por useEffect + api.get('/cursos/' + id)
    // const curso = MOCK_CURSOS.find((c) => c.id === Number(id));

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
                <p>{[curso.profesorNombre, curso.profesorApellido].filter(Boolean).join(' ') || curso.profesor || 'Docente Asignado'}</p>
                <p className="card-subtitle">
                    {curso.profesorCorreo || 'Sin correo'} · {curso.profesorDepartamento || 'No especificado'}
                </p>
            </div>
        </div>
    );
}

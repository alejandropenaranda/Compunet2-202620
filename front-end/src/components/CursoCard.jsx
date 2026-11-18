// src/components/CursoCard.jsx
import { Link } from 'react-router';

// >>> [MODIFICADO]: Se agregan las props 'destacado' (default false) y 'onSeleccionar' (callback)
export default function CursoCard({ curso, destacado = false, onSeleccionar }) {
  const { id, nombre, creditos, departamento, profesor, profesorNombre, profesorApellido } = curso;
  const nombreDocente = [profesorNombre, profesorApellido].filter(Boolean).join(' ') || profesor;

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
        <strong>Docente:</strong> {nombreDocente || 'Sin profesor asignado'}
      </p>

      <div className="card-actions">
        {/* >>> [MODIFICADO]: El botón ahora delega el evento al padre ejecutando onSeleccionar */}
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

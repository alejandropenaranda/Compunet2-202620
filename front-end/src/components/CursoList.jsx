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

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

// src/components/CursoFiltro.jsx
export default function CursoFiltro({ filtro, onFiltroChange }) {
    return (
        <div className="filtro-container">
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

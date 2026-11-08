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

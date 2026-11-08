import Navbar from './components/Navbar';
import CursoList from './components/CursoList';
import { MOCK_CURSOS } from './data/mockCursos';

export default function App() {
  return (
    <div>
      <Navbar />
      <main className="container">
        <h1>Catálogo de Asignaturas</h1>
        <CursoList cursos={MOCK_CURSOS} />
      </main>
    </div>
  );
}

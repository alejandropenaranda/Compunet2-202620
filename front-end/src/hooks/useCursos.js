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
  }, [])

  return { cursos, isLoading, error };
}

export default useCursos;

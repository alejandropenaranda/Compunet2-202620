// src/router.jsx
import { createBrowserRouter } from 'react-router';
import Root from './pages/Root';
import LoginPage from './pages/LoginPage';
import CursosPage from './pages/CursosPage';
import CursoDetallePage from './pages/CursoDetallePage';

export const router = createBrowserRouter([
    { path: '/', Component: LoginPage },
    {
        path: '/cursos',
        Component: Root,
        children: [
            { index: true, Component: CursosPage },
            { path: ':id', Component: CursoDetallePage },
        ],
    },
    { path: '*', Component: () => <h2>404: Pagina no encontrada</h2> },
]);

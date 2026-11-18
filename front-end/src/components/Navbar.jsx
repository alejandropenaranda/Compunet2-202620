import { useNavigate } from 'react-router';

export default function Navbar() {
    const navigate = useNavigate();
    const correo = localStorage.getItem('correo');

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('correo');
        navigate('/', { replace: true });
    };

    return (
        <nav className="navbar">
            <h2>Portal Cursos Icesi</h2>
            <div className="navbar-user">
                <span>{correo || 'Invitado'}</span>
                {correo && (
                    <button className="btn btn-danger" onClick={handleLogout}>
                        Salir
                    </button>
                )}
            </div>
        </nav>
    );
} 
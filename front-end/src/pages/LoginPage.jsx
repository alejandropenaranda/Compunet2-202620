// src/pages/LoginPage.jsx (version inicial enfocada en useNavigate)
import { useState } from 'react';
import { useNavigate } from 'react-router';
import api from '../api/axiosConfig';

export default function LoginPage() {
    const navigate = useNavigate();
    const [correoInstitucional, setCorreoInstitucional] = useState('drincon@icesi.edu.co');
    const [password, setPassword] = useState('profesor123');
    const [error, setError] = useState(null);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            const res = await api.post('/auth/login', { correoInstitucional, password });
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('correo', res.data.correoInstitucional);
            navigate('/cursos', { replace: true });
        } catch (err) {
            console.error(err);
            setError('Credenciales invalidas o servidor no disponible');
        }
    };

    return (
        <div className="login-page">
            <form onSubmit={handleLogin} className="form-curso login-card">
                <h2>Iniciar Sesion</h2>
                {error && <div className="status-box status-error">{error}</div>}
                <input
                    type="email"
                    placeholder="Correo institucional"
                    value={correoInstitucional}
                    onChange={(e) => setCorreoInstitucional(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="Contrasena"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button type="submit" className="btn btn-primary">
                    Ingresar
                </button>
            </form>
        </div>
    );
}

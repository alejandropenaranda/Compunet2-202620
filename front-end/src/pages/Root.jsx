// src/pages/Root.jsx
import { Outlet } from 'react-router';
import Navbar from '../components/Navbar';

export default function Root() {
    return (
        <div>
            <Navbar />
            <main className="container">
                <Outlet />
            </main>
        </div>
    );
}

import { Routes, Route, NavLink } from 'react-router-dom';
import Chambres from './components/Chambres.jsx';
import Reservations from './components/Reservations.jsx';
import Occupations from './components/Occupations.jsx';
import Sejours from './components/Sejours.jsx';
import RechercheDisponibilite from './components/RechercheDisponibilite.jsx';

export default function App() {
  return (
    <div className="app">
      <header className="topbar">
        <h1>🏨 Gestion des Réservations d'Hôtel</h1>
        <nav>
          <NavLink to="/" end>Chambres</NavLink>
          <NavLink to="/reservations">Réservations</NavLink>
          <NavLink to="/occupations">Occupations</NavLink>
          <NavLink to="/sejours">Séjours</NavLink>
          <NavLink to="/disponibilite">Recherche disponibilité</NavLink>
        </nav>
      </header>

      <main className="content">
        <Routes>
          <Route path="/" element={<Chambres />} />
          <Route path="/reservations" element={<Reservations />} />
          <Route path="/occupations" element={<Occupations />} />
          <Route path="/sejours" element={<Sejours />} />
          <Route path="/disponibilite" element={<RechercheDisponibilite />} />
        </Routes>
      </main>
    </div>
  );
}

import { useState } from 'react';
import { api } from '../api.js';

export default function RechercheDisponibilite() {
  const [dateEntree, setDateEntree] = useState('');
  const [nbrJour, setNbrJour] = useState(1);
  const [resultats, setResultats] = useState(null);
  const [msg, setMsg] = useState(null);

  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      const res = await api.getChambresDisponibles(dateEntree, nbrJour);
      setResultats(res);
      setMsg(null);
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  return (
    <div>
      <h2>Recherche d'une chambre libre</h2>
      {msg && <div className={`msg ${msg.type}`}>{msg.text}</div>}

      <div className="card">
        <form className="inline-form" onSubmit={handleSearch}>
          <div className="field">
            <label>Date d'entrée</label>
            <input type="date" value={dateEntree} onChange={e => setDateEntree(e.target.value)} required />
          </div>
          <div className="field">
            <label>Nombre de jours</label>
            <input type="number" min="1" value={nbrJour} onChange={e => setNbrJour(e.target.value)} required />
          </div>
          <button type="submit">Rechercher</button>
        </form>
      </div>

      {resultats && (
        <div className="card">
          <h3>{resultats.length} chambre(s) disponible(s)</h3>
          <table>
            <thead>
              <tr><th>N° Chambre</th><th>Désignation</th><th>Type</th><th>Prix/nuitée</th></tr>
            </thead>
            <tbody>
              {resultats.map(ch => (
                <tr key={ch.numChambre}>
                  <td>{ch.numChambre}</td>
                  <td>{ch.design}</td>
                  <td>{ch.type}</td>
                  <td>{ch.prixNuite} Ar</td>
                </tr>
              ))}
              {resultats.length === 0 && (
                <tr><td colSpan="4">Aucune chambre disponible pour cette période.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

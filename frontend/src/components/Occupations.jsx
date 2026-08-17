import { useEffect, useState } from 'react';
import { api } from '../api.js';

export default function Occupations() {
  const [occupations, setOccupations] = useState([]);
  const [reservationsActives, setReservationsActives] = useState([]);
  const [solde, setSolde] = useState(null);
  const [selectedReserv, setSelectedReserv] = useState('');
  const [msg, setMsg] = useState(null);

  const load = () => {
    api.getOccupations().then(setOccupations);
    api.getReservations().then(list => setReservationsActives(list.filter(r => r.statut === 'ACTIVE')));
    api.getSolde().then(setSolde);
  };

  useEffect(() => { load(); }, []);

  const handleArrivee = async (e) => {
    e.preventDefault();
    if (!selectedReserv) return;
    try {
      await api.createOccupation(Number(selectedReserv));
      setMsg({ type: 'success', text: 'Client enregistré comme arrivé. Le solde a été mis à jour.' });
      setSelectedReserv('');
      load();
    } catch (err) {
      setMsg({ type: 'error', text: err.message });
    }
  };

  return (
    <div>
      <h2>Occupations (arrivées de clients réservés)</h2>
      {solde && <div className="solde-box">Solde actuel : {solde.soldeActuel} Ar</div>}
      {msg && <div className={`msg ${msg.type}`}>{msg.text}</div>}

      <div className="card">
        <form className="inline-form" onSubmit={handleArrivee}>
          <div className="field">
            <label>Réservation active</label>
            <select value={selectedReserv} onChange={e => setSelectedReserv(e.target.value)} required>
              <option value="">-- choisir une réservation --</option>
              {reservationsActives.map(r => (
                <option key={r.idReserv} value={r.idReserv}>
                  #{r.idReserv} - {r.nomClient} - Chambre {r.numChambre} ({r.dateEntree}, {r.nbrJour}j)
                </option>
              ))}
            </select>
          </div>
          <button type="submit">Enregistrer l'arrivée</button>
        </form>
      </div>

      <div className="card">
        <table>
          <thead>
            <tr><th>ID</th><th>Réservation</th><th>Chambre</th><th>Client</th><th>Jours</th><th>Montant</th><th>Date arrivée</th></tr>
          </thead>
          <tbody>
            {occupations.map(o => (
              <tr key={o.idOccup}>
                <td>{o.idOccup}</td>
                <td>#{o.idReserv}</td>
                <td>{o.numChambre}</td>
                <td>{o.nomClient}</td>
                <td>{o.nbrJour}</td>
                <td>{o.montant} Ar</td>
                <td>{o.dateOccup}</td>
              </tr>
            ))}
            {occupations.length === 0 && (
              <tr><td colSpan="7">Aucune occupation enregistrée.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
